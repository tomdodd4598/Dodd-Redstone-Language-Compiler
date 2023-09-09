package drlc.intermediate.ast.stop;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public abstract class StopNode extends ASTNode {
	
	protected StopNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
