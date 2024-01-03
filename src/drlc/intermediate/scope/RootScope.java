package drlc.intermediate.scope;

import java.util.*;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.component.data.ValueDataId;
import drlc.intermediate.component.value.FunctionItemValue;
import drlc.intermediate.routine.Routine;

public class RootScope extends Scope {
	
	public RootScope() {
		super(null);
		definiteLocalReturn = true;
	}
	
	@Override
	public boolean checkCompleteReturn() {
		return true;
	}
	
	public void flattenRoutines() {
		forEachRoutine(Routine::flattenSections, true);
	}
	
	public void finalizeRoutines() {
		for (Routine routine : routineIterable(true)) {
			routine.setTransientRegisters();
			routine.checkFunctionVariableInitialization();
		}
		updateRoutineTypes(Main.rootRoutine, new ArrayList<>(), new HashMap<>(), 0);
	}
	
	public void updateRoutineTypes(Routine routine, List<String> callList, Map<String, Integer> callMap, int depth) {
		List<List<Action>> body = routine.getBodyActionLists();
		for (List<Action> list : body) {
			for (Action action : list) {
				if (action instanceof FunctionCallAction) {
					FunctionCallAction fca = (FunctionCallAction) action;
					if (fca.function instanceof ValueDataId) {
						ValueDataId valueData = (ValueDataId) fca.function;
						if (valueData.value instanceof FunctionItemValue) {
							Scope callScope = fca.scope;
							String callName = ((FunctionItemValue) valueData.value).name;
							if (callScope.routineExists(callName, false)) {
								String routineName = routine.name;
								if (callMap.containsKey(routineName)) {
									int i = depth, j = callMap.get(routineName);
									while (--i >= j) {
										callScope.getRoutine(null, callList.get(i)).onRequiresStack();
									}
								}
								else if (callName.equals(routineName)) {
									routine.onRequiresStack();
								}
								else {
									routine.onRequiresNesting();
									
									Routine nextRoutine = callScope.getRoutine(null, callName);
									if (!nextRoutine.isBuiltInFunctionRoutine()) {
										callList.add(routineName);
										callMap.put(routineName, depth);
										updateRoutineTypes(nextRoutine, callList, callMap, depth + 1);
										callList.remove(depth);
										callMap.remove(routineName);
									}
								}
							}
							else if (callScope.functionExists(callName, false)) {
								throw Helpers.error("Function \"%s\" was not defined!", callName);
							}
						}
					}
				}
			}
		}
	}
}
