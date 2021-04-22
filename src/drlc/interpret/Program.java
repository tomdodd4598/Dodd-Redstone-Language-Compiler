package drlc.interpret;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import drlc.Global;
import drlc.Helper;
import drlc.interpret.action.Action;
import drlc.interpret.action.IJumpAction;
import drlc.interpret.routine.FunctionRoutine;
import drlc.interpret.routine.MethodRoutine;
import drlc.interpret.routine.RootRoutine;
import drlc.interpret.routine.Routine;
import drlc.interpret.scope.Scope;
import drlc.interpret.type.VariableReferenceInfo;
import drlc.node.Node;

public class Program {
	
	public final RootRoutine rootRoutine;
	public final Map<String, Routine> routineMap = new LinkedHashMap<>();
	public String currentRoutine = Global.ROOT_ROUTINE;
	
	final List<VariableReferenceInfo> paramList = new ArrayList<>();
	
	public Program() {
		rootRoutine = new RootRoutine(this);
		routineMap.put(Global.ROOT_ROUTINE, rootRoutine);
	}
	
	public Routine currentRoutine() {
		return routineMap.get(currentRoutine);
	}
	
	public void addBuiltInMethods(Node node, Scope scope) {
		for (Entry<String, Integer> entry : Global.BUILT_IN_METHODS.entrySet()) {
			scope.methodName = entry.getKey();
			scope.methodArgs = entry.getValue();
			scope.addMethod(node);
		}
	}
	
	public void addBuiltInFunctions(Node node, Scope scope) {
		for (Entry<String, Integer> entry : Global.BUILT_IN_FUNCTIONS.entrySet()) {
			scope.functionName = entry.getKey();
			scope.functionArgs = entry.getValue();
			scope.addFunction(node);
		}
	}
	
	public void returnToRootRoutine() {
		currentRoutine = Global.ROOT_ROUTINE;
	}
	
	public void createAndSetMethodRoutine(Node node, Scope scope) {
		String methodName = scope.methodName = scope.previous.methodName;
		if (routineMap.containsKey(methodName)) {
			throw new IllegalArgumentException(String.format("Routine \"%s\" already created! %s", methodName, node));
		}
		else {
			currentRoutine = methodName;
			int args = scope.methodArgs = scope.previous.methodArgs;
			VariableReferenceInfo[] paramArray = getParamArray(node, args, true);
			for (Scope s : new Scope[] {scope, scope.previous}) {
				routineMap.put(methodName, new MethodRoutine(this, methodName, s.addMethod(node), paramArray));
			}
		}
	}
	
	public void createAndSetFunctionRoutine(Node node, Scope scope) {
		String functionName = scope.functionName = scope.previous.functionName;
		if (routineMap.containsKey(functionName)) {
			throw new IllegalArgumentException(String.format("Routine \"%s\" already created! %s", functionName, node));
		}
		else {
			currentRoutine = functionName;
			int args = scope.functionArgs = scope.previous.functionArgs;
			VariableReferenceInfo[] paramArray = getParamArray(node, args, true);
			for (Scope s : new Scope[] {scope, scope.previous}) {
				routineMap.put(functionName, new FunctionRoutine(this, functionName, s.addFunction(node), paramArray));
			}
		}
	}
	
	public void addParam(Node node, VariableReferenceInfo param) {
		if (param == null) {
			throw new IllegalArgumentException(String.format("Method or function parameter was null! %s", node));
		}
		else {
			paramList.add(param);
		}
	}
	
	public VariableReferenceInfo[] getParamArray(Node node, int args, boolean clear) {
		if (args != paramList.size()) {
			throw new IllegalArgumentException(String.format("Found %s parameters but expected %s! %s", paramList.size(), args, node));
		}
		else {
			VariableReferenceInfo[] paramArray = paramList.toArray(new VariableReferenceInfo[args]);
			if (clear) {
				paramList.clear();
			}
			return paramArray;
		}
	}
	
	// Preliminary optimization
	
	public void trim() {
		for (Routine routine : routineMap.values()) {
			flattenSections(routine);
		}
	}
	
	static void flattenSections(Routine routine) {
		List<List<Action>> body = routine.getBodyActionLists();
		String _d = Helper.sectionIdString(body.size());
		body.add(routine.getDestructionActionList());
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IJumpAction && ((IJumpAction<?>) action).getTarget().equals(Global.DESTRUCTOR)) {
					list.set(j, ((IJumpAction<?>) action).copy(_d));
				}
			}
		}
	}
	
	// Finalization
	
	public void finalizeRoutines() {
		for (Routine routine : routineMap.values()) {
			routine.setTransientRegisters();
			routine.updateRoutineType();
		}
	}
}
