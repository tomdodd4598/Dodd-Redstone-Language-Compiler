package drlc.intermediate.scope;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.action.JumpAction;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.FunctionItemValue;
import drlc.intermediate.routine.Routine;

public class Scope {
	
	private static long idCounter = 0;
	
	private long localCounter = 0;
	
	public final long globalId = idCounter++;
	
	public final Scope parent;
	
	protected final Map<String, Scope> childMap = new LinkedHashMap<>();
	
	protected final Hierarchy<String, Scope> constantShadowMap;
	
	protected final Hierarchy<String, TypeDefinition> typedefMap;
	protected final Hierarchy<String, TypeInfo> typealiasMap;
	protected final Hierarchy<String, Constant> constantMap;
	protected final Hierarchy<String, Variable> variableMap;
	protected final Hierarchy<String, Function> functionMap;
	
	public boolean definiteLocalReturn = false;
	public boolean definiteExecution = true, potentialOuterMultipleExecution = false;
	
	protected final Set<Variable> initializationSet = new HashSet<>();
	
	public Scope(ASTNode<?> node, Scope parent) {
		this.parent = parent;
		
		if (parent == null) {
			constantShadowMap = new Hierarchy<>(null);
			
			typedefMap = new Hierarchy<>(null);
			typealiasMap = new Hierarchy<>(null);
			constantMap = new Hierarchy<>(null);
			variableMap = new Hierarchy<>(null);
			functionMap = new Hierarchy<>(null);
		}
		else {
			String name = getName();
			if (parent.childMap.containsKey(name)) {
				throw Helpers.nodeError(node, "Module name \"%s\" already used in this scope!", name);
			}
			parent.childMap.put(name, this);
			
			constantShadowMap = new Hierarchy<>(parent.constantShadowMap);
			
			typedefMap = new Hierarchy<>(parent.typedefMap);
			typealiasMap = new Hierarchy<>(parent.typealiasMap);
			constantMap = new Hierarchy<>(parent.constantMap);
			variableMap = new Hierarchy<>(parent.variableMap);
			functionMap = new Hierarchy<>(parent.functionMap);
		}
	}
	
	protected String getName() {
		return "\\" + globalId;
	}
	
	public boolean isSubScopeOf(Scope other) {
		return equals(other) || other.childMap.values().stream().anyMatch(x -> isSubScopeOf(x));
	}
	
	public @Nullable FunctionScope getContextFunctionScope() {
		return parent == null ? null : parent.getContextFunctionScope();
	}
	
	public @Nullable Function getContextFunction() {
		return parent == null ? null : parent.getContextFunction();
	}
	
	public long nextLocalId() {
		return localCounter++;
	}
	
	public DataId nextLocalDataId(Routine routine, @NonNull TypeInfo typeInfo) {
		DeclaratorInfo declarator = Helpers.builtInDeclarator("\\r" + nextLocalId(), typeInfo);
		addVariable(null, declarator.variable, false);
		routine.declaratorList.add(declarator);
		return declarator.dataId();
	}
	
	// Contains
	
	public boolean typedefExists(String name, boolean shallow) {
		return typedefMap.containsKey(name, shallow);
	}
	
	public boolean typealiasExists(String name, boolean shallow) {
		return typealiasMap.containsKey(name, shallow);
	}
	
	public boolean constantExists(String name, boolean shallow) {
		Constant constant = constantMap.get(name, shallow);
		Scope shadowScope;
		return constant != null && ((shadowScope = constantShadowMap.get(name, shallow)) == null || !shadowScope.isSubScopeOf(constant.scope));
	}
	
	public boolean variableExists(String name, boolean shallow) {
		return variableMap.containsKey(name, shallow);
	}
	
	public boolean functionExists(String name, boolean shallow) {
		return functionMap.containsKey(name, shallow);
	}
	
	public boolean typeNameCollision(String name) {
		return typedefExists(name, true) || typealiasExists(name, true);
	}
	
	public boolean valueNameCollision(String name) {
		return constantExists(name, true) || variableExists(name, true) || functionExists(name, true);
	}
	
	// Getters
	
	public @NonNull TypeDefinition getTypedef(ASTNode<?> node, String name) {
		TypeDefinition typedef = typedefMap.get(name, false);
		if (typedef == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return typedef;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node, String name) {
		TypeDefinition typedef = typedefMap.get(name, false);
		if (typedef == null) {
			TypeInfo aliasType = typealiasMap.get(name, false);
			if (aliasType == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			return aliasType;
		}
		return typedef.getTypeInfo(node, new ArrayList<>(), this);
	}
	
	public void collectTypedefs(ASTNode<?> node, Set<TypeDefinition> typedefs, String name) {
		TypeDefinition typedef = typedefMap.get(name, false);
		if (typedef == null) {
			TypeInfo aliasType = typealiasMap.get(name, false);
			if (aliasType == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			else {
				aliasType.collectTypedefs(typedefs);
			}
		}
		else {
			typedefs.add(typedef);
		}
	}
	
	@SuppressWarnings("null")
	public @NonNull Constant getConstant(ASTNode<?> node, String name) {
		if (!constantExists(name, false)) {
			throw Helpers.nodeError(node, "Constant \"%s\" not defined in this scope!", name);
		}
		return constantMap.get(name, false);
	}
	
	public @NonNull Variable getVariable(ASTNode<?> node, String name) {
		Variable variable = variableMap.get(name, false);
		if (variable == null) {
			throw Helpers.nodeError(node, "Variable \"%s\" not defined in this scope!", name);
		}
		return variable;
	}
	
	public @NonNull Function getFunction(ASTNode<?> node, String name) {
		Function function = functionMap.get(name, false);
		if (function == null) {
			throw Helpers.nodeError(node, "Function \"%s\" not defined in this scope!", name);
		}
		return function;
	}
	
	// Adders
	
	public void addConstantShadow(String name) {
		constantShadowMap.put(name, this, true);
	}
	
	public void addTypedef(ASTNode<?> node, @NonNull TypeDefinition typedef) {
		String name = typedef.name;
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		typedef.scope = this;
		typedefMap.put(name, typedef, true);
	}
	
	public void addTypealias(ASTNode<?> node, @NonNull String name, @NonNull TypeInfo aliasType) {
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		if (!aliasType.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", aliasType.copy(node));
		}
		
		typealiasMap.put(name, aliasType, true);
	}
	
	public void addConstant(ASTNode<?> node, @NonNull Constant constant, boolean replace) {
		String name = constant.name;
		if (!replace && valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		TypeInfo typeInfo = constant.value.typeInfo;
		if (!typeInfo.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", typeInfo.copy(node));
		}
		
		constant.scope = this;
		constantMap.put(name, constant, true);
	}
	
	public void addVariable(ASTNode<?> node, @NonNull Variable variable, boolean replace) {
		String name = variable.name;
		if (!replace && valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		TypeInfo typeInfo = variable.typeInfo;
		if (!typeInfo.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", typeInfo.copy(node));
		}
		
		variable.scope = this;
		variableMap.put(name, variable, true);
	}
	
	public void addFunction(ASTNode<?> node, @NonNull Function function, boolean replace) {
		String name = function.name;
		if (!replace && valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		for (TypeInfo paramTypeInfo : function.paramTypeInfos) {
			if (!paramTypeInfo.exists(this)) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", paramTypeInfo.copy(node));
			}
		}
		
		TypeInfo returnTypeInfo = function.returnTypeInfo;
		if (!returnTypeInfo.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", returnTypeInfo.copy(node));
		}
		
		function.scope = this;
		functionMap.put(name, function, true);
		FunctionItemValue value = new FunctionItemValue(node, new FunctionItemTypeInfo(node, this, name), name, this);
		addConstant(node, new Constant(name, value), true);
		function.value = value;
	}
	
	// Control flow
	
	public boolean hasDefiniteReturn() {
		return definiteLocalReturn || childMap.values().stream().anyMatch(x -> x.definiteExecution && x.hasDefiniteReturn());
	}
	
	protected @Nullable Scope potentialMultipleExecutionScope() {
		return potentialOuterMultipleExecution ? this : (parent == null ? null : parent.potentialMultipleExecutionScope());
	}
	
	public boolean isBreakable(@Nullable String label) {
		return parent != null && parent.isBreakable(label);
	}
	
	public @NonNull JumpAction getContinueJump(ASTNode<?> node, @Nullable String label) {
		return parent.getContinueJump(node, label);
	}
	
	public @NonNull JumpAction getBreakJump(ASTNode<?> node, @Nullable String label) {
		return parent.getBreakJump(node, label);
	}
	
	// Variable initialization
	
	public void onVariableInitialization(ASTNode<?> node, Variable variable) {
		if (!variable.modifier.mutable && (!Objects.equals(potentialMultipleExecutionScope(), variable.scope.potentialMultipleExecutionScope()) || isVariablePotentiallyInitialized(variable))) {
			throw Helpers.nodeError(node, "Attempted to potentially assign twice to immutable variable \"%s\"!", variable.name);
		}
		initializationSet.add(variable);
	}
	
	public boolean isVariablePotentiallyInitialized(Variable variable) {
		return variable.scope.isVariablePotentiallyInitializedInternal(variable, this);
	}
	
	public boolean isVariableDefinitelyInitialized(Variable variable) {
		return variable.scope.isVariableDefinitelyInitializedInternal(variable, this);
	}
	
	protected boolean isVariablePotentiallyInitializedInternal(Variable variable, Scope location) {
		return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> x.isVariablePotentiallyInitializedInternal(variable, location));
	}
	
	protected boolean isVariableDefinitelyInitializedInternal(Variable variable, Scope location) {
		return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> (x.definiteExecution || location.isSubScopeOf(x)) && x.isVariableDefinitelyInitializedInternal(variable, location));
	}
	
	// Environment capture
	
	public Variable captureVariable(ASTNode<?> node, Variable variable) {
		FunctionScope functionScope = getContextFunctionScope();
		if (!variable.modifier._static && functionScope != null && !functionScope.equals(variable.scope.getContextFunctionScope())) {
			Function function = functionScope.function;
			if (!function.closure) {
				throw Helpers.nodeError(node, "Attempted to capture variable \"%s\" in non-closure function!", variable.name);
			}
			Variable copy = variable.copy();
			functionScope.addVariable(node, copy, false);
			if (isVariableDefinitelyInitialized(variable)) {
				functionScope.initializationSet.add(copy);
			}
			functionScope.function.addCapture(variable, new DeclaratorInfo(copy));
			return copy;
		}
		else {
			return variable;
		}
	}
}
