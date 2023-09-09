package drlc.intermediate.ast.section;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public abstract class ProgramSectionNode extends ASTNode {
	
	protected ProgramSectionNode(Node[] parseNodes) {
		super(parseNodes);
	}
}
