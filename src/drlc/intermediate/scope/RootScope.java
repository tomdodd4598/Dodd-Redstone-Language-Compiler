package drlc.intermediate.scope;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.module.*;
import drlc.intermediate.routine.Routine;

public class RootScope extends ModuleScope {
	
	@SuppressWarnings("null")
	protected @NonNull Hierarchy<String, TypeEntry> preludeTypeEntryHierarchy = null;
	
	@SuppressWarnings("null")
	protected @NonNull Hierarchy<String, ValueEntry> preludeValueEntryHierarchy = null;
	
	@SuppressWarnings("null")
	protected @NonNull Hierarchy<String, Function> preludeFunctionHierarchy = null;
	
	public final Map<Function, Routine> routineMap = new LinkedHashMap<>();
	
	public RootScope(ASTNode<?> node) {
		super(node, Global.ROOT, null);
	}
	
	@Override
	public boolean hasDefiniteReturn() {
		return true;
	}
	
	public void capturePrelude() {
		preludeTypeEntryHierarchy = typeEntryHierarchy.local();
		preludeValueEntryHierarchy = valueEntryHierarchy.local();
		preludeFunctionHierarchy = functionHierarchy.local();
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
		Set<Integer> reachableSections = routine.getReachableSections();
		for (int section = 0; section < routine.body.size(); ++section) {
			if (!reachableSections.contains(section)) {
				continue;
			}
			
			for (Action action : routine.body.get(section)) {
				if (action instanceof CallAction ca) {
					Function callFunction = ca.getDirectFunction();
					if (callFunction == null) {
						routine.onRequiresStack();
					}
					else {
						if (routineExists(callFunction)) {
							Function routineFunction = routine.function;
							if (callMap.containsKey(callFunction)) {
								routine.onRequiresStack();
								int cycleStart = callMap.get(callFunction);
								for (int i = cycleStart; i < depth; ++i) {
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
						else if (!callFunction.builtIn && callFunction.definitionScope != null && callFunction.definitionScope.definesLocalFunction(callFunction)) {
							throw Helpers.error("Function \"%s\" was not defined!", callFunction);
						}
					}
				}
			}
		}
	}
}
