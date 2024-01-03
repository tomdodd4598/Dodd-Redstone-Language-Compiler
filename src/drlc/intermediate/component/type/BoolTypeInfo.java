package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class BoolTypeInfo extends BasicTypeInfo {
	
	protected BoolTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull RawType type) {
		super(node, referenceLevel, type);
	}
	
	public BoolTypeInfo(ASTNode<?, ?> node, int referenceLevel, Scope scope) {
		super(node, referenceLevel, scope, Global.BOOL);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new BoolTypeInfo(node, newReferenceLevel, rawType);
	}
}
