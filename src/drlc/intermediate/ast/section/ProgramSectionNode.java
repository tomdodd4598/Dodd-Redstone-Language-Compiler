package drlc.intermediate.ast.section;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class ProgramSectionNode<SCOPE extends Scope, ROUTINE extends Routine> extends ASTNode<SCOPE, ROUTINE> {
	
	protected ProgramSectionNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
