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
	
	protected final Hierarchy<String, Scope> constantShadowHierarchy;
	
	public final Hierarchy<String, TypeDef> typeDefHierarchy;
	public final Hierarchy<String, TypeInfo> typeAliasHierarchy;
	
	public final Hierarchy<String, Constant> constantHierarchy;
	public final Hierarchy<String, Variable> variableHierarchy;
	public final Hierarchy<String, Function> functionHierarchy;
	
	public boolean definiteLocalReturn = false;
	public boolean definiteExecution = true, potentialOuterMultipleExecution = false;
	
	protected final Set<Variable> initializationSet = new HashSet<>();
	
	public Scope(ASTNode<?> node, @Nullable String name, @Nullable Scope parent, boolean pseudo) {
		this.name = name == null ? "\\" + globalId : name;
		this.parent = parent;
		this.pseudo = pseudo;
		isModule = name != null;
		
		if (parent == null) {
			constantShadowHierarchy = new Hierarchy<>(null);
			
			typeDefHierarchy = new Hierarchy<>(null);
			typeAliasHierarchy = new Hierarchy<>(null);
			
			constantHierarchy = new Hierarchy<>(null);
			variableHierarchy = new Hierarchy<>(null);
			functionHierarchy = new Hierarchy<>(null);
		}
		else {
			parent.addChild(node, this.name, this);
			
			constantShadowHierarchy = new Hierarchy<>(parent.constantShadowHierarchy);
			
			typeDefHierarchy = new Hierarchy<>(parent.typeDefHierarchy);
			typeAliasHierarchy = new Hierarchy<>(parent.typeAliasHierarchy);
			
			constantHierarchy = new Hierarchy<>(parent.constantHierarchy);
			variableHierarchy = new Hierarchy<>(parent.variableHierarchy);
			functionHierarchy = new Hierarchy<>(parent.functionHierarchy);
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
	
	public void pathAction(ASTNode<?> node, @NonNull Path path, java.util.function.BiConsumer<Scope, String> consumer) {
		consumer.accept(getPathScope(node, path), path.name);
	}
	
	public <T> T pathGet(ASTNode<?> node, @NonNull Path path, java.util.function.BiFunction<Scope, String, T> function) {
		return function.apply(getPathScope(node, path), path.name);
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
	
	public @NonNull DeclaratorInfo nextLocalDeclarator(Routine routine, @NonNull TypeInfo typeInfo) {
		return Helpers.builtInDeclarator("\\r" + nextLocalId(), typeInfo);
	}
	
	public @NonNull DataId nextLocalDataId(Routine routine, @NonNull TypeInfo typeInfo) {
		DeclaratorInfo declarator = nextLocalDeclarator(routine, typeInfo);
		addVariable(null, declarator.variable);
		routine.declaratorList.add(declarator);
		return declarator.dataId();
	}
	
	// Contains
	
	public boolean typeDefExists(String name, boolean shallow) {
		return typeDefHierarchy.containsKey(name, shallow);
	}
	
	public boolean typeAliasExists(String name, boolean shallow) {
		return typeAliasHierarchy.containsKey(name, shallow);
	}
	
	public boolean constantExists(String name, boolean shallow) {
		Constant constant = constantHierarchy.get(name, shallow);
		Scope shadowScope;
		return constant != null && ((shadowScope = constantShadowHierarchy.get(name, shallow)) == null || !shadowScope.isSubScopeOf(constant.scope));
	}
	
	public boolean variableExists(String name, boolean shallow) {
		return variableHierarchy.containsKey(name, shallow);
	}
	
	public boolean functionExists(String name, boolean shallow) {
		return functionHierarchy.containsKey(name, shallow);
	}
	
	public boolean typeNameCollision(String name) {
		return typeDefExists(name, true) || typeAliasExists(name, true);
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
	public @NonNull Scope getPathScope(ASTNode<?> node, @NonNull Path path) {
		if (path.prefix.isEmpty()) {
			return this;
		}
		
		String first = path.segments.get(0);
		@NonNull Scope pathScope = getConcreteScope();
		if (!pathScope.childExists(first.equals(Global.SELF) ? path.segments.get(1) : first)) {
			pathScope = getCurrentModule();
		}
		
		for (String segment : path.prefix) {
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
		TypeDef typeDef = typeDefHierarchy.get(name, shallow);
		if (typeDef == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return typeDef;
	}
	
	public @NonNull TypeInfo getTypeAlias(ASTNode<?> node, String name, boolean shallow) {
		TypeInfo typeInfo = typeAliasHierarchy.get(name, shallow);
		if (typeInfo == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return typeInfo;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node, String name, boolean shallow) {
		TypeDef typeDef = typeDefHierarchy.get(name, shallow);
		if (typeDef == null) {
			TypeInfo typeInfo = typeAliasHierarchy.get(name, shallow);
			if (typeInfo == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			return typeInfo;
		}
		return typeDef.getTypeInfo(node, new ArrayList<>(), this);
	}
	
	public void collectTypeDefs(ASTNode<?> node, String name, Set<TypeDef> typeDefs) {
		TypeDef typeDef = typeDefHierarchy.get(name, false);
		if (typeDef == null) {
			TypeInfo typeInfo = typeAliasHierarchy.get(name, false);
			if (typeInfo == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			else {
				typeInfo.collectTypeDefs(typeDefs);
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
		return constantHierarchy.get(name, shallow);
	}
	
	public @NonNull Variable getVariable(ASTNode<?> node, String name, boolean shallow) {
		Variable variable = variableHierarchy.get(name, shallow);
		if (variable == null) {
			throw Helpers.nodeError(node, "Variable \"%s\" not defined in this scope!", name);
		}
		return variable;
	}
	
	public @NonNull Function getFunction(ASTNode<?> node, String name, boolean shallow) {
		Function function = functionHierarchy.get(name, shallow);
		if (function == null) {
			throw Helpers.nodeError(node, "Function \"%s\" not defined in this scope!", name);
		}
		return function;
	}
	
	// Adders
	
	public void addConstantShadow(String name) {
		constantShadowHierarchy.put(name, this, true);
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
		typeDefHierarchy.put(name, typeDef, true);
	}
	
	@SuppressWarnings("null")
	public void addStructTypeDef(ASTNode<?> node, @NonNull String name, List<TypeInfo> typeInfos, List<String> memberNames) {
		int typeInfoCount = typeInfos.size(), memberNameCount = memberNames.size();
		if (typeInfoCount != memberNames.size()) {
			throw Helpers.nodeError(node, "Struct \"%s\" requires %d member names but received %d!", typeInfoCount, memberNameCount);
		}
		
		Map<String, MemberInfo> memberMap = new LinkedHashMap<>();
		@NonNull TypeDef typeDef = new TypeDef(name, 0, memberMap, (n, r, s) -> new StructTypeInfo(n, r, typeInfos, s, name));
		Main.rootScope.addTypeDef(node, typeDef);
		
		Set<TypeDef> typeDefs = new HashSet<>();
		for (TypeInfo typeInfo : typeInfos) {
			typeInfo.collectTypeDefs(typeDefs);
		}
		if (typeDefs.contains(typeDef)) {
			throw Helpers.nodeError(node, "Struct \"%s\" can not directly contain itself!", name);
		}
		
		typeDef.size = Helpers.sumToInt(typeInfos, TypeInfo::getSize);
		
		int offset = 0;
		for (int i = 0; i < typeInfoCount; ++i) {
			@NonNull String memberName = memberNames.get(i);
			if (memberMap.containsKey(memberName)) {
				throw Helpers.nodeError(node, "Struct \"%s\" already has member \"%s\"!", name, memberName);
			}
			else {
				@NonNull TypeInfo typeInfo = typeInfos.get(i);
				memberMap.put(memberName, new MemberInfo(memberName, typeInfo, i, offset));
				offset += typeInfo.getSize();
			}
		}
	}
	
	public void addTypeAlias(ASTNode<?> node, @NonNull String name, @NonNull TypeInfo typeInfo) {
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		typeAliasHierarchy.put(name, typeInfo, true);
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
		constantHierarchy.put(name, constant, true);
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
		variableHierarchy.put(name, variable, true);
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
		functionHierarchy.put(name, function, true);
		
		FunctionItemValue value = new FunctionItemValue(node, new FunctionItemTypeInfo(node, function), name, this);
		function.value = value;
		
		@NonNull Constant constant = new Constant(name, value);
		if (constant.scope == null) {
			constant.scope = this;
		}
		constantHierarchy.put(name, constant, true);
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
