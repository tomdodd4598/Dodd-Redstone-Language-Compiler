package drlc.intermediate;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import drlc.Helpers.Pair;
import drlc.Main;
import drlc.intermediate.action.*;
import drlc.intermediate.component.Variable;
import drlc.intermediate.component.data.*;
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
	
	public static boolean removeUnreachableSections(Routine routine) {
		List<List<Action>> body = routine.body;
		int prevSize = body.size();
		if (prevSize <= 0) {
			return false;
		}
		Set<Integer> reachableSections = routine.getReachableSections();
		
		boolean flag = false;
		boolean[] removed = new boolean[prevSize];
		int removedCount = 0;
		for (int i = 0; i < prevSize; ++i) {
			if (!reachableSections.contains(i)) {
				flag = true;
				removed[i] = true;
				++removedCount;
			}
		}
		
		if (!flag) {
			return false;
		}
		
		int[] removedBefore = new int[prevSize + 1];
		int count = 0;
		for (int i = 0; i < prevSize; ++i) {
			removedBefore[i] = count;
			if (removed[i]) {
				++count;
			}
		}
		removedBefore[prevSize] = count;
		
		List<List<Action>> newBody = new ArrayList<>(prevSize - removedCount);
		for (int i = 0; i < prevSize; ++i) {
			if (!removed[i]) {
				newBody.add(body.get(i));
			}
		}
		body.clear();
		body.addAll(newBody);
		
		for (List<Action> list : body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IJumpAction jump) {
					int target = jump.getTarget();
					if (target < 0 || target > prevSize) {
						throw new IllegalArgumentException(String.format("Found unexpected jump target %d while removing unreachable sections from routine \"%s\" with %d sections!", target, routine, prevSize));
					}
					int newTarget = target - removedBefore[target];
					if (newTarget != target) {
						list.set(i, jump.copy(newTarget));
					}
				}
			}
		}
		return true;
	}
	
	public static boolean removeEmptySections(Routine routine) {
		boolean flag = false;
		List<List<Action>> body = routine.body;
		int prevSize = body.size();
		boolean[] removed = new boolean[prevSize];
		int removedCount = 0;
		for (int i = 0; i < prevSize; ++i) {
			if (body.get(i).isEmpty()) {
				flag = true;
				removed[i] = true;
				++removedCount;
			}
		}
		
		if (!flag) {
			return false;
		}
		
		int[] removedBefore = new int[prevSize + 1];
		int count = 0;
		for (int i = 0; i < prevSize; ++i) {
			removedBefore[i] = count;
			if (removed[i]) {
				++count;
			}
		}
		removedBefore[prevSize] = count;
		
		List<List<Action>> newBody = new ArrayList<>(prevSize - removedCount);
		for (int i = 0; i < prevSize; ++i) {
			if (!removed[i]) {
				newBody.add(body.get(i));
			}
		}
		body.clear();
		body.addAll(newBody);
		
		for (List<Action> list : body) {
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IJumpAction jump) {
					int target = jump.getTarget();
					if (target < 0 || target > prevSize) {
						throw new IllegalArgumentException(String.format("Found unexpected jump target %d while removing empty sections from routine \"%s\" with %d sections!", target, routine, prevSize));
					}
					int newTarget = target - removedBefore[target];
					if (newTarget != target) {
						list.set(i, jump.copy(newTarget));
					}
				}
			}
		}
		return true;
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
	
	private static boolean fillCompressMap(IValueAction iva, int index, boolean lvalues, Map<DataId, Integer> map, Set<DataId> repeatedDataIds) {
		boolean added = false;
		for (DataId dataId : iva.dataIds(lvalues)) {
			if (dataId.isCompressable()) {
				if (repeatedDataIds.contains(dataId)) {
					continue;
				}
				else if (map.containsKey(dataId)) {
					if (!lvalues && iva instanceof CompoundAssignmentAction && map.get(dataId) == index) {
						continue;
					}
					else if (!dataId.isRepeatable(lvalues)) {
						throw new IllegalArgumentException(String.format("Found unexpected repeated use of data ID %s! %s", dataId, iva));
					}
					else {
						map.remove(dataId);
						repeatedDataIds.add(dataId);
					}
				}
				else {
					added = true;
					map.put(dataId, index);
				}
			}
		}
		return added;
	}
	
	private static Variable getMappedVariable(DataId dataId, Map<Long, Variable> regIdAddressMap, Map<Variable, Variable> variableAddressMap) {
		if (dataId instanceof VariableDataId variableDataId) {
			if (variableDataId.isAddress()) {
				return variableDataId.variable;
			}
			else if (variableDataId.dereferenceLevel == 0) {
				return variableAddressMap.get(variableDataId.variable);
			}
		}
		else if (dataId instanceof RegDataId regDataId) {
			if (regDataId.dereferenceLevel == 0) {
				return regIdAddressMap.get(regDataId.regId);
			}
		}
		return null;
	}
	
	private static boolean hasInterveningAssignment(List<Action> list, int fromIndex, int toIndex, Variable variable) {
		int start = Math.min(fromIndex, toIndex), end = Math.max(fromIndex, toIndex);
		Map<Long, Variable> regIdAddressMap = new HashMap<>();
		Map<Variable, Variable> variableAddressMap = new HashMap<>();
		for (int i = start + 1; i < end; ++i) {
			Action action = list.get(i);
			if (action instanceof CallAction) {
				return true;
			}
			if (action instanceof IValueAction iva) {
				for (DataId lvalue : iva.lvalues()) {
					if (lvalue instanceof VariableDataId variableDataId) {
						if (variableDataId.dereferenceLevel == 0 && variableDataId.variable.equals(variable)) {
							return true;
						}
						else if (variableDataId.dereferenceLevel == 0 && variableDataId.variable.typeInfo.isAddress()) {
							Variable mapped = null;
							if (action instanceof AssignmentAction aa) {
								mapped = getMappedVariable(aa.arg, regIdAddressMap, variableAddressMap);
							}
							if (mapped == null) {
								variableAddressMap.remove(variableDataId.variable);
							}
							else {
								variableAddressMap.put(variableDataId.variable, mapped);
							}
						}
						else if (variableDataId.dereferenceLevel > 0) {
							Variable mapped = variableAddressMap.get(variableDataId.variable);
							if (variable.equals(mapped)) {
								return true;
							}
						}
					}
					else if (lvalue instanceof RegDataId regDataId) {
						if (regDataId.dereferenceLevel > 0) {
							Variable mapped = regIdAddressMap.get(regDataId.regId);
							if (variable.equals(mapped)) {
								return true;
							}
						}
						else if (regDataId.dereferenceLevel == 0) {
							Variable mapped = null;
							if (action instanceof AssignmentAction aa) {
								mapped = getMappedVariable(aa.arg, regIdAddressMap, variableAddressMap);
							}
							if (mapped == null) {
								regIdAddressMap.remove(regDataId.regId);
							}
							else {
								regIdAddressMap.put(regDataId.regId, mapped);
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private static boolean canRetarget(DataId target, DataId arg) {
		return target.typeInfo.equalsOther(arg.typeInfo, true) || (target.typeInfo.isAddress() && arg.typeInfo.isWord());
	}
	
	private static boolean compressInternal(List<Action> list, Map<DataId, Integer> lMap, Map<DataId, Integer> rMap, boolean lvalues) {
		boolean flag = false;
		Map<DataId, Integer> otherMap = lvalues ? lMap : rMap;
		Map<DataId, Integer> currentMap = lvalues ? rMap : lMap;
		for (Entry<DataId, Integer> entry : currentMap.entrySet()) {
			DataId dataId = entry.getKey();
			int actionIndex = entry.getValue();
			if (!(list.get(actionIndex) instanceof IValueAction action)) {
				continue;
			}
			if (otherMap.containsKey(dataId)) {
				int otherIndex = otherMap.get(dataId);
				if (!(list.get(otherIndex) instanceof IValueAction other)) {
					continue;
				}
				if (other.canReplaceDataId(lvalues)) {
					if (actionIndex + 1 == otherIndex && dataId instanceof RegDataId regDataId && regDataId.dereferenceLevel == 0 && action instanceof AssignmentAction from && other instanceof AssignmentAction to && to.target.dereferenceLevel == 0 && dataId.equals(to.arg)) {
						flag = true;
						list.set(actionIndex, new NoOpAction());
						list.set(otherIndex, new AssignmentAction(null, to.target, from.arg));
					}
					else if (actionIndex + 1 == otherIndex && dataId instanceof RegDataId regDataId && regDataId.dereferenceLevel == 0 && action.canReplaceLvalue() && (action instanceof BinaryOpAction || action instanceof UnaryOpAction) && other instanceof AssignmentAction to && to.target.dereferenceLevel == 0 && dataId.equals(to.arg) && canRetarget(to.target, dataId)) {
						flag = true;
						list.set(actionIndex, action.replaceLvalue(dataId, to.target));
						list.set(otherIndex, new NoOpAction());
					}
					else if (action.canRemove(false)) {
						DataId replacer = action.getDataIdReplacer(lvalues);
						if (replacer instanceof VariableDataId variableDataId) {
							if (!variableDataId.isAddress() && hasInterveningAssignment(list, actionIndex, otherIndex, variableDataId.variable)) {
								continue;
							}
						}
						Action replacement = other.replaceDataId(lvalues, dataId, replacer);
						if (replacement != null) {
							flag = true;
							list.set(actionIndex, new NoOpAction());
							list.set(otherIndex, replacement);
						}
					}
					else if (action.canRemove(true) && other instanceof CompoundAssignmentAction to) {
						flag = true;
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
					else if (actionIndex + 1 == otherIndex && action.canRemove(true) && action instanceof CompoundAssignmentAction from && other instanceof AssignmentAction to && to.canRemove(false) && dataId.equals(to.arg) && from.args.stream().noneMatch(to.target::equals)) {
						flag = true;
						list.set(actionIndex, new CompoundAssignmentAction(null, to.target, new ArrayList<>(from.args)));
						list.set(otherIndex, new NoOpAction());
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean compressRegisters(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			Map<DataId, Integer> lMap = new LinkedHashMap<>(), rMap = new LinkedHashMap<>();
			Set<DataId> repeatedLDataIds = new HashSet<>(), repeatedRDataIds = new HashSet<>();
			for (int i = 0; i < list.size(); ++i) {
				Action action = list.get(i);
				if (action instanceof IValueAction iva) {
					fillCompressMap(iva, i, true, lMap, repeatedLDataIds);
					fillCompressMap(iva, i, false, rMap, repeatedRDataIds);
				}
			}
			
			if (!Collections.disjoint(lMap.keySet(), rMap.keySet())) {
				flag |= compressInternal(list, lMap, rMap, false);
			}
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
	
	private static boolean usesDirectRegId(DataId dataId, long regId) {
		return dataId instanceof RegDataId regDataId && regDataId.dereferenceLevel == 0 && regDataId.regId == regId;
	}
	
	private static boolean writesDirectRegId(IValueAction iva, long regId) {
		for (DataId dataId : iva.lvalues()) {
			if (usesDirectRegId(dataId, regId)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean writesDirectVariable(IValueAction iva, Variable variable) {
		for (DataId dataId : iva.lvalues()) {
			if (dataId instanceof VariableDataId variableDataId && variableDataId.dereferenceLevel == 0 && variableDataId.variable.equals(variable)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean invalidatesDereferenceReplacer(IValueAction iva, DataId dataId) {
		if (dataId instanceof RegDataId regDataId) {
			return writesDirectRegId(iva, regDataId.regId);
		}
		else if (dataId instanceof VariableDataId variableDataId) {
			return variableDataId.dereferenceLevel > 0 && writesDirectVariable(iva, variableDataId.variable);
		}
		else {
			return true;
		}
	}
	
	private static void invalidateDereferenceReplacers(IValueAction iva, int index, Map<DataId, Pair<DataId, Integer>> replacerInfoMap) {
		Iterator<Entry<DataId, Pair<DataId, Integer>>> iterator = replacerInfoMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<DataId, Pair<DataId, Integer>> entry = iterator.next();
			DataId target = entry.getKey(), replacer = entry.getValue().left;
			if (index <= entry.getValue().right) {
				continue;
			}
			else if (invalidatesDereferenceReplacer(iva, target) || invalidatesDereferenceReplacer(iva, replacer)) {
				iterator.remove();
			}
		}
	}
	
	private static void fillReplaceMap(IValueAction iva, int index, boolean lvalues, Map<DataId, Pair<DataId, Integer>> replacerInfoMap, Map<Integer, Map<DataId, Pair<DataId, boolean[]>>> targetMatchMap) {
		for (DataId dataId : iva.dataIds(lvalues)) {
			Pair<DataId, Integer> info = replacerInfoMap.get(dataId);
			if (info != null) {
				if (dataId.isDereferenced() && index > info.right) {
					if (iva.canReplaceDataId(lvalues)) {
						Map<DataId, Pair<DataId, boolean[]>> matchMap = targetMatchMap.computeIfAbsent(index, k -> new LinkedHashMap<>());
						Pair<DataId, boolean[]> match = matchMap.computeIfAbsent(dataId, k -> new Pair<>(info.left, new boolean[] {false, false}));
						boolean[] arr = match.right;
						arr[lvalues ? 0 : 1] = true;
					}
				}
			}
		}
	}
	
	public static <T extends Action & IValueAction> boolean simplifyDereferences(Routine routine) {
		boolean flag = false;
		for (List<Action> list : routine.body) {
			Map<DataId, Pair<DataId, Integer>> replacerInfoMap = new HashMap<>();
			for (int i = 0; i < list.size(); ++i) {
				if (list.get(i) instanceof IValueAction iva) {
					if (iva instanceof AssignmentAction) {
						DataId lvalue = iva.lvalues()[0], rvalue = iva.rvalues()[0];
						int lref = lvalue.typeInfo.getReferenceLevel(), rref = rvalue.typeInfo.getReferenceLevel();
						if (lvalue instanceof RegDataId lreg && lreg.dereferenceLevel == 0 && !rvalue.isDereferenced() && lvalue.typeInfo.isAddress()) {
							DataId deref = lvalue.addDereference(null);
							DataId replacer = null;
							if (rvalue.typeInfo.isAddress() && lref == rref && lvalue.typeInfo.equalsOther(rvalue.typeInfo, true)) {
								replacer = rvalue.addDereference(null);
							}
							else if (rvalue instanceof RegDataId rreg && rvalue.typeInfo.isWord()) {
								replacer = new RegDataId(deref.dereferenceLevel, deref.typeInfo, rreg.regId);
							}
							if (replacer != null) {
								if (replacerInfoMap.containsKey(deref)) {
									throw new IllegalArgumentException(String.format("Found unexpected repeated use of register %s! %s", lvalue, iva));
								}
								else {
									replacerInfoMap.put(deref, new Pair<>(replacer, i));
								}
							}
						}
					}
				}
			}
			
			Set<Integer> replacerIndices = replacerInfoMap.values().stream().map(x -> x.right).collect(Collectors.toSet());
			Map<Integer, Map<DataId, Pair<DataId, boolean[]>>> targetMatchMap = new TreeMap<>();
			for (int i = 0; i < list.size(); ++i) {
				if (!replacerIndices.contains(i)) {
					Action action = list.get(i);
					if (action instanceof IValueAction iva) {
						invalidateDereferenceReplacers(iva, i, replacerInfoMap);
						fillReplaceMap(iva, i, true, replacerInfoMap, targetMatchMap);
						fillReplaceMap(iva, i, false, replacerInfoMap, targetMatchMap);
					}
				}
			}
			
			for (Pair<DataId, Integer> info : replacerInfoMap.values()) {
				flag = true;
				list.set(info.right, new NoOpAction());
			}
			
			for (Entry<Integer, Map<DataId, Pair<DataId, boolean[]>>> entry : targetMatchMap.entrySet()) {
				int index = entry.getKey();
				Map<DataId, Pair<DataId, boolean[]>> matchMap = entry.getValue();
				T iva = (T) list.get(index);
				boolean changed = false;
				for (Entry<DataId, Pair<DataId, boolean[]>> matchEntry : matchMap.entrySet()) {
					DataId target = matchEntry.getKey();
					Pair<DataId, boolean[]> match = matchEntry.getValue();
					DataId replacer = target.getRawReplacer(null, match.left);
					if (replacer == null) {
						throw new IllegalArgumentException(String.format("Unexpectedly failed to replace data ID %s! %s", target, list.get(index)));
					}
					boolean[] arr = match.right;
					if (arr[0]) {
						flag = true;
						iva = iva.replaceLvalue(target, replacer);
						changed = true;
					}
					if (arr[1]) {
						flag = true;
						iva = iva.replaceRvalue(target, replacer);
						changed = true;
					}
				}
				if (changed) {
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
		
		for (int i = 0; i < body.size(); ++i) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); ++j) {
				Action element = list.get(j);
				if (element instanceof IValueAction iva) {
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
			for (Action element : list) {
				if (element instanceof IValueAction iva) {
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
