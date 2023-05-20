package drlc.intermediate;

import drlc.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.constant.Constant;
import drlc.intermediate.component.info.VariableModifierInfo;
import drlc.intermediate.component.type.*;
import drlc.node.Node;

public class Scope {
	
	public final Generator generator;
	
	public final Scope previous;
	
	private final HierarchyMap<String, Type> typeMap;
	private final HierarchyMap<String, Constant> constantMap;
	private final HierarchyMap<String, Variable> variableMap;
	private final HierarchyMap<String, Function> functionMap;
	
	public Boolean expectingFunctionReturn = null;
	
	public Scope(Node node, Generator generator, Scope previous) {
		this.generator = generator;
		this.previous = previous;
		
		if (previous == null) {
			typeMap = new HierarchyMap<>(null);
			constantMap = new HierarchyMap<>(null);
			variableMap = new HierarchyMap<>(null);
			functionMap = new HierarchyMap<>(null);
		}
		else {
			typeMap = new HierarchyMap<>(previous.typeMap);
			constantMap = new HierarchyMap<>(previous.constantMap);
			variableMap = new HierarchyMap<>(previous.variableMap);
			functionMap = new HierarchyMap<>(previous.functionMap);
			
			expectingFunctionReturn = previous.expectingFunctionReturn == null ? null : new Boolean(previous.expectingFunctionReturn.booleanValue());
		}
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
			return typeExists(typeInfo.type.toString());
		}
	}
	
	public boolean typeExists(String name) {
		return typeMap.containsKey(name);
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
	
	public boolean typeNameCollision(Type type) {
		return typeExists(type.toString());
	}
	
	public boolean valueNameCollision(String name) {
		return constantExists(name) || variableExists(name) || functionExists(name);
	}
	
	// Getters
	
	public Type getType(Node node, String name) {
		Type type = typeMap.get(name);
		if (type == null) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", name, node));
		}
		return type;
	}
	
	public Constant getConstant(Node node, String name) {
		Constant constant = constantMap.get(name);
		if (constant == null) {
			throw new IllegalArgumentException(String.format("Constant \"%s\" not defined in this scope! %s", name, node));
		}
		return constant;
	}
	
	public Variable getVariable(Node node, String name) {
		name = Helpers.removeAllDereferences(name);
		Variable variable = variableMap.get(name);
		if (variable == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined in this scope! %s", name, node));
		}
		return variable;
	}
	
	public Function getFunction(Node node, String name) {
		Function function = functionMap.get(name);
		if (function == null) {
			throw new IllegalArgumentException(String.format("Function \"%s\" not defined in this scope! %s", name, node));
		}
		return function;
	}
	
	// Adders
	
	public void addType(Node node, Type type) {
		String typeString;
		if (type == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null type! %s", node));
		}
		else if ((typeString = type.toString()) == null) {
			throw new IllegalArgumentException(String.format("Attempted to create type with null name! %s", node));
		}
		else if (typeNameCollision(type)) {
			throw new IllegalArgumentException(String.format("Type name \"%s\" already used in this scope! %s", typeString, node));
		}
		else {
			typeMap.put(typeString, type, true);
		}
	}
	
	public void addConstant(Node node, Constant constant, boolean replace) {
		TypeInfo typeInfo;
		Type type;
		String typeString;
		if (constant == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null constant! %s", node));
		}
		else if (constant.name == null) {
			throw new IllegalArgumentException(String.format("Attempted to create constant with null name! %s", node));
		}
		else if (!replace && valueNameCollision(constant.name)) {
			throw new IllegalArgumentException(String.format("Name \"%s\" already used in this scope! %s", constant.name, node));
		}
		else if ((typeInfo = constant.typeInfo) == null || (type = typeInfo.type) == null || (typeString = type.toString()) == null) {
			throw new IllegalArgumentException(String.format("Attempted to create constant \"%s\" with null type! %s", constant.name, node));
		}
		else if (!typeExists(typeInfo)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", typeString, node));
		}
		else {
			constantMap.put(constant.name, constant, true);
		}
	}
	
	public void addVariable(Node node, Variable variable, boolean replace) {
		TypeInfo typeInfo;
		Type type;
		String typeString;
		if (variable == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null variable! %s", node));
		}
		else if (variable.name == null) {
			throw new IllegalArgumentException(String.format("Attempted to create variable with null name! %s", node));
		}
		else if (!replace && valueNameCollision(variable.name)) {
			throw new IllegalArgumentException(String.format("Name \"%s\" already used in this scope! %s", variable.name, node));
		}
		else if ((typeInfo = variable.typeInfo) == null || (type = typeInfo.type) == null || (typeString = type.toString()) == null) {
			throw new IllegalArgumentException(String.format("Attempted to create variable \"%s\" with null type! %s", variable.name, node));
		}
		else if (!typeExists(typeInfo)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", typeString, node));
		}
		else {
			variable.scope = typeInfo.isFunction() ? generator.program.rootScope : this;
			variableMap.put(variable.name, variable, true);
		}
	}
	
	public void addFunction(Node node, Function function, boolean replace) {
		TypeInfo typeInfo;
		Type type;
		String typeString;
		if (function == null) {
			throw new IllegalArgumentException(String.format("Attempted to create null function! %s", node));
		}
		else if (function.name == null) {
			throw new IllegalArgumentException(String.format("Attempted to create function with null name! %s", node));
		}
		else if ((typeInfo = function.returnTypeInfo) == null || (type = typeInfo.type) == null || (typeString = type.toString()) == null) {
			throw new IllegalArgumentException(String.format("Attempted to create function \"%s\" with null return type! %s", function.name, node));
		}
		else if (!typeExists(typeInfo)) {
			throw new IllegalArgumentException(String.format("Type \"%s\" not defined in this scope! %s", typeString, node));
		}
		else if (!replace && valueNameCollision(function.name)) {
			throw new IllegalArgumentException(String.format("Name \"%s\" already used in this scope! %s", function.name, node));
		}
		else {
			functionMap.put(function.name, function, true);
			addVariable(node, new Variable(function.name, new VariableModifierInfo(true), new FunctionTypeInfo(node, this, function.name)), true);
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
