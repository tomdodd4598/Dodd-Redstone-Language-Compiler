package drlc.intermediate.ast.section;

import drlc.Source;
import drlc.intermediate.scope.Scope;

public abstract class StaticSectionNode<SCOPE extends Scope> extends RuntimeSectionNode<SCOPE> {
	
	protected StaticSectionNode(Source source) {
		super(source);
	}
}
