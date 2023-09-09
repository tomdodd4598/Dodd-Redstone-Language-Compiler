package drlc.intermediate.ast.conditional;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public abstract class ConditionalEndNode extends ASTNode {
	
	protected ConditionalEndNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
