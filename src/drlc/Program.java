package drlc;

import java.util.*;

import drlc.intermediate.action.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.value.FunctionItemValue;
import drlc.intermediate.routine.*;

public class Program {
	
	public final Map<String, FunctionRoutine> builtInRoutineMap = new LinkedHashMap<>();
	public final Map<String, Routine> routineMap = new LinkedHashMap<>();
	
	public Program() {
		
	}
	
	public Routine getRoutine(String name) {
		return routineMap.get(name);
	}
	
	public boolean routineExists(String name) {
		return routineMap.containsKey(name);
	}
	
	public boolean routineDefined(String name) {
		return routineExists(name) && getRoutine(name).isDefined();
	}
	
	public void flattenRoutines() {
		for (Routine routine : routineMap.values()) {
			routine.flattenSections();
		}
	}
	
	public void finalizeRoutines() {
		for (Routine routine : routineMap.values()) {
			if (routine.isRootRoutine()) {
				Main.generator.generateRootParams((RootRoutine) routine);
			}
			routine.setTransientRegisters();
			routine.checkFunctionVariableInitialization();
		}
		updateRoutineTypes(Main.rootRoutine, new ArrayList<>(), new HashMap<>(), 0);
	}
	
	public void updateRoutineTypes(Routine routine, List<String> callList, Map<String, Integer> callMap, int index) {
		List<List<Action>> body = routine.getBodyActionLists();
		for (List<Action> list : body) {
			for (Action action : list) {
				if (action instanceof FunctionCallAction) {
					DataId function = ((FunctionCallAction) action).function;
					if (function instanceof ValueDataId) {
						ValueDataId valueData = (ValueDataId) function;
						if (valueData.value instanceof FunctionItemValue) {
							String routineName = ((FunctionItemValue) valueData.value).name;
							if (routineExists(routineName)) {
								if (callMap.containsKey(routine.name)) {
									int k = index, l = callMap.get(routine.name);
									while (--k >= l) {
										getRoutine(callList.get(k)).onRequiresStack();
									}
								}
								else if (routineName.equals(routine.name)) {
									routine.onRequiresStack();
								}
								else {
									Routine nextRoutine = getRoutine(routineName);
									if (!nextRoutine.isBuiltInFunctionRoutine()) {
										routine.onRequiresNesting();
										callList.add(routine.name);
										callMap.put(routine.name, index);
										updateRoutineTypes(nextRoutine, callList, callMap, index + 1);
										callList.remove(index);
										callMap.remove(routine.name);
									}
								}
							}
							else if (Main.rootScope.functionExists(routineName)) {
								throw Helpers.error("Function \"%s\" was not defined!", routineName);
							}
						}
					}
				}
			}
		}
	}
}
