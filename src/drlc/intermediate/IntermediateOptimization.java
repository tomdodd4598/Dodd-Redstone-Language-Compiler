package drlc.intermediate;

import java.util.*;
import java.util.Map.Entry;

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
		final Map<Integer, Integer> sectionMap = new TreeMap<>();
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
								Value value = ((ValueDataId) arg).value;
								if (value.typeInfo.equals(Main.generator.boolTypeInfo)) {
									boolean noop = value.equals(Main.generator.falseValue) ^ !cja.jumpCondition;
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
	
	private static void fillCompressMap(IValueAction iva, int index, boolean lvalues, Map<DataId, Integer> map) {
		for (DataId dataId : lvalues ? iva.lvalues() : iva.rvalues()) {
			if (dataId.isCompressable()) {
				if (!dataId.isRepeatable(lvalues) && map.containsKey(dataId)) {
					throw new IllegalArgumentException(String.format("Found unexpected repeated use of register %s! %s", dataId, iva));
				}
				else {
					map.put(dataId, index);
				}
			}
		}
	}
	
	public static boolean compressRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			final Map<DataId, Integer> lMap = new LinkedHashMap<>(), rMap = new LinkedHashMap<>();
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction valAction = (IValueAction) action;
					fillCompressMap(valAction, j, true, lMap);
					fillCompressMap(valAction, j, false, rMap);
				}
			}
			
			boolean sectionFlag = false;
			
			for (Entry<DataId, Integer> entry : lMap.entrySet()) {
				DataId dataId = entry.getKey();
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove() && rMap.containsKey(dataId)) {
					int otherIndex = rMap.get(dataId);
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceRvalue()) {
						Action replacement = other.replaceRvalue(dataId, action.getRvalueReplacer());
						if (replacement != null) {
							flag = sectionFlag = true;
							list.set(actionIndex, new NoOpAction());
							list.set(otherIndex, replacement);
						}
					}
				}
			}
			
			if (sectionFlag) {
				continue;
			}
			
			for (Entry<DataId, Integer> entry : rMap.entrySet()) {
				DataId dataId = entry.getKey();
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove() && lMap.containsKey(dataId)) {
					int otherIndex = lMap.get(dataId);
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceLvalue()) {
						Action replacement = other.replaceLvalue(dataId, action.getLvalueReplacer());
						if (replacement != null) {
							flag = true;
							list.set(actionIndex, new NoOpAction());
							list.set(otherIndex, replacement);
						}
					}
				}
			}
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
	
	private static void fillReplaceMap(IValueAction iva, int index, boolean lvalues, Map<DataId, Pair<DataId, Integer>> replacerInfoMap, Map<Integer, Pair<DataId, boolean[]>> targetMatchMap) {
		for (DataId dataId : lvalues ? iva.lvalues() : iva.rvalues()) {
			if (replacerInfoMap.containsKey(dataId)) {
				if (lvalues ? iva.canReplaceLvalue() : iva.canReplaceRvalue()) {
					Pair<DataId, boolean[]> match = targetMatchMap.get(index);
					if (match != null) {
						match.right[lvalues ? 0 : 1] = true;
					}
					else {
						targetMatchMap.put(index, new Pair<>(dataId, new boolean[] {lvalues, !lvalues}));
					}
				}
			}
			else {
				Iterator<DataId> iter = replacerInfoMap.keySet().iterator();
				while (iter.hasNext()) {
					if (dataId.equalsOther(iter.next(), true)) {
						iter.remove();
					}
				}
			}
		}
	}
	
	public static <T extends Action & IValueAction> boolean simplifyDereferences(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			final Set<Integer> indices = new HashSet<>();
			final Map<DataId, Pair<DataId, Integer>> replacerInfoMap = new HashMap<>();
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					if (iva instanceof AssignmentAction) {
						DataId lvalue = iva.lvalues()[0], rvalue = iva.rvalues()[0];
						if (!lvalue.isRepeatable(true) && lvalue.typeInfo.isAddress() && rvalue.dereferenceLevel <= 0) {
							DataId deref = lvalue.addDereference(null);
							if (replacerInfoMap.containsKey(deref)) {
								throw new IllegalArgumentException(String.format("Found unexpected repeated use of register %s! %s", lvalue, iva));
							}
							else {
								indices.add(j);
								replacerInfoMap.put(deref, new Pair<>(rvalue.addDereference(null), j));
							}
						}
					}
				}
			}
			
			final Map<Integer, Pair<DataId, boolean[]>> targetMatchMap = new TreeMap<>();
			for (int j = 0; j < list.size(); ++j) {
				if (!indices.contains(j)) {
					Action action = list.get(j);
					if (action instanceof IValueAction) {
						IValueAction iva = (IValueAction) action;
						fillReplaceMap(iva, j, true, replacerInfoMap, targetMatchMap);
						fillReplaceMap(iva, j, false, replacerInfoMap, targetMatchMap);
					}
				}
			}
			
			for (Pair<DataId, Integer> info : replacerInfoMap.values()) {
				flag = true;
				list.set(info.right, new NoOpAction());
			}
			
			for (Entry<Integer, Pair<DataId, boolean[]>> entry : targetMatchMap.entrySet()) {
				int index = entry.getKey();
				Pair<DataId, boolean[]> match = entry.getValue();
				Pair<DataId, Integer> info = replacerInfoMap.get(match.left);
				if (info != null) {
					flag = true;
					boolean[] arr = match.right;
					T iva = (T) list.get(index);
					if (arr[0]) {
						iva = iva.replaceLvalue(match.left, info.left);
					}
					if (arr[1]) {
						iva = iva.replaceRvalue(match.left, info.left);
					}
					list.set(index, iva);
				}
			}
		}
		return flag;
	}
	
	public static boolean orderRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		final Map<Long, Long> regIdMap = new TreeMap<>();
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
