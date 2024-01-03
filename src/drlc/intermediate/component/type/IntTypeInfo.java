package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class IntTypeInfo extends BasicTypeInfo {
	
	protected IntTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, @NonNull RawType type) {
		super(node, referenceMutability, type);
	}
	
	public IntTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, Scope scope) {
		super(node, referenceMutability, scope, Global.INT);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, List<Boolean> referenceMutability) {
		return new IntTypeInfo(node, referenceMutability, rawType);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
