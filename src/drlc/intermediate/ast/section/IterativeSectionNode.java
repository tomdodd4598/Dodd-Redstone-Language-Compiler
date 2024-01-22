package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.Nullable;

import drlc.Source;
import drlc.intermediate.scope.IterativeScope;

public abstract class IterativeSectionNode extends RuntimeSectionNode<IterativeScope> {
	
	public final @Nullable String label;
	
	protected IterativeSectionNode(Source source, @Nullable String label) {
		super(source);
		this.label = label;
	}
}
