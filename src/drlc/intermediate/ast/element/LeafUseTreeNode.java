package drlc.intermediate.ast.element;

import java.util.*;
import java.util.function.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.scope.Scope;

public class LeafUseTreeNode extends UseTreeNode {
	
	public final @NonNull List<String> pathSuffix;
	public final @Nullable String alias;
	
	@SuppressWarnings("null")
	public @NonNull Path path = null;
	
	protected Set<Scope> moduleSet = new HashSet<>();
	
	protected Set<TypeDef> typeDefSet = new HashSet<>();
	protected Set<TypeInfo> typeAliasSet = new HashSet<>();
	
	protected Set<Constant> constantSet = new HashSet<>();
	protected Set<Variable> variableSet = new HashSet<>();
	
	public LeafUseTreeNode(Source source, @NonNull List<String> pathSuffix, @Nullable String alias) {
		super(source);
		this.pathSuffix = pathSuffix;
		this.alias = alias;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope.getConcreteScope();
		if (!scope.childExists(path.segments.get(0))) {
			scope = scope.getCurrentModule();
		}
		
		tryImport();
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		tryImport();
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		tryImport();
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		tryImport();
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		tryImport();
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		if (moduleSet.isEmpty() && typeDefSet.isEmpty() && typeAliasSet.isEmpty() && constantSet.isEmpty() && variableSet.isEmpty()) {
			throw error("Failed to import \"%s\"!", path);
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		
	}
	
	@Override
	public void buildPath(@NonNull List<String> pathPrefix) {
		pathSegments.addAll(pathPrefix);
		pathSegments.addAll(pathSuffix);
		
		path = new Path(pathSegments);
	}
	
	@SuppressWarnings("null")
	protected void tryImport() {
		scope.pathAction(this, path, (x, name) -> {
			if (name.equals(Global.ROOT)) {
				importAction(name, moduleSet, () -> true, () -> Main.rootScope, (y, z) -> scope.addChild(this, y, z));
			}
			else if (name.equals(Global.SUPER)) {
				Scope module = x.getSuperModule(this);
				importAction(module.name, moduleSet, () -> true, () -> module, (y, z) -> scope.addChild(this, y, z));
			}
			else if (name.equals(Global.SELF)) {
				Scope module = x.getCurrentModule();
				importAction(module.name, moduleSet, () -> true, () -> module, (y, z) -> scope.addChild(this, y, z));
			}
			else {
				importAction(name, moduleSet, () -> x.childExists(name), () -> x.getChild(this, name), (y, z) -> scope.addChild(this, y, z));
				
				importAction(name, typeDefSet, () -> x.typeDefExists(name, true), () -> x.getTypeDef(this, name, true), (y, z) -> scope.addTypeDef(this, y, z));
				importAction(name, typeAliasSet, () -> x.typeAliasExists(name, true), () -> x.getTypeAlias(this, name, true), (y, z) -> scope.addTypeAlias(this, y, z));
				
				importAction(name, constantSet, () -> x.constantExists(name, true), () -> x.getConstant(this, name, true), (y, z) -> scope.addConstant(this, y, z));
				importAction(name, variableSet, () -> x.variableExists(name, true), () -> x.getVariable(this, name, true), (y, z) -> scope.addVariable(this, y, z));
			}
		});
	}
	
	@SuppressWarnings("null")
	protected <T> void importAction(String name, Set<T> set, BooleanSupplier exists, Supplier<T> getter, BiConsumer<String, T> adder) {
		if (exists.getAsBoolean()) {
			@NonNull T object = getter.get();
			if (!set.contains(object)) {
				adder.accept(alias == null ? name : alias, object);
				set.add(object);
			}
		}
	}
}
