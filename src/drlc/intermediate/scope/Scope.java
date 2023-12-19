package drlc.intermediate.scope;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.action.JumpAction;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.FunctionItemValue;

public abstract class Scope {
	
	private static long idCounter = 0;
	
	public final long globalId = idCounter++;
	
	public final Scope parent;
	
	protected final List<Scope> children = new ArrayList<>();
	
	protected final HierarchyMap<String, RawType> rawTypeMap;
	protected final HierarchyMap<String, Constant> constantMap;
	protected final HierarchyMap<String, Variable> variableMap;
	protected final HierarchyMap<String, Function> functionMap;
	
	public boolean definiteLocalReturn = false;
	
	public Scope(Scope parent) {
		this.parent = parent;
		
		if (parent == null) {
			rawTypeMap = new HierarchyMap<>(null);
			constantMap = new HierarchyMap<>(null);
			variableMap = new HierarchyMap<>(null);
			functionMap = new HierarchyMap<>(null);
		}
		else {
			parent.children.add(this);
			rawTypeMap = new HierarchyMap<>(parent.rawTypeMap);
			constantMap = new HierarchyMap<>(parent.constantMap);
			variableMap = new HierarchyMap<>(parent.variableMap);
			functionMap = new HierarchyMap<>(parent.functionMap);
		}
	}
	
	public boolean rawTypeExists(String name) {
		return rawTypeMap.containsKey(name);
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
	
	public boolean rawTypeNameCollision(String name) {
		return rawTypeExists(name);
	}
	
	public boolean valueNameCollision(String name) {
		return constantExists(name) || variableExists(name) || functionExists(name);
	}
	
	// Getters
	
	public @NonNull RawType getRawType(ASTNode<?, ?> node, String name) {
		RawType rawType = rawTypeMap.get(name);
		if (rawType == null) {
			throw Helpers.nodeError(node, "Raw type \"%s\" not defined in this scope!", name);
		}
		return rawType;
	}
	
	public @NonNull Constant getConstant(ASTNode<?, ?> node, String name) {
		Constant constant = constantMap.get(name);
		if (constant == null) {
			throw Helpers.nodeError(node, "Constant \"%s\" not defined in this scope!", name);
		}
		return constant;
	}
	
	public @NonNull Variable getVariable(ASTNode<?, ?> node, String name) {
		name = Helpers.removeAllDereferences(name);
		Variable variable = variableMap.get(name);
		if (variable == null) {
			throw Helpers.nodeError(node, "Variable \"%s\" not defined in this scope!", name);
		}
		return variable;
	}
	
	public @NonNull Function getFunction(ASTNode<?, ?> node, String name) {
		Function function = functionMap.get(name);
		if (function == null) {
			throw Helpers.nodeError(node, "Function \"%s\" not defined in this scope!", name);
		}
		return function;
	}
	
	// Adders
	
	public void addRawType(ASTNode<?, ?> node, @NonNull RawType rawType) {
		String name = rawType.name;
		if (rawTypeNameCollision(name)) {
			throw Helpers.nodeError(node, "Raw type name \"%s\" already used in this scope!", rawType);
		}
		rawTypeMap.put(name, rawType, true);
	}
	
	public void addConstant(ASTNode<?, ?> node, @NonNull Constant constant, boolean replace) {
		String name = constant.name;
		if (!replace && valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		TypeInfo typeInfo = constant.value.typeInfo;
		if (!typeInfo.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", typeInfo.copy(node, 0));
		}
		
		constant.scope = this;
		constantMap.put(name, constant, true);
	}
	
	public void addVariable(ASTNode<?, ?> node, @NonNull Variable variable, boolean replace) {
		String name = variable.name;
		if (!replace && valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		TypeInfo typeInfo = variable.typeInfo;
		if (!typeInfo.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", typeInfo.copy(node, 0));
		}
		
		variable.scope = this;
		variableMap.put(name, variable, true);
	}
	
	public void addFunction(ASTNode<?, ?> node, @NonNull Function function, boolean replace) {
		String functionName = function.name;
		if (!replace && valueNameCollision(functionName)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", functionName);
		}
		
		for (TypeInfo paramTypeInfo : function.paramTypeInfos) {
			if (!paramTypeInfo.exists(this)) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", paramTypeInfo.copy(node, 0));
			}
		}
		
		TypeInfo returnTypeInfo = function.returnTypeInfo;
		if (!returnTypeInfo.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", returnTypeInfo.copy(node, 0));
		}
		
		functionMap.put(functionName, function, true);
		addConstant(node, new Constant(functionName, new FunctionItemValue(node, new FunctionItemTypeInfo(node, this, functionName), functionName)), true);
	}
	
	// Control flow
	
	public abstract boolean checkCompleteReturn();
	
	public boolean isBreakable(@Nullable String label) {
		return parent != null && parent.isBreakable(label);
	}
	
	public @NonNull JumpAction getContinueJump(ASTNode<?, ?> node, @Nullable String label) {
		return parent.getContinueJump(node, label);
	}
	
	public @NonNull JumpAction getBreakJump(ASTNode<?, ?> node, @Nullable String label) {
		return parent.getBreakJump(node, label);
	}
}
