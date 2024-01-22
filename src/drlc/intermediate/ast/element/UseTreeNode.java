package drlc.intermediate.ast.element;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class UseTreeNode extends ASTNode<Scope> {
	
	public final @NonNull List<String> path = new ArrayList<>();
	
	public UseTreeNode(Source source) {
		super(source);
	}
	
	public abstract void buildPath(@NonNull List<String> pathPrefix);
}
