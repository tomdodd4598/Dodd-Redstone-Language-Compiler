
package drlc.intermediate.ast.element;

import java.util.*;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.scope.Scope;

public class WildcardUseTreeNode extends UseTreeNode {
	
	public final @NonNull List<String> pathPrefix;
	
	protected Set<Scope> moduleSet = new HashSet<>();
	protected Set<TypeDef> typeDefSet = new HashSet<>();
	protected Set<TypeInfo> typealiasSet = new HashSet<>();
	protected Set<Constant> constantSet = new HashSet<>();
	protected Set<Variable> variableSet = new HashSet<>();
	
	public WildcardUseTreeNode(Source source, @NonNull List<String> pathPrefix) {
		super(source);
		this.pathPrefix = pathPrefix;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
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
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		
	}
	
	@Override
	public void buildPath(@NonNull List<String> pathPrefix) {
		path.addAll(pathPrefix);
		path.addAll(this.pathPrefix);
		
		if (path.isEmpty()) {
			throw error("Wildcard import must be prefixed by path!");
		}
		path.add(Global.WILDCARD_PATH);
	}
	
	@SuppressWarnings("null")
	protected void tryImport() {
		scope.pathAction(this, path, (x, name) -> {
			x.childMap.forEach((k, v) -> importAction(v, moduleSet, y -> scope.addChild(this, k, y)));
			x.typeDefMap.forEach((k, v) -> importAction(v, typeDefSet, y -> scope.addTypeDef(this, k, y)), true);
			x.typealiasMap.forEach((k, v) -> importAction(v, typealiasSet, y -> scope.addTypealias(this, k, y)), true);
			x.constantMap.forEach((k, v) -> importAction(v, constantSet, y -> scope.addConstant(this, k, y)), true);
			x.variableMap.forEach((k, v) -> importAction(v, variableSet, y -> scope.addVariable(this, k, y)), true);
		});
	}
	
	protected <T> void importAction(T object, Set<T> set, Consumer<T> adder) {
		if (!set.contains(object)) {
			adder.accept(object);
			set.add(object);
		}
	}
}
