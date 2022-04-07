package drlc.interpret;

import java.util.*;

import drlc.Global;
import drlc.generate.Generator;
import drlc.interpret.action.*;
import drlc.interpret.component.*;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.interpret.routine.*;
import drlc.node.Node;

public class Program {
	
	public final Generator generator;
	
	public Scope rootScope = null;
	public int scopeId = Global.ROOT_SCOPE_ID;
	
	public final RootRoutine rootRoutine;
	private final Map<String, Routine> routineMap = new LinkedHashMap<>();
	private final Map<String, Routine> builtInRoutineMap = new LinkedHashMap<>();
	private String currentRoutine = Global.ROOT_ROUTINE;
	
	private final Stack<List<DeclaratorInfo>> paramListStack = new Stack<>();
	
	public final Map<BinaryOpType, Integer> binaryOpCountMap = new HashMap<>();
	public final Map<UnaryOpType, Integer> unaryOpCountMap = new HashMap<>();
	
	public Program(Generator generator) {
		this.generator = generator;
		rootRoutine = new RootRoutine(this);
		routineMap.put(Global.ROOT_ROUTINE, rootRoutine);
	}
	
	public Map<String, Routine> getRoutineMap() {
		return routineMap;
	}
	
	public Routine currentRoutine() {
		return routineMap.get(currentRoutine);
	}
	
	public Routine getRoutine(String name) {
		return routineMap.get(name);
	}
	
	public boolean routineExists(String name) {
		return routineMap.containsKey(name);
	}
	
	public Map<String, Routine> getBuiltInRoutineMap() {
		return builtInRoutineMap;
	}
	
	public void declareFunction(Node node, Scope scope, String name, int argc, TypeInfo returnTypeInfo) {
		boolean functionDefined = routineExists(name);
		Function newFunction = new Function(node, name, false, new FunctionModifierInfo(false, false), returnTypeInfo, getParamArray(node, argc, true), functionDefined);
		if (scope.functionExists(name)) {
			Function existingFunction = scope.getFunction(node, name);
			if (existingFunction.equals(newFunction)) {
				if (!functionDefined) {
					newFunction.updateFromExistingFunction(existingFunction);
					scope.addFunction(node, newFunction, true);
				}
			}
			else {
				throw new IllegalArgumentException(String.format("Routine declaration \"%s\" incompatible with previous declaration \"%s\"!", newFunction, existingFunction));
			}
		}
		else {
			scope.addFunction(node, newFunction, false);
		}
	}
	
	public void createAndSetFunctionRoutine(Node node, Scope scope, String name, FunctionModifierInfo modifierInfo, int argc, TypeInfo returnTypeInfo) {
		if (routineExists(name)) {
			throw new IllegalArgumentException(String.format("Routine \"%s\" already defined! %s", name, node));
		}
		else {
			currentRoutine = name;
			Function newFunction = new Function(node, name, false, modifierInfo, returnTypeInfo, getParamArray(node, argc, true), true);
			for (Scope sc : new Scope[] {scope, scope.previous}) {
				if (sc.functionExists(name)) {
					Function existingFunction = sc.getFunction(node, name);
					if (existingFunction.equals(newFunction)) {
						newFunction.updateFromExistingFunction(existingFunction);
						sc.addFunction(node, newFunction, true);
					}
					else {
						throw new IllegalArgumentException(String.format("Attempted to give declared routine \"%s\" incompatible definition \"%s\"!", existingFunction, newFunction));
					}
				}
				else {
					sc.addFunction(node, newFunction, false);
				}
			}
			routineMap.put(name, new FunctionRoutine(node, this, name, newFunction));
		}
	}
	
	public void returnToRootRoutine() {
		currentRoutine = Global.ROOT_ROUTINE;
	}
	
	public void addParam(Node node, DeclaratorInfo param) {
		if (param == null) {
			throw new IllegalArgumentException(String.format("Function parameter was null! %s", node));
		}
		else {
			currentParamList(node).add(param);
		}
	}
	
	public DeclaratorInfo[] getParamArray(Node node, int argc, boolean pop) {
		List<DeclaratorInfo> paramList = pop ? popParamList(node) : currentParamList(node);
		if (argc != paramList.size()) {
			throw new IllegalArgumentException(String.format("Found %s parameters but expected %s! %s", paramList.size(), argc, node));
		}
		else {
			return paramList.toArray(new DeclaratorInfo[argc]);
		}
	}
	
	public int currentParamListSize(Node node) {
		return currentParamList(node).size();
	}
	
	public void pushParamList(Node node) {
		paramListStack.push(new ArrayList<>());
	}
	
	public List<DeclaratorInfo> currentParamList(Node node) {
		if (paramListStack.empty()) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to get parameter list! %s", node));
		}
		else {
			return paramListStack.peek();
		}
	}
	
	public List<DeclaratorInfo> popParamList(Node node) {
		if (paramListStack.empty()) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to get parameter list! %s", node));
		}
		else {
			return paramListStack.pop();
		}
	}
	
	// Preliminary optimization
	
	public void fixUp() {
		for (Routine routine : routineMap.values()) {
			routine.flattenSections();
			routine.fixUpLabelJumps();
		}
	}
	
	// Finalization
	
	public void finalizeRoutines() {
		for (Routine routine : routineMap.values()) {
			if (routine.isRootRoutine()) {
				generator.generateRootParams((RootRoutine) routine);
			}
			routine.setTransientRegisters();
			routine.checkInvalidDataIds();
			routine.checkFunctionVariableInitialization();
		}
		countOperators();
		updateRoutineTypes(rootRoutine, new ArrayList<>(), new HashMap<>(), 0);
	}
	
	public void countOperators() {
		for (Routine routine : routineMap.values()) {
			for (int i = 0; i < routine.getBodyActionLists().size(); i++) {
				List<Action> list = routine.getBodyActionLists().get(i);
				for (int j = 0; j < list.size(); j++) {
					Action action = list.get(j);
					if (action instanceof BinaryOpAction) {
						BinaryOpAction boa = (BinaryOpAction) action;
						binaryOpCountMap.put(boa.opType, binaryOpCountMap.getOrDefault(boa.opType, 0) + 1);
					}
					else if (action instanceof UnaryOpAction) {
						UnaryOpAction uoa = (UnaryOpAction) action;
						unaryOpCountMap.put(uoa.opType, binaryOpCountMap.getOrDefault(uoa.opType, 0) + 1);
					}
				}
			}
		}
	}
	
	public void updateRoutineTypes(Routine routine, List<String> callList, Map<String, Integer> callMap, int index) {
		for (int i = 0; i < routine.getBodyActionLists().size(); i++) {
			List<Action> list = routine.getBodyActionLists().get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof FunctionCallAction) {
					String routineName = ((FunctionCallAction) action).getCallId().raw;
					if (routineExists(routineName)) {
						if (callMap.containsKey(routine.name)) {
							int k = index, l = callMap.get(routine.name);
							while (--k >= l) {
								getRoutine(callList.get(k)).onRequiresStack(false);
							}
						}
						else if (routineName.equals(routine.name)) {
							routine.onRequiresStack(false);
						}
						else {
							Routine nextRoutine = getRoutine(routineName);
							if (!nextRoutine.isBuiltInFunctionRoutine()) {
								routine.onRequiresNesting(false);
								callList.add(routine.name);
								callMap.put(routine.name, index);
								updateRoutineTypes(nextRoutine, callList, callMap, index + 1);
								callList.remove(index);
								callMap.remove(routine.name);
							}
						}
					}
					else if (rootScope.functionExists(routineName)) {
						throw new IllegalArgumentException(String.format("Function \"%s\" was not defined!", routineName));
					}
				}
			}
		}
	}
	
	// Op Counts
	
	public int getBinaryOpCount(BinaryOpType type) {
		return binaryOpCountMap.getOrDefault(type, 0);
	}
	
	public int getUnaryOpCount(UnaryOpType type) {
		return unaryOpCountMap.getOrDefault(type, 0);
	}
}
