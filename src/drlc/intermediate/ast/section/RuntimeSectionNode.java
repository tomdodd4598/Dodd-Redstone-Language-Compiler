package drlc.intermediate.ast.section;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class RuntimeSectionNode<SCOPE extends Scope> extends ASTNode<SCOPE> {
	
	protected RuntimeSectionNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
