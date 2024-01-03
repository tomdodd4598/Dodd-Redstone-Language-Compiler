package drlc.intermediate.ast.type;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class TypeNode extends ASTNode<Scope, Routine> {
	
	public boolean setTypeInfo = false;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	protected TypeNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	public void setTypeInfo() {
		if (!setTypeInfo) {
			setTypeInfoInternal();
		}
		setTypeInfo = true;
	}
	
	protected abstract void setTypeInfoInternal();
	
	public abstract void collectRawTypes(Set<RawType> rawTypes);
}
