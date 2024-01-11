package drlc.intermediate.ast.section;

import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class StaticSectionNode<SCOPE extends Scope> extends RuntimeSectionNode<SCOPE> {
	
	protected StaticSectionNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
