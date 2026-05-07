package drlc.intermediate.scope;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.action.JumpAction;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.VariableDataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.FunctionItemValue;
import drlc.intermediate.module.*;
import drlc.intermediate.routine.Routine;

public class Scope {
	
	private static long globalCounter = 0;
	private long localCounter = 0;
	
	public final long globalId = globalCounter++;
	
	public final @NonNull String name;
	public final @Nullable Scope parent;
	public final boolean concrete;
	
	public final boolean isModule;
	
	protected final Map<String, Scope> childMap = new LinkedHashMap<>();
	protected final Map<String, Scope> moduleMap = new LinkedHashMap<>();
	
	public final List<NominalImport> nominalImports = new ArrayList<>();
	public final List<WildcardImport> wildcardImports = new ArrayList<>();
	
	protected final Hierarchy<String, TypeEntry> typeEntryHierarchy;
	
	protected final Hierarchy<String, ValueEntry> valueEntryHierarchy;
	
	protected final Hierarchy<String, Function> functionHierarchy;
	
	public boolean definiteLocalReturn = false, definiteExecution = true, potentialOuterMultipleExecution = false;
	
	protected final Set<Variable> initializationSet = new HashSet<>();
	
	public Scope(ASTNode<?> node, @Nullable String name, @Nullable Scope parent, boolean concrete) {
		this.name = name == null ? "\\" + globalId : name;
		this.parent = parent;
		this.concrete = concrete;
		isModule = name != null;
		
		if (parent == null) {
			typeEntryHierarchy = new Hierarchy<>(null);
			
			valueEntryHierarchy = new Hierarchy<>(null);
			
			functionHierarchy = new Hierarchy<>(null);
		}
		else {
			if (isModule) {
				parent.addModule(node, this.name, this);
			}
			parent.addChild(node, this.name, this);
			
			typeEntryHierarchy = new Hierarchy<>(parent.typeEntryHierarchy);
			
			valueEntryHierarchy = new Hierarchy<>(parent.valueEntryHierarchy);
			
			functionHierarchy = new Hierarchy<>(parent.functionHierarchy);
		}
	}
	
	private void addChild(ASTNode<?> node, @NonNull String name, @NonNull Scope scope) {
		if (childMap.containsKey(name)) {
			throw Helpers.nodeError(node, "Scope name \"%s\" already used in this scope!", name);
		}
		childMap.put(name, scope);
	}
	
	public void addModule(ASTNode<?> node, @NonNull String name, @NonNull Scope scope) {
		if (moduleMap.containsKey(name)) {
			throw Helpers.nodeError(node, "Module name \"%s\" already used in this scope!", name);
		}
		else if (name.equals(Global.ROOT)) {
			throw Helpers.nodeError(node, "Root import must be aliased!");
		}
		moduleMap.put(name, scope);
	}
	
	public boolean isSubScopeOf(Scope other) {
		Deque<Scope> stack = new ArrayDeque<>();
		Set<Scope> visited = new HashSet<>();
		stack.push(other);
		while (!stack.isEmpty()) {
			Scope current = stack.pop();
			if (!visited.add(current)) {
				continue;
			}
			if (equals(current)) {
				return true;
			}
			for (Scope child : current.childMap.values()) {
				stack.push(child);
			}
		}
		return false;
	}
	
	public void pathAction(ASTNode<?> node, @NonNull Path path, java.util.function.BiConsumer<Scope, String> consumer) {
		consumer.accept(getPathScope(node, path, null), path.name);
	}
	
	public <T> T pathGet(ASTNode<?> node, @NonNull Path path, java.util.function.BiFunction<Scope, String, T> function) {
		return function.apply(getPathScope(node, path, null), path.name);
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
		DeclaratorInfo declarator = new DeclaratorInfo(new Variable("\\r" + nextLocalId(), new VariableModifier(routine.isRootRoutine(), true), typeInfo));
		addVariable(null, declarator.variable.name, declarator.variable);
		routine.declaratorList.add(declarator);
		return declarator;
	}
	
	public @NonNull VariableDataId nextLocalDataId(Routine routine, @NonNull TypeInfo typeInfo) {
		return nextLocalDeclarator(routine, typeInfo).dataId();
	}
	
	// Contains
	
	public boolean declaredFunctionExists(String name, boolean shallow) {
		return tryGetDeclaredFunction(name, shallow) != null;
	}
	
	public boolean definesLocalFunction(@NonNull Function function) {
		return functionHierarchy.get(function.name, true) == function;
	}
	
	private boolean localTypeNameCollision(String name) {
		return typeEntryHierarchy.containsKey(name, true);
	}
	
	private boolean localValueNameCollision(String name) {
		return valueEntryHierarchy.containsKey(name, true) || functionHierarchy.containsKey(name, true);
	}
	
	// Getters
	
	public @NonNull Scope getConcreteScope() {
		return concrete ? this : (parent == null ? Main.rootScope : parent.getConcreteScope());
	}
	
	public @NonNull Scope getCurrentModule() {
		return isModule ? this : (parent == null ? Main.rootScope : parent.getCurrentModule());
	}
	
	public @NonNull Scope getSuperModule(ASTNode<?> node) {
		@NonNull Scope module = getCurrentModule();
		if (module.parent == null) {
			throw Helpers.nodeError(node, "Could not find \"%s\" in \"%s\"!", Global.SUPER, module.name);
		}
		return module.parent.getCurrentModule();
	}
	
	public @NonNull Scope getPathScope(ASTNode<?> node, @NonNull Path path, @Nullable NominalImport excludedImport) {
		Scope pathScope = findPathScope(node, path, excludedImport);
		if (pathScope == null) {
			throw Helpers.nodeError(node, "Could not resolve path \"%s\"!", path);
		}
		return pathScope;
	}
	
	public @Nullable Scope tryGetPathScope(ASTNode<?> node, @NonNull Path path, @Nullable NominalImport excludedImport) {
		return findPathScope(node, path, excludedImport);
	}
	
	private @Nullable Scope findPathScope(ASTNode<?> node, @NonNull Path path, @Nullable NominalImport excludedImport) {
		if (path.prefix.isEmpty()) {
			return this;
		}
		
		boolean seenStandardSegment = false;
		for (String segment : path.prefix) {
			boolean specialSegment = segment.equals(Global.ROOT) || segment.equals(Global.SELF) || segment.equals(Global.SUPER);
			if (specialSegment) {
				if (seenStandardSegment) {
					throw Helpers.nodeError(node, "Special path segment \"%s\" must appear before standard path segments in path \"%s\"!", segment, path);
				}
			}
			else {
				seenStandardSegment = true;
			}
		}
		
		String first = path.segments.get(0);
		String localFirst = first.equals(Global.SELF) && path.segments.size() > 1 ? path.segments.get(1) : first;
		Scope pathScope = getConcreteScope();
		if (pathScope.tryGetLocalModule(node, localFirst, excludedImport) == null) {
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
			else {
				Scope nextPathScope = pathScope.tryGetLocalModule(node, segment, excludedImport);
				if (nextPathScope == null) {
					return null;
				}
				pathScope = nextPathScope;
			}
		}
		return pathScope;
	}
	
	public @NonNull TypeDef getTypeDef(ASTNode<?> node, String name, boolean shallow) {
		TypeEntry typeEntry = tryGetTypeEntry(node, name, shallow);
		if (!(typeEntry instanceof NominalTypeEntry nominalTypeEntry)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return nominalTypeEntry.getTypeDef();
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node, String name, boolean shallow) {
		TypeEntry typeEntry = tryGetTypeEntry(node, name, shallow);
		if (typeEntry == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		return typeEntry.getTypeInfo(node);
	}
	
	public void collectTypeDefs(ASTNode<?> node, String name, Set<TypeDef> typeDefs) {
		TypeEntry typeEntry = tryGetTypeEntry(node, name, false);
		if (typeEntry == null) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
		}
		typeEntry.getTypeInfo(node).collectTypeDefs(typeDefs);
	}
	
	public @Nullable Constant tryGetConstant(ASTNode<?> node, String name, boolean shallow) {
		ValueEntry valueEntry = tryGetValueEntry(node, name, shallow);
		return valueEntry instanceof Constant constant ? constant : null;
	}
	
	public @NonNull Constant getConstant(ASTNode<?> node, String name, boolean shallow) {
		Constant constant = tryGetConstant(node, name, shallow);
		if (constant == null) {
			throw Helpers.nodeError(node, "Constant \"%s\" not defined in this scope!", name);
		}
		return constant;
	}
	
	public @Nullable Variable tryGetVariable(ASTNode<?> node, String name, boolean shallow) {
		ValueEntry valueEntry = tryGetValueEntry(node, name, shallow);
		return valueEntry instanceof Variable variable ? variable : null;
	}
	
	public @NonNull Variable getVariable(ASTNode<?> node, String name, boolean shallow) {
		Variable variable = tryGetVariable(node, name, shallow);
		if (variable == null) {
			throw Helpers.nodeError(node, "Variable \"%s\" not defined in this scope!", name);
		}
		return variable;
	}
	
	public @NonNull Function getDeclaredFunction(ASTNode<?> node, String name, boolean shallow) {
		Function function = tryGetDeclaredFunction(name, shallow);
		if (function == null) {
			throw Helpers.nodeError(node, "Function \"%s\" not defined in this scope!", name);
		}
		return function;
	}
	
	public @Nullable Scope tryGetLocalModule(ASTNode<?> node, String name, @Nullable NominalImport excludedImport) {
		Scope moduleResolution = moduleMap.get(name);
		for (NominalImport nominalImport : nominalImports) {
			if (nominalImport == excludedImport) {
				continue;
			}
			Scope module = nominalImport.tryResolveModuleAsLocalName(node, name);
			if (module != null) {
				moduleResolution = mergeModuleResolution(node, moduleResolution, module, name);
			}
		}
		for (WildcardImport wildcardImport : wildcardImports) {
			moduleResolution = mergeModuleResolution(node, moduleResolution, wildcardImport.tryGetModule(node, name), name);
		}
		return moduleResolution;
	}
	
	public @Nullable TypeEntry tryGetLocalTypeEntry(ASTNode<?> node, String name, @Nullable NominalImport excludedImport) {
		TypeEntry typeResolution = mergeTypeResolution(node, null, typeEntryHierarchy.get(name, true), name);
		for (NominalImport nominalImport : nominalImports) {
			if (nominalImport == excludedImport) {
				continue;
			}
			if (nominalImport.matchesImportedLocalName(node, name)) {
				typeResolution = mergeTypeResolution(node, typeResolution, nominalImport.resolveTypeEntry(node), name);
			}
		}
		for (WildcardImport wildcardImport : wildcardImports) {
			typeResolution = mergeTypeResolution(node, typeResolution, wildcardImport.tryGetTypeEntry(node, name), name);
		}
		return typeResolution;
	}
	
	protected @Nullable TypeEntry tryGetTypeEntry(ASTNode<?> node, String name, boolean shallow) {
		TypeEntry typeResolution = tryGetLocalTypeEntry(node, name, null);
		if (typeResolution != null) {
			return typeResolution;
		}
		if (shallow || parent == null) {
			return null;
		}
		return isModule ? Main.rootScope.preludeTypeEntryHierarchy.get(name, true) : parent.tryGetTypeEntry(node, name, false);
	}
	
	public @Nullable ValueEntry tryGetLocalValueEntry(ASTNode<?> node, String name, @Nullable NominalImport excludedImport) {
		ValueEntry valueResolution = mergeValueResolution(node, null, valueEntryHierarchy.get(name, true), name);
		for (NominalImport nominalImport : nominalImports) {
			if (nominalImport == excludedImport) {
				continue;
			}
			if (nominalImport.matchesImportedLocalName(node, name)) {
				valueResolution = mergeValueResolution(node, valueResolution, nominalImport.resolveValueEntry(node), name);
			}
		}
		for (WildcardImport wildcardImport : wildcardImports) {
			valueResolution = mergeValueResolution(node, valueResolution, wildcardImport.tryGetValueEntry(node, name), name);
		}
		return valueResolution;
	}
	
	protected @Nullable ValueEntry tryGetValueEntry(ASTNode<?> node, String name, boolean shallow) {
		ValueEntry valueResolution = tryGetLocalValueEntry(node, name, null);
		if (valueResolution != null) {
			return valueResolution;
		}
		if (shallow || parent == null) {
			return null;
		}
		return isModule ? Main.rootScope.preludeValueEntryHierarchy.get(name, true) : parent.tryGetValueEntry(node, name, false);
	}
	
	public void collectLocalModuleNames(ASTNode<?> node, Set<String> names, Set<WildcardImport> visited) {
		names.addAll(moduleMap.keySet());
		for (NominalImport nominalImport : nominalImports) {
			Scope module = nominalImport.tryResolveModule(node);
			if (module != null) {
				names.add(nominalImport.resolveModuleLocalName(module));
			}
		}
		for (WildcardImport wildcardImport : wildcardImports) {
			wildcardImport.collectModuleNames(node, names, visited);
		}
	}
	
	public void collectLocalTypeNames(ASTNode<?> node, Set<String> names, Set<WildcardImport> visited) {
		typeEntryHierarchy.forEachLocal((k, v) -> names.add(k));
		for (NominalImport nominalImport : nominalImports) {
			if (nominalImport.tryResolveTypeEntry(node) != null) {
				names.add(nominalImport.resolveLocalName(node));
			}
		}
		for (WildcardImport wildcardImport : wildcardImports) {
			wildcardImport.collectTypeNames(node, names, visited);
		}
	}
	
	public void collectLocalValueNames(ASTNode<?> node, Set<String> names, Set<WildcardImport> visited) {
		valueEntryHierarchy.forEachLocal((k, v) -> names.add(k));
		for (NominalImport nominalImport : nominalImports) {
			if (nominalImport.tryResolveValueEntry(node) != null) {
				names.add(nominalImport.resolveLocalName(node));
			}
		}
		for (WildcardImport wildcardImport : wildcardImports) {
			wildcardImport.collectValueNames(node, names, visited);
		}
	}
	
	public @Nullable Function tryGetDeclaredFunction(String name, boolean shallow) {
		Function function = functionHierarchy.get(name, true);
		if (function != null) {
			return function;
		}
		if (shallow || parent == null) {
			return null;
		}
		return isModule ? Main.rootScope.preludeFunctionHierarchy.get(name, true) : parent.tryGetDeclaredFunction(name, false);
	}
	
	private Scope mergeModuleResolution(ASTNode<?> node, Scope resolution, Scope candidate, String name) {
		if (candidate == null) {
			return resolution;
		}
		if (resolution != null) {
			if (resolution == candidate) {
				return resolution;
			}
			throw Helpers.nodeError(node, "Module name \"%s\" is ambiguous in this scope!", name);
		}
		return candidate;
	}
	
	private TypeEntry mergeTypeResolution(ASTNode<?> node, TypeEntry resolution, TypeEntry candidate, String name) {
		if (candidate == null) {
			return resolution;
		}
		if (resolution != null) {
			if (resolution == candidate) {
				return resolution;
			}
			throw Helpers.nodeError(node, "Type name \"%s\" is ambiguous in this scope!", name);
		}
		return candidate;
	}
	
	private ValueEntry mergeValueResolution(ASTNode<?> node, ValueEntry resolution, ValueEntry candidate, String name) {
		if (candidate == null) {
			return resolution;
		}
		if (resolution != null) {
			if (resolution == candidate) {
				return resolution;
			}
			throw Helpers.nodeError(node, "Name \"%s\" is ambiguous in this scope!", name);
		}
		return candidate;
	}
	
	public void addTypeDef(ASTNode<?> node, @NonNull String name, @NonNull TypeDef typeDef) {
		if (localTypeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		if (typeDef.scope == null) {
			typeDef.scope = this;
		}
		typeEntryHierarchy.put(name, typeDef, true);
	}
	
	public void addStructTypeDef(ASTNode<?> node, @NonNull String name, List<TypeInfo> typeInfos, List<String> memberNames) {
		int typeInfoCount = typeInfos.size(), memberNameCount = memberNames.size();
		if (typeInfoCount != memberNames.size()) {
			throw Helpers.nodeError(node, "Struct \"%s\" requires %d member names but received %d!", name, typeInfoCount, memberNameCount);
		}
		
		Map<String, MemberInfo> memberMap = new LinkedHashMap<>();
		final TypeDef[] typeDefHolder = new TypeDef[1];
		@NonNull TypeDef typeDef = typeDefHolder[0] = new TypeDef(name, 0, memberMap, (n, r) -> new StructTypeInfo(n, r, typeInfos, typeDefHolder[0]));
		Main.rootScope.addTypeDef(node, typeDef.name, typeDef);
		
		Set<TypeDef> typeDefs = new HashSet<>();
		for (TypeInfo typeInfo : typeInfos) {
			typeInfo.collectTypeDefs(typeDefs);
		}
		if (typeDefs.contains(typeDef)) {
			throw Helpers.nodeError(node, "Struct \"%s\" can not directly contain itself!", name);
		}
		
		try {
			int size = 0;
			for (TypeInfo typeInfo : typeInfos) {
				size = Math.addExact(size, typeInfo.getSize());
			}
			typeDef.size = size;
		}
		catch (ArithmeticException e) {
			throw Helpers.nodeError(node, "Size of struct \"%s\" is too large!", name);
		}
		
		int offset = 0;
		for (int i = 0; i < typeInfoCount; ++i) {
			@NonNull String memberName = memberNames.get(i);
			if (memberMap.containsKey(memberName)) {
				throw Helpers.nodeError(node, "Struct \"%s\" already has member \"%s\"!", name, memberName);
			}
			else {
				@NonNull TypeInfo typeInfo = typeInfos.get(i);
				memberMap.put(memberName, new MemberInfo(memberName, typeInfo, i, offset));
				try {
					offset = Math.addExact(offset, typeInfo.getSize());
				}
				catch (ArithmeticException e) {
					throw Helpers.nodeError(node, "Offset of member \"%s\" in struct \"%s\" is too large!", memberName, name);
				}
			}
		}
	}
	
	public void addTypeAliasEntry(ASTNode<?> node, @NonNull String name, @NonNull TypeAliasEntry typeAliasEntry) {
		if (localTypeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		typeEntryHierarchy.put(name, typeAliasEntry, true);
	}
	
	public void addDirectTypeName(ASTNode<?> node, @NonNull String name, @NonNull TypeInfo typeInfo) {
		if (localTypeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		typeEntryHierarchy.put(name, new DirectTypeEntry(typeInfo), true);
	}
	
	public void addConstant(ASTNode<?> node, @NonNull String name, @NonNull Constant constant) {
		if (localValueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		if (constant.scope == null) {
			constant.scope = this;
		}
		valueEntryHierarchy.put(name, constant, true);
	}
	
	public void addVariable(ASTNode<?> node, @NonNull String name, @NonNull Variable variable) {
		if (localValueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		if (variable.scope == null) {
			variable.scope = this;
		}
		valueEntryHierarchy.put(name, variable, true);
	}
	
	public void addFunction(ASTNode<?> node, @NonNull Function function) {
		String name = function.name;
		if (localValueNameCollision(name)) {
			throw Helpers.nodeError(node, "Name \"%s\" already used in this scope!", name);
		}
		
		if (function.definitionScope == null) {
			function.definitionScope = this;
		}
		functionHierarchy.put(name, function, true);
		
		FunctionItemValue value = new FunctionItemValue(node, new FunctionItemTypeInfo(node, function), name, this);
		function.value = value;
		
		@NonNull Constant constant = new Constant(name, value);
		if (constant.scope == null) {
			constant.scope = this;
		}
		valueEntryHierarchy.put(name, constant, true);
	}
	
	// Control flow
	
	public boolean hasDefiniteReturn() {
		return hasDefiniteReturnInternal(new HashSet<>());
	}
	
	protected boolean hasDefiniteReturnInternal(Set<Scope> path) {
		if (!path.add(this)) {
			return false;
		}
		try {
			return definiteLocalReturn || childMap.values().stream().anyMatch(x -> x.definiteExecution && x.hasDefiniteReturnInternal(path));
		}
		finally {
			path.remove(this);
		}
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
		return variable.scope.isVariablePotentiallyInitializedInternal(variable, this, new HashSet<>());
	}
	
	public boolean isVariableDefinitelyInitialized(Variable variable) {
		return variable.scope.isVariableDefinitelyInitializedInternal(variable, this, new HashSet<>());
	}
	
	protected boolean isVariablePotentiallyInitializedInternal(Variable variable, Scope location, Set<Scope> path) {
		if (!path.add(this)) {
			return false;
		}
		try {
			return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> x.isVariablePotentiallyInitializedInternal(variable, location, path));
		}
		finally {
			path.remove(this);
		}
	}
	
	protected boolean isVariableDefinitelyInitializedInternal(Variable variable, Scope location, Set<Scope> path) {
		if (!path.add(this)) {
			return false;
		}
		try {
			return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> (x.definiteExecution || location.isSubScopeOf(x)) && x.isVariableDefinitelyInitializedInternal(variable, location, path));
		}
		finally {
			path.remove(this);
		}
	}
	
	// Environment capture
	
	public Variable captureVariable(ASTNode<?> node, Variable variable) {
		FunctionScope functionScope = getContextFunctionScope();
		if (!variable.modifier._static && functionScope != null && !functionScope.equals(variable.scope.getContextFunctionScope())) {
			Function function = functionScope.function;
			if (!function.closure) {
				throw Helpers.nodeError(node, "Attempted to capture variable \"%s\" in non-closure function!", variable.name);
			}
			DeclaratorInfo capturedParam = function.getCapturedParam(variable);
			Variable capturedVariable = capturedParam == null ? null : capturedParam.variable;
			if (capturedVariable == null) {
				capturedVariable = variable.copy();
				functionScope.addVariable(node, capturedVariable.name, capturedVariable);
				function.addCapture(variable, new DeclaratorInfo(capturedVariable));
			}
			if (isVariableDefinitelyInitialized(variable)) {
				functionScope.initializationSet.add(capturedVariable);
			}
			return capturedVariable;
		}
		else {
			return variable;
		}
	}
}
