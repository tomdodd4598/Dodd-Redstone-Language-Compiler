package drlc.generate.intermediate;

import java.util.*;
import java.util.Map.Entry;

import drlc.Helpers;
import drlc.Helpers.Pair;
import drlc.interpret.action.*;
import drlc.interpret.component.DataId;
import drlc.interpret.routine.Routine;

public class IntermediateOptimization {
	
	public static boolean removeNoOps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof NoOpAction) {
					flag = true;
					list.remove(j);
				}
			}
		}
		return flag;
	}
	
	public static boolean removeDeadActions(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			int j, size = list.size();
			boolean dead = false;
			for (j = 0; j < size - 1; j++) {
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
	
	public static boolean simplifySections(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		final Map<Integer, Integer> sectionMap = new TreeMap<>();
		int count = 0;
		for (int i = 0; i < body.size(); i++) {
			if (body.get(i).isEmpty()) {
				flag = true;
				sectionMap.put(i + count, count);
				body.remove(i);
				++count;
			}
		}
		Set<Integer> keys = sectionMap.keySet();
		
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) action;
					int target = Helpers.parseSectionId(jump.getTarget());
					boolean bool = true;
					for (int key : keys) {
						if (target <= key) {
							bool = false;
							list.set(j, jump.copy(Helpers.sectionIdString(target - sectionMap.get(key))));
							break;
						}
					}
					if (bool) {
						list.set(j, jump.copy(Helpers.sectionIdString(target - count)));
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyJumps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) action;
					int target = Helpers.parseSectionId(jump.getTarget());
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						if (section.get(0) instanceof IJumpAction) {
							IJumpAction<?> other = (IJumpAction<?>) section.get(0);
							if (!other.isConditional()) {
								flag = true;
								list.set(j, jump.copy(other.getTarget()));
							}
						}
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean shiftActions(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		Set<Integer> targets = new HashSet<>();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) action;
					targets.add(Helpers.parseSectionId(jump.getTarget()));
				}
			}
		}
		
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			int j = i + 1;
			while (j < body.size() && !targets.contains(j)) {
				flag = true;
				list.addAll(body.get(j));
				body.get(j).clear();
				++j;
			}
		}
		return flag;
	}
	
	public static boolean replaceJumps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		final Map<List<Action>, Boolean> clearMap = new HashMap<>();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) action;
					int target = Helpers.parseSectionId(jump.getTarget());
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						if (section.get(0) instanceof IDefiniteRedirectAction) {
							boolean conditional = jump.isConditional();
							if (!conditional) {
								flag = true;
								list.set(j, section.get(0));
							}
							clearMap.put(section, clearMap.containsKey(section) ? clearMap.get(section) || conditional : conditional);
						}
					}
				}
			}
		}
		
		for (Entry<List<Action>, Boolean> entry : clearMap.entrySet()) {
			if (!entry.getValue()) {
				flag = true;
				entry.getKey().clear();
			}
		}
		
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); j++) {
				Action action = list.get(j), previous = list.get(j - 1);
				if (action instanceof ConditionalJumpAction) {
					if (previous instanceof IValueAction) {
						IValueAction iva = (IValueAction) previous;
						if (iva instanceof AssignmentAction || iva instanceof InitializationAction) {
							DataId arg = iva.rvalues()[0];
							if (Helpers.isImmediateValue(arg.raw)) {
								flag = true;
								ConditionalJumpAction cja = (ConditionalJumpAction) action;
								list.set(j, (Helpers.parseImmediateValue(arg.raw) == 0) ^ !cja.jumpCondition ? new NoOpAction() : new JumpAction(null, cja.target));
								list.set(j - 1, new NoOpAction());
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			if (!list.isEmpty()) {
				Action action = list.get(list.size() - 1);
				if (action instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) action;
					if (!jump.isConditional() && Helpers.parseSectionId(jump.getTarget()) == i + 1) {
						flag = true;
						list.set(list.size() - 1, new NoOpAction());
					}
				}
			}
		}
		return flag;
	}
	
	static class MapArrayPair extends Pair<Map<Long, Integer>, DataId[]> {
		
		public MapArrayPair(Map<Long, Integer> map, DataId[] array) {
			super(map, array);
		}
	}
	
	public static boolean compressRvalueRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			final Map<Long, Integer> lMap = new TreeMap<>(), rMap = new TreeMap<>();
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					IValueAction valAction = (IValueAction) list.get(j);
					for (MapArrayPair pair : new MapArrayPair[] {new MapArrayPair(lMap, valAction.lvalues()), new MapArrayPair(rMap, valAction.rvalues())}) {
						for (DataId val : pair.right) {
							if (Helpers.isRegId(val.raw)) {
								long regId = Helpers.parseRegId(val.raw);
								if (pair.left.containsKey(regId)) {
									throw new IllegalArgumentException(String.format("Found unexpected use of register %s! %s", val, valAction));
								}
								else {
									pair.left.put(regId, j);
								}
							}
						}
					}
				}
			}
			
			for (Entry<Long, Integer> entry : lMap.entrySet()) {
				long lKey = entry.getKey();
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove() && rMap.containsKey(lKey)) {
					int otherIndex = rMap.get(lKey);
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceRvalue()) {
						Action replacement = other.replaceRvalue(Helpers.regDataId(lKey), action.getRvalueReplacer());
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
	
	public static boolean compressLvalueRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			final Map<Long, Integer> lMap = new TreeMap<>(), rMap = new TreeMap<>();
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					IValueAction valAction = (IValueAction) list.get(j);
					for (MapArrayPair pair : new MapArrayPair[] {new MapArrayPair(lMap, valAction.lvalues()), new MapArrayPair(rMap, valAction.rvalues())}) {
						for (DataId val : pair.right) {
							if (Helpers.isRegId(val.raw)) {
								long regId = Helpers.parseRegId(val.raw);
								if (pair.left.containsKey(regId)) {
									throw new IllegalArgumentException(String.format("Found unexpected use of register %s! %s", val, valAction));
								}
								else {
									pair.left.put(regId, j);
								}
							}
						}
					}
				}
			}
			
			loop: for (Entry<Long, Integer> entry : rMap.entrySet()) {
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove()) {
					DataId regId = Helpers.regDataId(entry.getKey());
					int otherIndex = lMap.get(entry.getKey());
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceLvalue()) {
						DataId lvalueReplacer = action.getLvalueReplacer();
						if (lvalueReplacer.dereferenceLevel > 0 && actionIndex > otherIndex) {
							for (int k = otherIndex; k <= actionIndex; ++k) {
								if (list.get(k) instanceof IValueAction) {
									IValueAction iva = (IValueAction) list.get(k);
									for (DataId lvalue : iva.lvalues()) {
										if (lvalue.equalsOther(lvalueReplacer, true)) {
											continue loop;
										}
									}
								}
							}
						}
						Action replacement = other.replaceLvalue(regId, lvalueReplacer);
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
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); j++) {
				if (list.get(j - 1) instanceof IValueAction) {
					IValueAction lvalAction = (IValueAction) list.get(j - 1);
					if (lvalAction.lvalues().length == 1 && list.get(j) instanceof IValueAction) {
						DataId lvalue = lvalAction.lvalues()[0];
						if (Helpers.isImmediateValue(lvalue.raw)) {
							throw new IllegalArgumentException(String.format("Immediate %s can not be used as an lvalue! %s", lvalue, lvalAction));
						}
						IValueAction valAction = (IValueAction) list.get(j);
						if (valAction.canReorderRvalues()) {
							int index = 0;
							DataId[] rvalues = valAction.rvalues();
							for (int k = 0; k < rvalues.length; k++) {
								if (rvalues[k].equals(lvalue)) {
									index = k;
									break;
								}
							}
							if (index != 0) {
								Action replace = valAction.swapRvalues(0, index);
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
	
	public static boolean orderRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		final Map<DataId, DataId> regIdMap = new HashMap<>();
		int count = 0;
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					IValueAction valAction = (IValueAction) list.get(j);
					for (DataId[] arr : new DataId[][] {valAction.lvalues(), valAction.rvalues()}) {
						for (DataId id : arr) {
							id = id.removeAllDereferences();
							if (Helpers.isRegId(id.raw) && !regIdMap.containsKey(id)) {
								regIdMap.put(id, Helpers.regDataId(count++));
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					Action replace = ((IValueAction) list.get(j)).replaceRegIds(regIdMap);
					if (replace != null) {
						flag = true;
						list.set(j, replace);
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyAddressDereferences(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof DereferenceAction) {
					DereferenceAction deref = (DereferenceAction) list.get(j);
					DataId arg = deref.arg;
					if (Helpers.hasAddressPrefix(arg.raw)) {
						list.set(j, new AssignmentAction(null, deref.target, arg.removeAddressPrefix()));
						flag = true;
					}
				}
			}
		}
		return flag;
	}
}
