package drlc.intermediate.scope;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.action.JumpAction;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.FunctionItemValue;
import drlc.intermediate.routine.Routine;

public class Scope {
	
	private static long idCounter = 0;
	
	private long localCounter = 0;
	
	public final long globalId = idCounter++;
	
	public final Scope parent;
	
	protected final List<Scope> children = new ArrayList<>();
	
	protected final Hierarchy<String, Scope> constantShadowMap;
	
	protected final Hierarchy<String, RawType> rawTypeMap;
	protected final Hierarchy<String, TypeInfo> aliasTypeMap;
	protected final Hierarchy<String, Constant> constantMap;
	protected final Hierarchy<String, Variable> variableMap;
	protected final Hierarchy<String, Function> functionMap;
	protected final Hierarchy<String, Routine> routineMap;
	
	public boolean definiteLocalReturn = false;
	public boolean definiteExecution = true, potentialOuterMultipleExecution = false;
	
	protected final Set<Variable> initializationSet = new HashSet<>();
	
	public Scope(Scope parent) {
		this.parent = parent;
		
		if (parent == null) {
			constantShadowMap = new Hierarchy<>(null);
			
			rawTypeMap = new Hierarchy<>(null);
			aliasTypeMap = new Hierarchy<>(null);
			constantMap = new Hierarchy<>(null);
			variableMap = new Hierarchy<>(null);
			functionMap = new Hierarchy<>(null);
			routineMap = new Hierarchy<>(null);
		}
		else {
			parent.children.add(this);
			
			constantShadowMap = new Hierarchy<>(parent.constantShadowMap);
			
			rawTypeMap = new Hierarchy<>(parent.rawTypeMap);
			aliasTypeMap = new Hierarchy<>(parent.aliasTypeMap);
			constantMap = new Hierarchy<>(parent.constantMap);
			variableMap = new Hierarchy<>(parent.variableMap);
			functionMap = new Hierarchy<>(parent.functionMap);
			routineMap = new Hierarchy<>(parent.routineMap);
		}
	}
	
	public boolean isSubScopeOf(Scope other) {
		return equals(other) || other.children.stream().anyMatch(x -> isSubScopeOf(x));
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
	
	// Iterables
	
	protected <T, K, V> Iterable<T> iterable(java.util.function.Function<? super Scope, ? extends Hierarchy<K, V>> supplier, java.util.function.Function<? super Map<K, V>, ? extends Iterable<T>> mapFunction, BiFunction<? super Hierarchy<K, V>, ? super Boolean, ? extends Iterable<T>> hierarchyFunction, boolean children) {
		return () -> new Iterator<T>() {
			
			Iterator<T> current = supplier.apply(Scope.this).iterable(mapFunction, hierarchyFunction, true).iterator();
			Iterator<Scope> childIter = children ? Scope.this.children.iterator() : null;
			
			@Override
			public boolean hasNext() {
				while (!current.hasNext()) {
					if (childIter == null || !childIter.hasNext()) {
						return false;
					}
					current = childIter.next().iterable(supplier, mapFunction, hierarchyFunction, true).iterator();
				}
				return current.hasNext();
			}
			
			@Override
			public T next() {
				return current.next();
			}
		};
	}
	
	@SuppressWarnings("null")
	protected <K, V> Iterable<Entry<K, V>> entryIterable(java.util.function.Function<? super Scope, ? extends Hierarchy<K, V>> supplier, boolean children) {
		return iterable(supplier, Map::entrySet, Hierarchy::entryIterable, children);
	}
	
	public Iterable<Entry<String, Routine>> routineEntryIterable(boolean children) {
		return entryIterable(x -> x.routineMap, children);
	}
	
	@SuppressWarnings("null")
	protected <K, V> Iterable<V> valueIterable(java.util.function.Function<? super Scope, ? extends Hierarchy<K, V>> supplier, boolean children) {
		return iterable(supplier, Map::values, Hierarchy::valueIterable, children);
	}
	
	public Iterable<Routine> routineIterable(boolean children) {
		return valueIterable(x -> x.routineMap, children);
	}
	
	// Foreach
	
	protected <K, V> void forEachEntry(java.util.function.Function<? super Scope, ? extends Hierarchy<K, V>> supplier, BiConsumer<? super K, ? super V> consumer, boolean children) {
		supplier.apply(this).forEachEntry(consumer, true);
		if (children) {
			for (Scope child : this.children) {
				child.forEachEntry(supplier, consumer, true);
			}
		}
	}
	
	public void forEachRoutineEntry(BiConsumer<? super String, ? super Routine> consumer, boolean children) {
		forEachEntry(x -> x.routineMap, consumer, children);
	}
	
	protected <K, V> void forEachValue(java.util.function.Function<? super Scope, ? extends Hierarchy<K, V>> supplier, Consumer<? super V> consumer, boolean children) {
		supplier.apply(this).forEachValue(consumer, true);
		if (children) {
			for (Scope child : this.children) {
				child.forEachValue(supplier, consumer, true);
			}
		}
	}
	
	public void forEachRoutine(Consumer<? super Routine> consumer, boolean children) {
		forEachValue(x -> x.routineMap, consumer, children);
	}
	
	// Contains
	
	public boolean rawTypeExists(String name, boolean shallow) {
		return rawTypeMap.containsKey(name, shallow);
	}
	
	public boolean aliasTypeExists(String name, boolean shallow) {
		return aliasTypeMap.containsKey(name, shallow);
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
	
	public boolean routineExists(String name, boolean shallow) {
		return routineMap.containsKey(name, shallow);
	}
	
	public boolean typeNameCollision(String name) {
		return rawTypeExists(name, true) || aliasTypeExists(name, true);
	}
	
	public boolean valueNameCollision(String name) {
		return constantExists(name, true) || variableExists(name, true) || functionExists(name, true);
	}
	
	public boolean routineNameCollision(String name) {
		return routineExists(name, true);
	}
	
	// Removers
	
	public void removeRoutine(ASTNode<?, ?> node, String name, Routine value) {
		boolean remove = routineMap.remove(name, value, false);
		if (!remove) {
			throw Helpers.nodeError(node, "Routine \"%s\" not defined in this scope!", value);
		}
	}
	
	// Getters
	
	public @NonNull RawType getRawType(ASTNode<?, ?> node, String name) {
		RawType rawType = rawTypeMap.get(name, false);
		if (rawType == null) {
			throw Helpers.nodeError(node, "Raw type \"%s\" not defined in this scope!", name);
		}
		return rawType;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?, ?> node, String name) {
		RawType rawType = rawTypeMap.get(name, false);
		if (rawType == null) {
			TypeInfo aliasType = aliasTypeMap.get(name, false);
			if (aliasType == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			return aliasType;
		}
		return rawType.getTypeInfo(node, new ArrayList<>(), this);
	}
	
	public void collectRawTypes(ASTNode<?, ?> node, Set<RawType> rawTypes, String name) {
		RawType rawType = rawTypeMap.get(name, false);
		if (rawType == null) {
			TypeInfo aliasType = aliasTypeMap.get(name, false);
			if (aliasType == null) {
				throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", name);
			}
			else {
				aliasType.collectRawTypes(rawTypes);
			}
		}
		else {
			rawTypes.add(rawType);
		}
	}
	
	@SuppressWarnings("null")
	public @NonNull Constant getConstant(ASTNode<?, ?> node, String name) {
		if (!constantExists(name, false)) {
			throw Helpers.nodeError(node, "Constant \"%s\" not defined in this scope!", name);
		}
		return constantMap.get(name, false);
	}
	
	public @NonNull Variable getVariable(ASTNode<?, ?> node, String name) {
		Variable variable = variableMap.get(name, false);
		if (variable == null) {
			throw Helpers.nodeError(node, "Variable \"%s\" not defined in this scope!", name);
		}
		return variable;
	}
	
	public @NonNull Function getFunction(ASTNode<?, ?> node, String name) {
		Function function = functionMap.get(name, false);
		if (function == null) {
			throw Helpers.nodeError(node, "Function \"%s\" not defined in this scope!", name);
		}
		return function;
	}
	
	public @NonNull Routine getRoutine(ASTNode<?, ?> node, String name) {
		Routine routine = routineMap.get(name, false);
		if (routine == null) {
			throw Helpers.nodeError(node, "Routine \"%s\" not defined in this scope!", name);
		}
		return routine;
	}
	
	// Adders
	
	public void addConstantShadow(String name) {
		constantShadowMap.put(name, this, true);
	}
	
	public void addRawType(ASTNode<?, ?> node, @NonNull RawType rawType) {
		String name = rawType.name;
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		rawType.scope = this;
		rawTypeMap.put(name, rawType, true);
	}
	
	public void addAliasType(ASTNode<?, ?> node, @NonNull String name, @NonNull TypeInfo aliasType) {
		if (typeNameCollision(name)) {
			throw Helpers.nodeError(node, "Type name \"%s\" already used in this scope!", name);
		}
		
		if (!aliasType.exists(this)) {
			throw Helpers.nodeError(node, "Type \"%s\" not defined in this scope!", aliasType.copy(node));
		}
		
		aliasTypeMap.put(name, aliasType, true);
	}
	
	public void addConstant(ASTNode<?, ?> node, @NonNull Constant constant, boolean replace) {
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
	
	public void addVariable(ASTNode<?, ?> node, @NonNull Variable variable, boolean replace) {
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
	
	public void addFunction(ASTNode<?, ?> node, @NonNull Function function, boolean replace) {
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
		addConstant(node, new Constant(name, new FunctionItemValue(node, new FunctionItemTypeInfo(node, this, name), name, this)), true);
	}
	
	public void addRoutine(ASTNode<?, ?> node, @NonNull Routine routine) {
		String name = routine.name;
		if (routineNameCollision(name)) {
			throw Helpers.nodeError(node, "Routine name \"%s\" already used in this scope!", name);
		}
		
		routine.scope = this;
		routineMap.put(name, routine, true);
	}
	
	// Control flow
	
	public boolean hasDefiniteReturn() {
		return definiteLocalReturn || children.stream().anyMatch(x -> x.definiteExecution && x.hasDefiniteReturn());
	}
	
	public @Nullable FunctionScope getFunctionScope() {
		return parent == null ? null : parent.getFunctionScope();
	}
	
	public @Nullable Function getContextFunction() {
		return parent == null ? null : parent.getContextFunction();
	}
	
	protected @Nullable Scope potentialMultipleExecutionScope() {
		return potentialOuterMultipleExecution ? this : (parent == null ? null : parent.potentialMultipleExecutionScope());
	}
	
	public boolean isBreakable(@Nullable String label) {
		return parent != null && parent.isBreakable(label);
	}
	
	public @NonNull JumpAction getContinueJump(ASTNode<?, ?> node, @Nullable String label) {
		return parent.getContinueJump(node, label);
	}
	
	public @NonNull JumpAction getBreakJump(ASTNode<?, ?> node, @Nullable String label) {
		return parent.getBreakJump(node, label);
	}
	
	// Variable initialization
	
	public void onVariableInitialization(ASTNode<?, ?> node, Variable variable) {
		if (!variable.modifier.mutable && (!Objects.equals(potentialMultipleExecutionScope(), variable.scope.potentialMultipleExecutionScope()) || isVariablePotentiallyInitialized(variable))) {
			throw Helpers.nodeError(node, "Attempted to potentially assign twice to immutable variable \"%s\"!", variable.name);
		}
		initializationSet.add(variable);
	}
	
	public boolean isVariablePotentiallyInitialized(Variable variable) {
		return variable.scope.isVariablePotentiallyInitializedInternal(variable, this);
	}
	
	protected boolean isVariablePotentiallyInitializedInternal(Variable variable, Scope location) {
		return initializationSet.contains(variable) || children.stream().anyMatch(x -> x.isVariablePotentiallyInitializedInternal(variable, location));
	}
	
	public boolean isVariableDefinitelyInitialized(Variable variable) {
		return variable.scope.isVariableDefinitelyInitializedInternal(variable, this);
	}
	
	protected boolean isVariableDefinitelyInitializedInternal(Variable variable, Scope location) {
		return initializationSet.contains(variable) || children.stream().anyMatch(x -> (x.definiteExecution || location.isSubScopeOf(x)) && x.isVariableDefinitelyInitializedInternal(variable, location));
	}
	
	// Environment capture
	
	public void onVariableExpression(ASTNode<?, ?> node, Variable variable) {
		if (!variable.modifier._static && !Objects.equals(getFunctionScope(), variable.scope.getFunctionScope())) {
			onVariableCapture(node, variable);
		}
	}
	
	public void onVariableCapture(ASTNode<?, ?> node, Variable variable) {
		throw Helpers.nodeError(node, "Attempted to capture environment in non-closure function!");
	}
}
