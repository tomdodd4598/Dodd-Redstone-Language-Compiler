package drlc.intermediate.ast.section;

import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class BasicSectionNode<SCOPE extends Scope, ROUTINE extends Routine> extends ProgramSectionNode<SCOPE, ROUTINE> {
	
	protected BasicSectionNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
