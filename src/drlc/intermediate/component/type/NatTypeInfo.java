package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class NatTypeInfo extends BasicTypeInfo {
	
	protected NatTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, @NonNull RawType type) {
		super(node, referenceMutability, type);
	}
	
	public NatTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, Scope scope) {
		super(node, referenceMutability, scope, Global.NAT);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, List<Boolean> referenceMutability) {
		return new NatTypeInfo(node, referenceMutability, rawType);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
