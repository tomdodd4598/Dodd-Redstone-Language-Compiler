package drlc.intermediate.component.value;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

public abstract class BasicValue extends Value {
	
	public BasicValue(ASTNode<?, ?> node, @NonNull TypeInfo typeInfo) {
		super(node, typeInfo);
	}
}
