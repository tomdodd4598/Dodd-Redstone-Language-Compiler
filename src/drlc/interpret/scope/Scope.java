package drlc.interpret.scope;

import java.util.*;

import drlc.Helper;
import drlc.interpret.type.*;
import drlc.node.Node;

public class Scope {
	
	public final Scope previous;
	
	private final Map<String, Constant> constantMap = new HashMap<>();
	private final Map<String, Variable> variableMap = new HashMap<>();
	private final Map<String, Method> methodMap = new HashMap<>();
	private final Map<String, Function> functionMap = new HashMap<>();
	private final Set<String> nameSet = new HashSet<>();
	
	public Boolean expectingFunctionReturn = null;
	
	public Scope(Node node, Scope previous) {
		this.previous = previous;
		if (previous != null) {
			constantMap.putAll(previous.constantMap);
			variableMap.putAll(previous.variableMap);
			methodMap.putAll(previous.methodMap);
			functionMap.putAll(previous.functionMap);
			nameSet.addAll(previous.nameSet);
			
			expectingFunctionReturn = previous.expectingFunctionReturn == null ? null : new Boolean(previous.expectingFunctionReturn.booleanValue());
		}
	}
	
	public boolean constantExists(String name) {
		return constantMap.containsKey(name);
	}
	
	public boolean variableExists(String name) {
		return variableMap.containsKey(name);
	}
	
	public boolean methodExists(String name) {
		return methodMap.containsKey(name);
	}
	
	public boolean functionExists(String name) {
		return functionMap.containsKey(name);
	}
	
	// Getters
	
	public Constant getConstant(Node node, String name) {
		if (!constantExists(name)) {
			throw new IllegalArgumentException(String.format("Constant \"%s\" not defined! %s", name, node));
		}
		return constantMap.get(name);
	}
	
	public Variable getVariable(Node node, String name) {
		name = Helper.fullyDereference(name);
		if (!variableExists(name)) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined! %s", name, node));
		}
		return variableMap.get(name);
	}
	
	public Method getMethod(Node node, String name) {
		if (!methodExists(name)) {
			throw new IllegalArgumentException(String.format("Method \"%s\" not defined! %s", name, node));
		}
		return methodMap.get(name);
	}
	
	public Function getFunction(Node node, String name) {
		if (!functionExists(name)) {
			throw new IllegalArgumentException(String.format("Function \"%s\" not defined! %s", name, node));
		}
		return functionMap.get(name);
	}
	
	// Adders
	
	public Constant addConstant(Node node, String name, int value) {
		if (name == null) {
			throw new IllegalArgumentException(String.format("Constant created with null name! %s", node));
		}
		else if (nameSet.contains(name)) {
			throw new IllegalArgumentException(String.format("Constant name \"%s\" already used in this scope! %s", name, node));
		}
		else {
			Constant constant = new Constant(name, value);
			constantMap.put(name, constant);
			nameSet.add(name);
			return constant;
		}
	}
	
	public Variable addVariable(Node node, Variable variable) {
		if (variable == null) {
			throw new IllegalArgumentException(String.format("Attempted to add null variable! %s", node));
		}
		else if (variable.name == null) {
			throw new IllegalArgumentException(String.format("Variable created with null name! %s", node));
		}
		else if (nameSet.contains(variable.name)) {
			throw new IllegalArgumentException(String.format("Variable name \"%s\" already used in this scope! %s", variable.name, node));
		}
		else if (variable.baseReferenceLevel < 0) {
			throw new IllegalArgumentException(String.format("Variable reference level \"%s\" is negative! %s", variable.name, node));
		}
		else {
			variableMap.put(variable.name, variable);
			nameSet.add(variable.name);
			return variable;
		}
	}
	
	public String methodName = null;
	public Integer methodArgs = null;
	
	public Method addMethod(Node node) {
		if (methodName == null) {
			throw new IllegalArgumentException(String.format("Method created with null name! %s", node));
		}
		else if (methodArgs == null) {
			throw new IllegalArgumentException(String.format("Method \"%s\" created with null argument count! %s", methodName, node));
		}
		else if (nameSet.contains(methodName)) {
			throw new IllegalArgumentException(String.format("Method name \"%s\" already used in this scope! %s", methodName, node));
		}
		else {
			Method method = new Method(methodName, methodArgs);
			methodMap.put(methodName, method);
			nameSet.add(methodName);
			methodName = null;
			methodArgs = null;
			return method;
		}
	}
	
	public String functionName = null;
	public Integer functionArgs = null;
	
	public Function addFunction(Node node) {
		if (functionName == null) {
			throw new IllegalArgumentException(String.format("Function created with null name! %s", node));
		}
		else if (functionArgs == null) {
			throw new IllegalArgumentException(String.format("Function \"%s\" created with null argument count! %s", functionName, node));
		}
		else if (nameSet.contains(functionName)) {
			throw new IllegalArgumentException(String.format("Function name \"%s\" already used in this scope! %s", functionName, node));
		}
		else {
			Function function = new Function(functionName, functionArgs);
			functionMap.put(functionName, function);
			nameSet.add(functionName);
			functionName = null;
			functionArgs = null;
			return function;
		}
	}
	
	// Function section return
	
	public void setExpectingFunctionReturn(boolean expectFunctionReturn) {
		expectingFunctionReturn = expectFunctionReturn;
	}
	
	public void checkExpectingFunctionReturn(Node node, boolean expectFunctionReturn) {
		if (expectingFunctionReturn == null) {
			throw new IllegalArgumentException(String.format("Function return boolean is null! %s", node));
		}
		else if (expectFunctionReturn != expectingFunctionReturn) {
			throw new IllegalArgumentException(String.format("Function return boolean is %s when expected to be %s! %s", expectingFunctionReturn, expectFunctionReturn, node));
		}
		else {
			expectingFunctionReturn = false;
		}
	}
}
