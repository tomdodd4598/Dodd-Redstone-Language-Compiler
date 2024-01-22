package drlc.intermediate.ast.stop;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class StopNode extends ASTNode<Scope> {
	
	protected StopNode(Source source) {
		super(source);
	}
}
