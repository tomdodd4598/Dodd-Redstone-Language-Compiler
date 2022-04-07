package drlc.interpret;

import java.util.*;

import drlc.*;
import drlc.interpret.component.*;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.type.*;
import drlc.node.Node;

public class Scope {
	
	public final Program program;
	public final Scope previous;
	public final int id;
	
	private final Map<String, Type> typeMap = new HashMap<>();
	private final Map<String, Constant> constantMap = new HashMap<>();
	private final Map<String, Variable> variableMap = new HashMap<>();
	private final Map<String, Function> functionMap = new HashMap<>();
	public final Set<String> nameSet = new HashSet<>();
	
	public Boolean expectingFunctionReturn = null;
	
	public Scope(Node node, Program program, Scope previous) {
		this.program = program;
		this.previous = previous;
		id = program.scopeId++;
		if (previous != null) {
			typeMap.putAll(previous.typeMap);
			constantMap.putAll(previous.constantMap);
			variableMap.putAll(previous.variableMap);
			functionMap.putAll(previous.functionMap);
			nameSet.addAll(previous.nameSet);
			
			expectingFunctionReturn = previous.expectingFunctionReturn == null ? null : new Boolean(previous.expectingFunctionReturn.booleanValue());
		}
	}
	
	public boolean typeExists(String name) {
		return typeMap.containsKey(name);
	}
	
	public boolean typeExists(TypeInfo typeInfo) {
		if (typeInfo.isFunction()) {
			FunctionTypeInfo functionTypeInfo = (FunctionTypeInfo) typeInfo;
			if (typeExists(functionTypeInfo.returnTypeInfo)) {
				for (TypeInfo info : functionTypeInfo.paramTypeInfos) {
					if (!typeExists(info)) {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return typeExists(typeInfo.typeString());
		}
	}
	
	public boolean constantExists(String name) {
		return constantMap.containsKey(name);
	}
	
	public boolean variableExists(String name) {
		return variableMap.containsKey(name);
	}
	
	public boolean functionExists(String name) {
		return functionMap.containsKey(name);
	}
	
	// Getters
	
	public Type getType(Node node, String name) {
		if (!typeExists(name)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", name, node));
		}
		return typeMap.get(name);
	}
	
	public Constant getConstant(Node node, String name) {
		if (!constantExists(name)) {
			throw new IllegalArgumentException(String.format("Constant \"%s\" not defined in this scope! %s", name, node));
		}
		return constantMap.get(name);
	}
	
	public Variable getVariable(Node node, String name) {
		name = Helpers.removeAllDereferences(name);
		if (!variableExists(name)) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined in this scope! %s", name, node));
		}
		return variableMap.get(name);
	}
	
	public Function getFunction(Node node, String name) {
		if (!functionExists(name)) {
			throw new IllegalArgumentException(String.format("Function \"%s\" not defined in this scope! %s", name, node));
		}
		return functionMap.get(name);
	}
	
	// Adders
	
	public void addType(Node node, Type type) {
		if (type == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null type! %s", node));
		}
		else if (type.toString() == null) {
			throw new IllegalArgumentException(String.format("Attempted to create type with null name! %s", node));
		}
		else if (typeExists(type.toString())) {
			throw new IllegalArgumentException(String.format("Type name \"%s\" already used in this scope! %s", type.toString(), node));
		}
		else {
			typeMap.put(type.toString(), type);
		}
	}
	
	public void addConstant(Node node, Constant constant, boolean replace) {
		if (constant == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null constant! %s", node));
		}
		else if (constant.name == null) {
			throw new IllegalArgumentException(String.format("Attempted to create constant with null name! %s", node));
		}
		else if (!replace && nameSet.contains(constant.name)) {
			throw new IllegalArgumentException(String.format("Name \"%s\" already used in this scope! %s", constant.name, node));
		}
		else if (constant.typeInfo == null || constant.typeInfo.type == null || constant.typeInfo.typeString() == null) {
			throw new IllegalArgumentException(String.format("Attempted to create constant \"%s\" with null type! %s", constant.name, node));
		}
		else if (!typeExists(constant.typeInfo)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", constant.typeInfo.typeString(), node));
		}
		else {
			constantMap.put(constant.name, constant);
			nameSet.add(constant.name);
		}
	}
	
	public void addVariable(Node node, Variable variable) {
		if (variable == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null variable! %s", node));
		}
		else if (variable.name == null) {
			throw new IllegalArgumentException(String.format("Attempted to create variable with null name! %s", node));
		}
		else if (nameSet.contains(variable.name)) {
			throw new IllegalArgumentException(String.format("Name \"%s\" already used in this scope! %s", variable.name, node));
		}
		else if (variable.baseTypeInfo == null || variable.baseTypeInfo.type == null || variable.baseTypeInfo.typeString() == null) {
			throw new IllegalArgumentException(String.format("Attempted to create variable \"%s\" with null type! %s", variable.name, node));
		}
		else if (!typeExists(variable.baseTypeInfo)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", variable.baseTypeInfo.typeString(), node));
		}
		else {
			variable.scopeId = variable.baseTypeInfo.isFunction() ? Global.ROOT_SCOPE_ID : id;
			variableMap.put(variable.name, variable);
			nameSet.add(variable.name);
		}
	}
	
	public void addFunction(Node node, Function function, boolean replace) {
		if (function == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null function! %s", node));
		}
		else if (function.name == null) {
			throw new IllegalArgumentException(String.format("Attempted to create function with null name! %s", node));
		}
		else if (function.returnTypeInfo == null || function.returnTypeInfo.type == null || function.returnTypeInfo.typeString() == null) {
			throw new IllegalArgumentException(String.format("Attempted to create function \"%s\" with null return type! %s", function.name, node));
		}
		else if (!typeExists(function.returnTypeInfo)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", function.returnTypeInfo.typeString(), node));
		}
		else if (!replace && nameSet.contains(function.name)) {
			throw new IllegalArgumentException(String.format("Name \"%s\" already used in this scope! %s", function.name, node));
		}
		else {
			functionMap.put(function.name, function);
			nameSet.remove(function.name);
			addVariable(node, new Variable(function.name, new VariableModifierInfo(true, false, true), new FunctionTypeInfo(node, this, function.name)));
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
