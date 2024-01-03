package drlc.intermediate.ast.section;

import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class StaticSectionNode<SCOPE extends Scope, ROUTINE extends Routine> extends RuntimeSectionNode<SCOPE, ROUTINE> {
	
	protected StaticSectionNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
