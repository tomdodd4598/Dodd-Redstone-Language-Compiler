package drlc.intermediate;

import java.util.*;
import java.util.Map.Entry;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.routine.Routine;

public class IntermediateOptimization {
	
	public static boolean removeNoOps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
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
	
	public static boolean simplifySections(Routine routine) {
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
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					int target = Helpers.parseSectionId(jump.getTarget());
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						Action single = section.get(0);
						if (single instanceof IJumpAction) {
							IJumpAction other = (IJumpAction) single;
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
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					targets.add(Helpers.parseSectionId(jump.getTarget()));
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
	
	public static boolean replaceJumps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		final Map<List<Action>, Boolean> clearMap = new HashMap<>();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					int target = Helpers.parseSectionId(jump.getTarget());
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						Action single = section.get(0);
						if (single instanceof IDefiniteRedirectAction) {
							boolean conditional = jump.isConditional();
							if (!conditional) {
								flag = true;
								list.set(j, single);
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
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); ++j) {
				Action action = list.get(j), previous = list.get(j - 1);
				if (action instanceof ConditionalJumpAction) {
					if (previous instanceof IValueAction) {
						IValueAction iva = (IValueAction) previous;
						if (iva instanceof AssignmentAction || iva instanceof InitializationAction) {
							DataId arg = iva.rvalues()[0];
							if (arg instanceof ValueDataId) {
								flag = true;
								ConditionalJumpAction cja = (ConditionalJumpAction) action;
								boolean noop = ((ValueDataId) arg).value.equals(Main.generator.falseValue) ^ !cja.jumpCondition;
								list.set(j, noop ? new NoOpAction() : new JumpAction(null, cja.target));
								list.set(j - 1, new NoOpAction());
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
					if (!jump.isConditional() && Helpers.parseSectionId(jump.getTarget()) == i + 1) {
						flag = true;
						list.set(list.size() - 1, new NoOpAction());
					}
				}
			}
		}
		return flag;
	}
	
	private static void fillCompressMap(IValueAction valAction, int index, Map<Long, Integer> map, DataId[] dataIds) {
		for (DataId dataId : dataIds) {
			if (dataId.dereferenceLevel == 0 && dataId instanceof RegDataId) {
				long regId = ((RegDataId) dataId).regId;
				if (map.containsKey(regId)) {
					throw new IllegalArgumentException(String.format("Found unexpected use of register %s! %s", dataId, valAction));
				}
				else {
					map.put(regId, index);
				}
			}
		}
	}
	
	public static boolean compressRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			final Map<Long, Integer> lMap = new TreeMap<>(), rMap = new TreeMap<>();
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction valAction = (IValueAction) action;
					fillCompressMap(valAction, j, lMap, valAction.lvalues());
					fillCompressMap(valAction, j, rMap, valAction.rvalues());
				}
			}
			
			boolean sectionFlag = false;
			
			for (Entry<Long, Integer> entry : lMap.entrySet()) {
				long regId = entry.getKey();
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove() && rMap.containsKey(regId)) {
					int otherIndex = rMap.get(regId);
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceRvalue()) {
						Action replacement = other.replaceRegRvalue(regId, action.getRvalueReplacer());
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
			
			for (Entry<Long, Integer> entry : rMap.entrySet()) {
				long regId = entry.getKey();
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove() && lMap.containsKey(regId)) {
					int otherIndex = lMap.get(regId);
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceLvalue()) {
						Action replacement = other.replaceRegLvalue(regId, action.getLvalueReplacer());
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
					Action second;
					if (lvalAction.lvalues().length == 1 && (second = list.get(j)) instanceof IValueAction) {
						DataId lvalue = lvalAction.lvalues()[0];
						if (lvalue instanceof ValueDataId) {
							throw new IllegalArgumentException(String.format("Data ID \"%s\" can not be used as an lvalue! %s", lvalue, lvalAction));
						}
						IValueAction valAction = (IValueAction) second;
						if (valAction.canReorderRvalues()) {
							int index = 0;
							DataId[] rvalues = valAction.rvalues();
							for (int k = 0; k < rvalues.length; ++k) {
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
	
	public static boolean foldRvalues(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction valAction = (IValueAction) action;
					Action replace = valAction.foldRvalues();
					if (replace != null) {
						list.set(j, replace);
						flag = true;
					}
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
					IValueAction valAction = (IValueAction) action;
					for (DataId[] arr : Helpers.array(valAction.lvalues(), valAction.rvalues())) {
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
	
	public static boolean simplifyAddressDereferences(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof DereferenceAction) {
					DereferenceAction deref = (DereferenceAction) action;
					DataId arg = deref.arg;
					if (arg.isAddress()) {
						list.set(j, new AssignmentAction(null, deref.target, arg.removeAddressPrefix()));
						flag = true;
					}
				}
			}
		}
		return flag;
	}
}
