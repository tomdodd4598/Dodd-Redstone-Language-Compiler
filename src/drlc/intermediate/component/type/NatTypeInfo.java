package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class NatTypeInfo extends BasicTypeInfo {
	
	protected NatTypeInfo(ASTNode<?, ?> node, @NonNull RawType rawType, int referenceLevel) {
		super(node, rawType, referenceLevel);
	}
	
	public NatTypeInfo(ASTNode<?, ?> node, Scope scope, int referenceLevel) {
		super(node, scope, Global.NAT, referenceLevel);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new NatTypeInfo(node, rawType, newReferenceLevel);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
