package drlc.intermediate.component.value;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.BasicTypeInfo;

public abstract class BasicValue<T extends BasicTypeInfo> extends Value<T> {
	
	public BasicValue(ASTNode<?> node, @NonNull T typeInfo) {
		super(node, typeInfo);
	}
}
