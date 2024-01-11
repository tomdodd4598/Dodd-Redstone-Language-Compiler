package drlc.intermediate;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.action.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.routine.Routine;

public class IntermediateOptimization {
	
	public static boolean removeNoOps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			Iterator<Action> iter = body.get(i).iterator();
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
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			int j, size = list.size();
			boolean dead = false;
			for (j = 0; j < size - 1; ++j) {
				Action action = list.get(j);
				if (action instanceof IDefiniteRedirectAction) {
					flag = dead = true;
					break;
				}
			}
			if (dead) {
				list.subList(j + 1, size).clear();
			}
		}
		return flag;
	}
	
	public static boolean removeEmptySections(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		Map<Integer, Integer> sectionMap = new TreeMap<>();
		int count = 0;
		for (int i = 0; i < body.size(); ++i) {
			if (body.get(i).isEmpty()) {
				flag = true;
				sectionMap.put(i + count, count);
				body.remove(i);
				++count;
			}
		}
		Set<Integer> keys = sectionMap.keySet();
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					int target = jump.getTarget();
					boolean bool = true;
					for (int key : keys) {
						if (target <= key) {
							bool = false;
							list.set(j, jump.copy(target - sectionMap.get(key)));
							break;
						}
					}
					if (bool) {
						list.set(j, jump.copy(target - count));
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean concatenateSections(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		Set<Integer> targets = new HashSet<>();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
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
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					int target = jump.getTarget();
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						Action single = section.get(0);
						if (single instanceof IDefiniteRedirectAction) {
							boolean conditional = jump.isConditional();
							if (!conditional) {
								flag = true;
								list.set(j, single);
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); ++j) {
				Action action = list.get(j), previous = list.get(j - 1);
				if (action instanceof ConditionalJumpAction) {
					if (previous instanceof IValueAction) {
						IValueAction iva = (IValueAction) previous;
						if (iva instanceof AssignmentAction) {
							DataId arg = iva.rvalues()[0];
							if (arg instanceof ValueDataId) {
								flag = true;
								ConditionalJumpAction cja = (ConditionalJumpAction) action;
								Value<?> value = ((ValueDataId) arg).value;
								if (value.typeInfo.equals(Main.generator.boolTypeInfo)) {
									boolean noop = value.boolValue(null) ^ cja.jumpCondition;
									list.set(j, noop ? new NoOpAction() : new JumpAction(null, cja.getTarget()));
									list.set(j - 1, new NoOpAction());
								}
								else {
									throw new IllegalArgumentException(String.format("Value \"%s\" can not be used as a conditional! %s", value, iva));
								}
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			if (!list.isEmpty()) {
				Action action = list.get(list.size() - 1);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					if (!jump.isConditional() && jump.getTarget() == i + 1) {
						flag = true;
						list.set(list.size() - 1, new NoOpAction());
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
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			Map<DataId, Integer> lMap = new LinkedHashMap<>(), rMap = new LinkedHashMap<>();
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction valAction = (IValueAction) action;
					fillCompressMap(valAction, j, true, lMap);
					fillCompressMap(valAction, j, false, rMap);
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
							else if (action.canRemove(true) && other instanceof CompoundAssignmentAction) {
								internalFlag = true;
								CompoundAssignmentAction from = (CompoundAssignmentAction) action, to = (CompoundAssignmentAction) other;
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
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); ++j) {
				Action first = list.get(j - 1);
				if (first instanceof IValueAction) {
					IValueAction lvalAction = (IValueAction) first;
					DataId[] lvalues = lvalAction.lvalues();
					Action second;
					if (lvalues.length == 1 && (second = list.get(j)) instanceof IValueAction) {
						DataId lvalue = lvalues[0];
						IValueAction rvalAction = (IValueAction) second;
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
									list.set(j, replace);
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
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					Action replace = iva.foldRvalues();
					if (replace != null) {
						flag = true;
						list.set(j, replace);
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyBinaryOps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof BinaryOpAction) {
					BinaryOpAction boa = (BinaryOpAction) action;
					Action replace = boa.simplify();
					if (replace != null) {
						flag = true;
						list.set(j, replace);
					}
				}
			}
		}
		return flag;
	}
	
	private static class RawDataId {
		
		final DataId internal;
		
		RawDataId(DataId internal) {
			this.internal = internal;
		}
		
		@Override
		public int hashCode() {
			return internal.hashCode(true);
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof RawDataId && internal.equalsOther(((RawDataId) obj).internal, true);
		}
	}
	
	private static void fillReplaceMap(IValueAction iva, int index, boolean lvalues, Map<RawDataId, Pair<RawDataId, Integer>> replacerInfoMap, Map<Integer, Pair<RawDataId, boolean[]>> targetMatchMap) {
		for (DataId dataId : iva.dataIds(lvalues)) {
			RawDataId rawDataId = new RawDataId(dataId);
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
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			Map<RawDataId, Pair<RawDataId, Integer>> replacerInfoMap = new HashMap<>();
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					if (iva instanceof AssignmentAction) {
						DataId lvalue = iva.lvalues()[0], rvalue = iva.rvalues()[0];
						if (lvalue.typeInfo.isAddress() && !lvalue.isRepeatable(true) && rvalue.dereferenceLevel <= 0 && !rvalue.isIndexed()) {
							RawDataId rawDeref = new RawDataId(lvalue.addDereference(null));
							if (replacerInfoMap.containsKey(rawDeref)) {
								throw new IllegalArgumentException(String.format("Found unexpected repeated use of register %s! %s", lvalue, iva));
							}
							else {
								replacerInfoMap.put(rawDeref, new Pair<>(new RawDataId(rvalue.addDereference(null)), j));
							}
						}
					}
				}
			}
			
			Set<Integer> replacerIndices = replacerInfoMap.values().stream().map(x -> x.right).collect(Collectors.toSet());
			Map<Integer, Pair<RawDataId, boolean[]>> targetMatchMap = new TreeMap<>();
			for (int j = 0; j < list.size(); ++j) {
				if (!replacerIndices.contains(j)) {
					Action action = list.get(j);
					if (action instanceof IValueAction) {
						IValueAction iva = (IValueAction) action;
						fillReplaceMap(iva, j, true, replacerInfoMap, targetMatchMap);
						fillReplaceMap(iva, j, false, replacerInfoMap, targetMatchMap);
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
		List<List<Action>> body = routine.getBodyActionLists();
		Map<Long, int[]> regIdMap = new TreeMap<>();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size() - 1; ++j) {
				Action action = list.get(j);
				if ((action instanceof AssignmentAction || action instanceof CompoundAssignmentAction) && !(list.get(j + 1) instanceof ConditionalJumpAction)) {
					IValueAction iva = (IValueAction) action;
					for (DataId id : iva.lvalues()) {
						if (id instanceof RegDataId) {
							RegDataId regDataId = (RegDataId) id;
							if (regDataId.dereferenceLevel == 0) {
								regIdMap.put(regDataId.regId, new int[] {i, j});
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					for (DataId id : iva.rvalues()) {
						if (id instanceof RegDataId) {
							regIdMap.remove(((RegDataId) id).regId);
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
		List<List<Action>> body = routine.getBodyActionLists();
		Map<Long, Long> regIdMap = new TreeMap<>();
		long count = 0;
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					for (DataId[] arr : Helpers.array(iva.lvalues(), iva.rvalues())) {
						for (DataId id : arr) {
							if (id instanceof RegDataId) {
								long regId = ((RegDataId) id).regId;
								if (!regIdMap.containsKey(regId)) {
									regIdMap.put(regId, count++);
								}
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					Action replace = ((IValueAction) action).replaceRegIds(regIdMap);
					if (replace != null) {
						flag = true;
						list.set(j, replace);
					}
				}
			}
		}
		return flag;
	}
}
