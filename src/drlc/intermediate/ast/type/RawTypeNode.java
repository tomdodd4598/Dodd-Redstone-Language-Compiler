package drlc.intermediate.ast.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class RawTypeNode extends ASTNode<Scope, Routine> {
	
	public boolean setTypeInfo = false;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	protected RawTypeNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	public void setTypeInfo() {
		if (!setTypeInfo) {
			setTypeInfoInternal();
		}
		setTypeInfo = true;
	}
	
	protected abstract void setTypeInfoInternal();
}
