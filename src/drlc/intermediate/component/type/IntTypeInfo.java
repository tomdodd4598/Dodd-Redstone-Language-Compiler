package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class IntTypeInfo extends BasicTypeInfo {
	
	protected IntTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull RawType type) {
		super(node, referenceLevel, type);
	}
	
	public IntTypeInfo(ASTNode<?, ?> node, int referenceLevel, Scope scope) {
		super(node, referenceLevel, scope, Global.INT);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new IntTypeInfo(node, newReferenceLevel, rawType);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
