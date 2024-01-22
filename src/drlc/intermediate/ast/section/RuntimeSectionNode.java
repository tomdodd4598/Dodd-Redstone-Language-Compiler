package drlc.intermediate.ast.section;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class RuntimeSectionNode<SCOPE extends Scope> extends ASTNode<SCOPE> {
	
	protected RuntimeSectionNode(Source source) {
		super(source);
	}
}
