package drlc.generate.intermediate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import drlc.Helper;
import drlc.Helper.Pair;
import drlc.interpret.action.Action;
import drlc.interpret.action.IJumpAction;
import drlc.interpret.action.IStopAction;
import drlc.interpret.action.IValueAction;
import drlc.interpret.action.NoOpAction;
import drlc.interpret.routine.Routine;

public class IntermediateOptimization {
	
	public static boolean removeDeadActions(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			int j, size = list.size();
			boolean dead = false;
			for (j = 0; j < size - 1; j++) {
				Action action = list.get(j);
				if (action instanceof IStopAction) {
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
					int target = Helper.parseSectionId(jump.getTarget());
					boolean bool = true;
					for (int key : keys) {
						if (target <= key) {
							bool = false;
							list.set(j, jump.copy(Helper.sectionIdString(target - sectionMap.get(key))));
							break;
						}
					}
					if (bool) {
						list.set(j, jump.copy(Helper.sectionIdString(target - count)));
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyJumps(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		Set<List<Action>> clearSet = new HashSet<>();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) action;
					int target = Helper.parseSectionId(jump.getTarget());
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						if (section.get(0) instanceof IJumpAction) {
							flag = true;
							list.set(j, jump.copy(((IJumpAction<?>) section.get(0)).getTarget()));
							clearSet.add(section);
						}
					}
				}
			}
		}
		
		for (List<Action> section : clearSet) {
			section.clear();
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
					targets.add(Helper.parseSectionId(jump.getTarget()));
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
					int target = Helper.parseSectionId(jump.getTarget());
					List<Action> section = body.get(target);
					if (section.size() == 1) {
						if (section.get(0) instanceof IStopAction) {
							boolean conditional = jump.conditional();
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
		return flag;
	}
	
	static class MapArrayPair extends Pair<Map<Integer, Integer>, String[]> {
		public MapArrayPair(Map<Integer, Integer> map, String[] array) {
			super(map, array);
		}
	}
	
	public static boolean compressRValueRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			final Map<Integer, Integer> lMap = new TreeMap<>(), rMap = new TreeMap<>();
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					IValueAction valAction = (IValueAction) list.get(j);
					for (MapArrayPair pair : new MapArrayPair[] {new MapArrayPair(lMap, valAction.lValues()), new MapArrayPair(rMap, valAction.rValues())}) {
						for (String val : pair.right) {
							if (Helper.isRegId(val)) {
								int regId = Helper.parseRegId(val);
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
			
			for (Entry<Integer, Integer> entry : lMap.entrySet()) {
				int lKey = entry.getKey(), actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove() && rMap.containsKey(lKey)) {
					int otherIndex = rMap.get(lKey);
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceRValue()) {
						Action replacement = other.replaceRValue(Helper.regIdString(lKey), action.getRValueReplacer());
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
	
	public static boolean compressLValueRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			final Map<Integer, Integer> lMap = new TreeMap<>(), rMap = new TreeMap<>();
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					IValueAction valAction = (IValueAction) list.get(j);
					for (MapArrayPair pair : new MapArrayPair[] {new MapArrayPair(lMap, valAction.lValues()), new MapArrayPair(rMap, valAction.rValues())}) {
						for (String val : pair.right) {
							if (Helper.isRegId(val)) {
								int regId = Helper.parseRegId(val);
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
			
			for (Entry<Integer, Integer> entry : rMap.entrySet()) {
				int actionIndex = entry.getValue();
				IValueAction action = (IValueAction) list.get(actionIndex);
				if (action.canRemove()) {
					String regId = Helper.regIdString(entry.getKey());
					int otherIndex = lMap.get(entry.getKey());
					IValueAction other = (IValueAction) list.get(otherIndex);
					if (other.canReplaceLValue()) {
						Action replacement = other.replaceLValue(regId, action.getLValueReplacer());
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
	
	public static boolean orderRegisters(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		final Map<String, String> regIdMap = new HashMap<>();
		int count = 0;
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IValueAction) {
					IValueAction valAction = (IValueAction) list.get(j);
					for (String[] arr : new String[][] {valAction.lValues(), valAction.rValues()}) {
						for (String str : arr) {
							if (Helper.isRegId(str) && !regIdMap.containsKey(str)) {
								regIdMap.put(str, Helper.regIdString(count));
								++count;
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
	
	public static boolean reorderRValues(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); j++) {
				if (list.get(j - 1) instanceof IValueAction) {
					IValueAction lValAction = (IValueAction) list.get(j - 1);
					if (lValAction.lValues().length == 1 && list.get(j) instanceof IValueAction) {
						String lValue = lValAction.lValues()[0];
						if (Helper.isImmediateValue(lValue)) {
							throw new IllegalArgumentException(String.format("Immediate %s can not be used as an lvalue! %s", lValue, lValAction));
						}
						IValueAction valAction = (IValueAction) list.get(j);
						if (valAction.canReorderRValues()) {
							int index = 0;
							String[] rValues = valAction.rValues();
							for (int k = 0; k < rValues.length; k++) {
								if (rValues[k].equals(lValue)) {
									index = k;
									break;
								}
							}
							if (index != 0) {
								Action replace = valAction.swapRValues(0, index);
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
}
