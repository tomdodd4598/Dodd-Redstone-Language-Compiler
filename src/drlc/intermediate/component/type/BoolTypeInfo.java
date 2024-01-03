package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class BoolTypeInfo extends BasicTypeInfo {
	
	protected BoolTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, @NonNull RawType type) {
		super(node, referenceMutability, type);
	}
	
	public BoolTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, Scope scope) {
		super(node, referenceMutability, scope, Global.BOOL);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, List<Boolean> referenceMutability) {
		return new BoolTypeInfo(node, referenceMutability, rawType);
	}
}
