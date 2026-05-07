package drlc.intermediate.ast.element;

import java.util.List;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Path;
import drlc.intermediate.module.NominalImport;

public class LeafUseTreeNode extends UseTreeNode {
	
	public final @NonNull List<String> pathSuffix;
	public final @Nullable String alias;
	
	@SuppressWarnings("null")
	public @NonNull Path path = null;
	
	@SuppressWarnings("null")
	protected @NonNull NominalImport nominalImport = null;
	
	public LeafUseTreeNode(Source source, @NonNull List<String> pathSuffix, @Nullable String alias) {
		super(source);
		this.pathSuffix = pathSuffix;
		this.alias = alias;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope.getConcreteScope();
	}
	
	@Override
	public void declareImports(ASTNode<?> parent) {
		nominalImport = new NominalImport(scope, path, alias);
		scope.nominalImports.add(nominalImport);
	}
	
	@Override
	public void buildPath(@NonNull List<String> pathPrefix) {
		pathSegments.clear();
		pathSegments.addAll(pathPrefix);
		pathSegments.addAll(pathSuffix);
		
		path = new Path(pathSegments);
	}
	
	@Override
	public void checkImports(ASTNode<?> parent) {
		nominalImport.check(this);
	}
}
