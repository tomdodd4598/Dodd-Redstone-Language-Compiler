package drlc.intermediate;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import drlc.Helpers.Pair;
import drlc.Main;
import drlc.intermediate.action.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.data.DataId.RawDataId;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.routine.Routine;

public class IntermediateOptimization {
	
	public static boolean removeNoOps(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			Iterator<Action> iter = list.iterator();
			while (iter.hasNext()) {
				if (iter.next() instanceof NoOpAction) {
					flag = true;
					iter.remove();
				}
			}
		}
		return flag;
	}
	
	public static boolean removeDeadActions(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			int i, size = list.size();
			boolean dead = false;
			for (i = 0; i < size - 1; ++i) {
				if (list.get(i) instanceof IDefiniteRedirectAction) {
					flag = dead = true;
					break;
				}
			}
			if (dead) {
				list.subList(i + 1, size).clear();
			}
		}
		return flag;
	}
	
	public static boolean removeEmptySections(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.body;
		Map<Integer, Integer> sectionIndexMap = new TreeMap<>();
		int count = 0;
		for (int i = 0; i < body.size(); ++i) {
			if (body.get(i).isEmpty()) {
				flag = true;
				sectionIndexMap.put(i + count, count);
				body.remove(i);
				++count;
			}
		}
		Set<Integer> sectionIndexKeys = sectionIndexMap.keySet();
		
		for (List<Action> list : body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IJumpAction jump) {
					int target = jump.getTarget();
					boolean bool = true;
					for (int key : sectionIndexKeys) {
						if (target <= key) {
							bool = false;
							list.set(i, jump.copy(target - sectionIndexMap.get(key)));
							break;
						}
					}
					if (bool) {
						list.set(i, jump.copy(target - count));
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean concatenateSections(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.body;
		Set<Integer> targets = new HashSet<>();
		for (List<Action> list : body) {
			for (Action action : list) {
				if (action instanceof IJumpAction jump) {
					targets.add(jump.getTarget());
				}
			}
		}
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			int j = i + 1;
			while (j < body.size() && !targets.contains(j)) {
				flag = true;
				List<Action> other = body.get(j);
				list.addAll(other);
				other.clear();
				++j;
			}
		}
		return flag;
	}
	
	public static boolean simplifyJumps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.body;
		for (List<Action> list : body) {
			for (int i = 1; i < list.size(); ++i) {
				if (list.get(i) instanceof ConditionalJumpAction cja && list.get(i - 1) instanceof IValueAction iva) {
					if (iva instanceof AssignmentAction) {
						DataId arg = iva.rvalues()[0];
						if (arg instanceof ValueDataId valueDataId) {
							flag = true;
							Value<?> value = valueDataId.value;
							if (value.typeInfo.equals(Main.generator.boolTypeInfo)) {
								boolean noop = value.boolValue(null) ^ cja.jumpCondition;
								list.set(i, noop ? new NoOpAction() : new JumpAction(null, cja.getTarget()));
								list.set(i - 1, new NoOpAction());
							}
							else {
								throw new IllegalArgumentException(String.format("Value \"%s\" can not be used as a conditional! %s", value, iva));
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			if (!list.isEmpty()) {
				int size = list.size();
				if (list.get(size - 1) instanceof IJumpAction jump) {
					if (!jump.isConditional() && jump.getTarget() == i + 1) {
						flag = true;
						list.set(size - 1, new NoOpAction());
					}
				}
			}
		}
		return flag;
	}
	
	private static boolean fillCompressMap(IValueAction iva, int index, boolean lvalues, Map<DataId, Integer> map) {
		boolean added = false;
		for (DataId dataId : iva.dataIds(lvalues)) {
			if (dataId.isCompressable()) {
				if (!dataId.isRepeatable(lvalues) && map.containsKey(dataId)) {
					throw new IllegalArgumentException(String.format("Found unexpected repeated use of data ID %s! %s", dataId, iva));
				}
				else {
					added = true;
					map.put(dataId, index);
				}
			}
		}
		return added;
	}
	
	public static boolean compressRegisters(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			Map<DataId, Integer> lMap = new LinkedHashMap<>(), rMap = new LinkedHashMap<>();
			for (int i = 0; i < list.size(); ++i) {
				Action action = list.get(i);
				if (action instanceof IValueAction iva) {
					fillCompressMap(iva, i, true, lMap);
					fillCompressMap(iva, i, false, rMap);
				}
			}
			
			Function<Boolean, Boolean> compressInternal = lvalues -> {
				boolean internalFlag = false;
				Map<DataId, Integer> otherMap = lvalues ? lMap : rMap;
				for (Entry<DataId, Integer> entry : (lvalues ? rMap : lMap).entrySet()) {
					DataId dataId = entry.getKey();
					int actionIndex = entry.getValue();
					IValueAction action = (IValueAction) list.get(actionIndex);
					if (otherMap.containsKey(dataId)) {
						int otherIndex = otherMap.get(dataId);
						IValueAction other = (IValueAction) list.get(otherIndex);
						if (other.canReplaceDataId(lvalues)) {
							if (action.canRemove(false)) {
								Action replacement = other.replaceDataId(lvalues, dataId, action.getDataIdReplacer(lvalues));
								if (replacement != null) {
									internalFlag = true;
									list.set(actionIndex, new NoOpAction());
									list.set(otherIndex, replacement);
								}
							}
							else if (action.canRemove(true) && other instanceof CompoundAssignmentAction to) {
								internalFlag = true;
								CompoundAssignmentAction from = (CompoundAssignmentAction) action;
								List<DataId> args = new ArrayList<>();
								for (DataId arg : to.args) {
									if (dataId.equals(arg)) {
										args.addAll(from.args);
									}
									else {
										args.add(arg);
									}
								}
								list.set(actionIndex, new NoOpAction());
								list.set(otherIndex, new CompoundAssignmentAction(null, to.target, args));
							}
						}
					}
				}
				return internalFlag;
			};
			
			flag |= compressInternal.apply(false) || compressInternal.apply(true);
		}
		return flag;
	}
	
	public static boolean reorderRvalues(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			for (int i = 1; i < list.size(); ++i) {
				if (list.get(i - 1) instanceof IValueAction lvalAction) {
					DataId[] lvalues = lvalAction.lvalues();
					if (lvalues.length == 1 && list.get(i) instanceof IValueAction rvalAction) {
						DataId lvalue = lvalues[0];
						if (rvalAction.canReorderRvalues()) {
							int index = 0;
							DataId[] rvalues = rvalAction.rvalues();
							for (int k = 0; k < rvalues.length; ++k) {
								if (rvalues[k].equals(lvalue)) {
									index = k;
									break;
								}
							}
							if (index != 0) {
								Action replace = rvalAction.swapRvalues(0, index);
								if (replace != null) {
									flag = true;
									list.set(i, replace);
								}
							}
						}
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean foldRvalues(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IValueAction iva) {
					Action replace = iva.foldRvalues();
					if (replace != null) {
						flag = true;
						list.set(i, replace);
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyBinaryOps(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof BinaryOpAction boa) {
					Action replace = boa.simplify();
					if (replace != null) {
						flag = true;
						list.set(i, replace);
					}
				}
			}
		}
		return flag;
	}
	
	private static void fillReplaceMap(IValueAction iva, int index, boolean lvalues, Map<RawDataId, Pair<RawDataId, Integer>> replacerInfoMap, Map<Integer, Pair<RawDataId, boolean[]>> targetMatchMap) {
		for (DataId dataId : iva.dataIds(lvalues)) {
			RawDataId rawDataId = dataId.raw();
			if (replacerInfoMap.containsKey(rawDataId)) {
				if (dataId.dereferenceLevel > 0) {
					if (iva.canReplaceDataId(lvalues)) {
						Pair<RawDataId, boolean[]> match = targetMatchMap.get(index);
						if (match != null) {
							match.right[lvalues ? 0 : 1] = true;
						}
						else {
							targetMatchMap.put(index, new Pair<>(rawDataId, new boolean[] {lvalues, !lvalues}));
						}
					}
				}
				else {
					replacerInfoMap.remove(rawDataId);
				}
			}
		}
	}
	
	public static <T extends Action & IValueAction> boolean simplifyDereferences(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			Map<RawDataId, Pair<RawDataId, Integer>> replacerInfoMap = new HashMap<>();
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IValueAction iva) {
					if (iva instanceof AssignmentAction) {
						DataId lvalue = iva.lvalues()[0], rvalue = iva.rvalues()[0];
						if (lvalue.typeInfo.isAddress() && !lvalue.isRepeatable(true) && rvalue.dereferenceLevel <= 0) {
							RawDataId rawDeref = lvalue.addDereference(null).raw();
							if (replacerInfoMap.containsKey(rawDeref)) {
								throw new IllegalArgumentException(String.format("Found unexpected repeated use of register %s! %s", lvalue, iva));
							}
							else {
								replacerInfoMap.put(rawDeref, new Pair<>(rvalue.addDereference(null).raw(), i));
							}
						}
					}
				}
			}
			
			Set<Integer> replacerIndices = replacerInfoMap.values().stream().map(x -> x.right).collect(Collectors.toSet());
			Map<Integer, Pair<RawDataId, boolean[]>> targetMatchMap = new TreeMap<>();
			for (int i = 0; i < list.size(); ++i) {
				if (!replacerIndices.contains(i)) {
					Action action = list.get(i);
					if (action instanceof IValueAction iva) {
						fillReplaceMap(iva, i, true, replacerInfoMap, targetMatchMap);
						fillReplaceMap(iva, i, false, replacerInfoMap, targetMatchMap);
					}
				}
			}
			
			for (Pair<RawDataId, Integer> info : replacerInfoMap.values()) {
				flag = true;
				list.set(info.right, new NoOpAction());
			}
			
			for (Entry<Integer, Pair<RawDataId, boolean[]>> entry : targetMatchMap.entrySet()) {
				int index = entry.getKey();
				Pair<RawDataId, boolean[]> match = entry.getValue();
				Pair<RawDataId, Integer> info = replacerInfoMap.get(match.left);
				if (info != null) {
					DataId target = match.left.internal, replacer = target.getRawReplacer(null, info.left.internal);
					T iva = null;
					if (replacer != null) {
						boolean[] arr = match.right;
						iva = (T) list.get(index);
						if (arr[0]) {
							flag = true;
							iva = iva.replaceLvalue(target, replacer);
						}
						if (arr[1]) {
							flag = true;
							iva = iva.replaceRvalue(target, replacer);
						}
					}
					if (iva == null) {
						throw new IllegalArgumentException(String.format("Unexpectedly failed to replace data ID %s! %s", target, list.get(index)));
					}
					list.set(index, iva);
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnusedAssignments(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.body;
		Map<Long, int[]> regIdMap = new TreeMap<>();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size() - 1; ++j) {
				Action action = list.get(j);
				if ((action instanceof AssignmentAction || action instanceof CompoundAssignmentAction) && !(list.get(j + 1) instanceof ConditionalJumpAction)) {
					IValueAction iva = (IValueAction) action;
					for (DataId id : iva.lvalues()) {
						if (id instanceof RegDataId regDataId) {
							if (regDataId.dereferenceLevel == 0) {
								regIdMap.put(regDataId.regId, new int[] {i, j});
							}
						}
					}
				}
			}
		}
		
		for (List<Action> list : body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IValueAction iva) {
					for (DataId id : iva.lvalues()) {
						if (id instanceof RegDataId regDataId) {
							if (regDataId.dereferenceLevel != 0) {
								regIdMap.remove(regDataId.regId);
							}
						}
					}
					for (DataId id : iva.rvalues()) {
						if (id instanceof RegDataId regDataId) {
							regIdMap.remove(regDataId.regId);
						}
					}
				}
			}
		}
		
		for (int[] fullIndex : regIdMap.values()) {
			flag = true;
			body.get(fullIndex[0]).set(fullIndex[1], new NoOpAction());
		}
		return flag;
	}
	
	public static boolean orderRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.body;
		Map<Long, Long> regIdMap = new TreeMap<>();
		long count = 0;
		for (List<Action> list : body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IValueAction iva) {
					for (DataId[] arr : Arrays.asList(iva.lvalues(), iva.rvalues())) {
						for (DataId id : arr) {
							if (id instanceof RegDataId regDataId) {
								long regId = regDataId.regId;
								if (!regIdMap.containsKey(regId)) {
									regIdMap.put(regId, count++);
								}
							}
						}
					}
				}
			}
		}
		
		for (List<Action> list : body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IValueAction iva) {
					Action replace = iva.replaceRegIds(regIdMap);
					if (replace != null) {
						flag = true;
						list.set(i, replace);
					}
				}
			}
		}
		return flag;
	}
}
