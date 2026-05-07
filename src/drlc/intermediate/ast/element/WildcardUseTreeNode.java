package drlc.intermediate.ast.element;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Path;
import drlc.intermediate.module.WildcardImport;

public class WildcardUseTreeNode extends UseTreeNode {
	
	public final @NonNull List<String> pathPrefix;
	
	@SuppressWarnings("null")
	public @NonNull Path path = null;
	
	@SuppressWarnings("null")
	protected @NonNull WildcardImport wildcardImport = null;
	
	public WildcardUseTreeNode(Source source, @NonNull List<String> pathPrefix) {
		super(source);
		this.pathPrefix = pathPrefix;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope.getConcreteScope();
	}
	
	@Override
	public void declareImports(ASTNode<?> parent) {
		wildcardImport = new WildcardImport(scope, path);
		scope.wildcardImports.add(wildcardImport);
	}
	
	@Override
	public void buildPath(@NonNull List<String> pathPrefix) {
		pathSegments.clear();
		pathSegments.addAll(pathPrefix);
		pathSegments.addAll(this.pathPrefix);
		
		if (pathSegments.isEmpty()) {
			throw error("Wildcard import must be prefixed by path!");
		}
		pathSegments.add(Global.WILDCARD_PATH);
		
		path = new Path(pathSegments);
	}
	
	@Override
	public void checkImports(ASTNode<?> parent) {
		wildcardImport.check(this);
	}
}
