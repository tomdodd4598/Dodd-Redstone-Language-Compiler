package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.Nullable;

import drlc.intermediate.scope.IterativeScope;
import drlc.node.Node;

public abstract class IterativeSectionNode extends RuntimeSectionNode<IterativeScope> {
	
	public final @Nullable String label;
	
	protected IterativeSectionNode(Node[] parseNodes, @Nullable String label) {
		super(parseNodes);
		this.label = label;
	}
}
