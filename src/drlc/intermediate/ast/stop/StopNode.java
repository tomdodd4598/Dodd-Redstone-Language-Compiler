package drlc.intermediate.ast.stop;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class StopNode extends ASTNode<Scope> {
	
	protected StopNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
