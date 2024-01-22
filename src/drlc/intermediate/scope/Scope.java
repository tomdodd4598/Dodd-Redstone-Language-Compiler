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
	
	private static long globalCounter = 0;
	private long localCounter = 0;
	
	public final long globalId = globalCounter++;
	
	public final @NonNull String name;
	public final @Nullable Scope parent;
	public final boolean pseudo;
	
	public final boolean isModule;
	
	public final Map<String, Scope> childMap = new LinkedHashMap<>();
	
	protected final Hierarchy<String, Scope> constantShadowMap;
	
	public final Hierarchy<String, TypeDef> typeDefMap;
	public final Hierarchy<String, TypeInfo> typealiasMap;
	public final Hierarchy<String, Constant> constantMap;
	public final Hierarchy<String, Variable> variableMap;
	public final Hierarchy<String, Function> functionMap;
	
	public boolean definiteLocalReturn = false;
	public boolean definiteExecution = true, potentialOuterMultipleExecution = false;
	
	protected final Set<Variable> initializationSet = new HashSet<>();
	
	public Scope(ASTNode<?> node, @Nullable String name, @Nullable Scope parent, boolean pseudo) {
		this.name = name == null ? "\\" + globalId : name;
		this.parent = parent;
		this.pseudo = pseudo;
		isModule = name != null;
		
		if (parent == null) {
			constantShadowMap = new Hierarchy<>(null);
			
			typeDefMap = new Hierarchy<>(null);
			typealiasMap = new Hierarchy<>(null);
			constantMap = new Hierarchy<>(null);
			variableMap = new Hierarchy<>(null);
			functionMap = new Hierarchy<>(null);
		}
		else {
			parent.addChild(node, this.name, this);
			
			constantShadowMap = new Hierarchy<>(parent.constantShadowMap);
			
			typeDefMap = new Hierarchy<>(parent.typeDefMap);
			typealiasMap = new Hierarchy<>(parent.typealiasMap);
			constantMap = new Hierarchy<>(parent.constantMap);
			variableMap = new Hierarchy<>(parent.variableMap);
			functionMap = new Hierarchy<>(parent.functionMap);
		}
	}
	
	public boolean childExists(String name) {
		return childMap.containsKey(name);
	}
	
	public @NonNull Scope getChild(ASTNode<?> node, String name) {
		Scope scope = childMap.get(name);
		if (scope == null) {
			throw Helpers.nodeError(node, "Module \"%s\" not defined in this scope!", name);
		}
		return scope;
	}
	
	public void addChild(ASTNode<?> node, @NonNull String name, @NonNull Scope scope) {
		if (childExists(name)) {
			throw Helpers.nodeError(node, "Module name \"%s\" already used in this scope!", name);
		}
		else if (name.equals(Global.ROOT)) {
			throw Helpers.nodeError(node, "Root import must be aliased!");
		}
		childMap.put(name, scope);
	}
	
	public boolean isSubScopeOf(Scope other) {
		return equals(other) || other.childMap.values().stream().anyMatch(x -> isSubScopeOf(x));
	}
	
	public void pathAction(ASTNode<?> node, List<String> path, java.util.function.BiConsumer<Scope, String> consumer) {
		consumer.accept(getPathScope(node, path), path.get(path.size() - 1));
	}
	
	public <T> T pathGet(ASTNode<?> node, List<String> path, java.util.function.BiFunction<Scope, String, T> function) {
		return function.apply(getPathScope(node, path), path.get(path.size() - 1));
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
	
	public @NonNull DataId nextLocalDataId(Routine routine, @NonNull TypeInfo typeInfo) {
		DeclaratorInfo declarator = Helpers.builtInDeclarator("\\r" + nextLocalId(), typeInfo);
		addVariable(null, declarator.variable);
		routine.declaratorList.add(declarator);
		return declarator.dataId();
	}
	
	// Contains
	
	public boolean typeDefExists(String name, boolean shallow) {
		return typeDefMap.containsKey(name, shallow);
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
		return typeDefExists(name, true) || typealiasExists(name, true);
	}
	
	public boolean valueNameCollision(String name) {
		return constantExists(name, true) || variableExists(name, true) || functionExists(name, true);
	}
	
	// Getters
	
	public @NonNull Scope getCurrentModule() {
		return isModule ? this : (parent == null ? Main.rootScope : parent.getCurrentModule());
	}
	
	public @NonNull Scope getConcreteScope() {
		return !pseudo ? this : (parent == null ? Main.rootScope : parent.getConcreteScope());
	}
	
	public @NonNull Scope getSuperModule(ASTNode<?> node) {
		@NonNull Scope module = getCurrentModule();
		if (module.parent == null) {
			throw Helpers.nodeError(node, "Could not find \"%s\" in \"%s\"!", Global.SUPER, module.name);
		}
		return module.parent.getCurrentModule();
	}
	
	@SuppressWarnings("null")
	public @NonNull Scope getPathScope(ASTNode<?> node, List<String> path) {
		if (path.isEmpty()) {
			throw Helpers.nodeError(node, "Unexpectedly encountered empty path!");
		}
		
		int count = path.size();
		if (count == 1) {
			return this;
		}
		
		String first = path.get(0);
		@NonNull Scope pathScope = getConcreteScope();
		if (!pathScope.childExists(first.equals(Global.SELF) ? path.get(1) : first)) {
			pathScope = getCurrentModule();
		}
		
		for (int i = 0; i < count - 1; ++i) {
			String segment = path.get(i);
			if (segment.equals(Global.ROOT)) {
				pathScope = Main.rootScope;
			}
			else if (segment.equals(Global.SUPER)) {
				pathScope = pathScope.getSuperModule(node);
			}
			else if (segment.equals(Global.SELF)) {
				pathScope = pathScope.getCurrentModule();
			}
			else if (pathScope.childExists(segment)) {
				pathScope = pathScope.childMap.get(segment);
			}
			else {
				String scopeDescription = pathScope.isModule ? "\"" + pathScope.name + "\"" : "this scope";
				throw Helpers.nodeError(node, "Could not find \"%s\" in %s!", segment, scopeDescription);
			}
		}
		return pathScope;
	}
	
	public @NonNull TypeDef getTypeDef(ASTNode<?> node, String name, boolean shallow) {
		TypeDef typeDef = typeDefMap.get(name, shallow);
		if (typeDef == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return typeDef;
	}
	
	public @NonNull TypeInfo getTypealias(ASTNode<?> node, String name, boolean shallow) {
		TypeInfo typealias = typealiasMap.get(name, shallow);
		if (typealias == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return typealias;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node, String name, boolean shallow) {
		TypeDef typeDef = typeDefMap.get(name, shallow);
		if (typeDef == null) {
			TypeInfo aliasType = typealiasMap.get(name, shallow);
			if (aliasType == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			return aliasType;
		}
		return typeDef.getTypeInfo(node, new ArrayList<>(), this);
	}
	
	public void collectTypeDefs(ASTNode<?> node, Set<TypeDef> typeDefs, String name) {
		TypeDef typeDef = typeDefMap.get(name, false);
		if (typeDef == null) {
			TypeInfo aliasType = typealiasMap.get(name, false);
			if (aliasType == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			else {
				aliasType.collectTypeDefs(typeDefs);
			}
		}
		else {
			typeDefs.add(typeDef);
		}
	}
	
	@SuppressWarnings("null")
	public @NonNull Constant getConstant(ASTNode<?> node, String name, boolean shallow) {
		if (!constantExists(name, shallow)) {
			throw Helpers.nodeError(node, "Constant \"%s\" not defined in this scope!", name);
		}
		return constantMap.get(name, shallow);
	}
	
	public @NonNull Variable getVariable(ASTNode<?> node, String name, boolean shallow) {
		Variable variable = variableMap.get(name, shallow);
		if (variable == null) {
			throw Helpers.nodeError(node, "Variable \"%s\" not defined in this scope!", name);
		}
		return variable;
	}
	
	public @NonNull Function getFunction(ASTNode<?> node, String name, boolean shallow) {
		Function function = functionMap.get(name, shallow);
		if (function == null) {
			throw Helpers.nodeError(node, "Function \"%s\" not defined in this scope!", name);
		}
		return function;
	}
	
	// Adders
	
	public void addConstantShadow(String name) {
		constantShadowMap.put(name, this, true);
	}
	
	public void addTypeDef(ASTNode<?> node, @NonNull TypeDef typeDef) {
		addTypeDef(node, typeDef.name, typeDef);
	}
	
	public void addTypeDef(ASTNode<?> node, @NonNull String name, @NonNull TypeDef typeDef) {
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		if (typeDef.scope == null) {
			typeDef.scope = this;
		}
		typeDefMap.put(name, typeDef, true);
	}
	
	public void addTypealias(ASTNode<?> node, @NonNull String name, @NonNull TypeInfo aliasType) {
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		typealiasMap.put(name, aliasType, true);
	}
	
	public void addConstant(ASTNode<?> node, @NonNull Constant constant) {
		addConstant(node, constant.name, constant);
	}
	
	public void addConstant(ASTNode<?> node, @NonNull String name, @NonNull Constant constant) {
		if (valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		if (constant.scope == null) {
			constant.scope = this;
		}
		constantMap.put(name, constant, true);
	}
	
	public void addVariable(ASTNode<?> node, @NonNull Variable variable) {
		addVariable(node, variable.name, variable);
	}
	
	public void addVariable(ASTNode<?> node, @NonNull String name, @NonNull Variable variable) {
		if (valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		if (variable.scope == null) {
			variable.scope = this;
		}
		variableMap.put(name, variable, true);
		addConstantShadow(name);
	}
	
	public void addFunction(ASTNode<?> node, @NonNull Function function) {
		String name = function.name;
		if (valueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		if (function.scope == null) {
			function.scope = this;
		}
		functionMap.put(name, function, true);
		
		FunctionItemValue value = new FunctionItemValue(node, new FunctionItemTypeInfo(node, function), name, this);
		function.value = value;
		
		@NonNull Constant constant = new Constant(name, value);
		if (constant.scope == null) {
			constant.scope = this;
		}
		constantMap.put(name, constant, true);
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
			functionScope.addVariable(node, copy);
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
