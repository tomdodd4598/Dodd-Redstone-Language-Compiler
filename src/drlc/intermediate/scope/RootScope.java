package drlc.intermediate.scope;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.ValueDataId;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;

public class RootScope extends ModuleScope {
	
	public final Map<Function, Routine> routineMap = new LinkedHashMap<>();
	
	public RootScope(ASTNode<?> node) {
		super(node, Global.ROOT, null);
	}
	
	@Override
	public boolean hasDefiniteReturn() {
		return true;
	}
	
	public boolean routineExists(Function function) {
		return routineMap.containsKey(function);
	}
	
	public void removeRoutine(ASTNode<?> node, Function function) {
		Routine routine = routineMap.remove(function);
		if (routine == null) {
			throw Helpers.nodeError(node, "Routine function \"%s\" not defined!", function);
		}
	}
	
	public @NonNull Routine getRoutine(ASTNode<?> node, Function function) {
		Routine routine = routineMap.get(function);
		if (routine == null) {
			throw Helpers.nodeError(node, "Routine function \"%s\" not defined!", function);
		}
		return routine;
	}
	
	public void addRoutine(ASTNode<?> node, @NonNull Routine routine) {
		@NonNull Function function = routine.function;
		if (routineExists(function)) {
			throw Helpers.nodeError(node, "Routine function \"%s\" already used!", function);
		}
		routineMap.put(function, routine);
	}
	
	public void flattenRoutines() {
		for (Routine routine : routineMap.values()) {
			routine.flattenSections();
		}
	}
	
	public void finalizeRoutines() {
		for (Routine routine : routineMap.values()) {
			routine.setTransientRegisters();
			routine.checkFunctionVariableInitialization();
		}
		updateRoutineTypes(Main.rootRoutine, new ArrayList<>(), new HashMap<>(), 0);
	}
	
	public void updateRoutineTypes(Routine routine, List<Function> callList, Map<Function, Integer> callMap, int depth) {
		for (List<Action> list : routine.body) {
			for (Action action : list) {
				if (action instanceof CallAction) {
					CallAction fca = (CallAction) action;
					if (fca.caller instanceof ValueDataId) {
						Value<?> value = ((ValueDataId) fca.caller).value;
						if (value instanceof FunctionItemValue) {
							Function callFunction = ((FunctionItemValue) value).typeInfo.function;
							if (routineExists(callFunction)) {
								Function routineFunction = routine.function;
								if (callMap.containsKey(routineFunction)) {
									int i = depth, j = callMap.get(routineFunction);
									while (--i >= j) {
										getRoutine(null, callList.get(i)).onRequiresStack();
									}
								}
								else if (callFunction.equals(routineFunction)) {
									routine.onRequiresStack();
								}
								else {
									routine.onRequiresNesting();
									
									Routine callRoutine = getRoutine(null, callFunction);
									if (!callRoutine.isBuiltIn()) {
										callList.add(routineFunction);
										callMap.put(routineFunction, depth);
										updateRoutineTypes(callRoutine, callList, callMap, depth + 1);
										callList.remove(depth);
										callMap.remove(routineFunction);
									}
								}
							}
							else if (fca.scope.functionExists(callFunction.name, false)) {
								throw Helpers.error("Function \"%s\" was not defined!", callFunction);
							}
						}
					}
				}
			}
		}
	}
}
