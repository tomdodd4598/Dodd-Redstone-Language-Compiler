package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class CharTypeInfo extends BasicTypeInfo {
	
	protected CharTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull RawType type) {
		super(node, referenceLevel, type);
	}
	
	public CharTypeInfo(ASTNode<?, ?> node, int referenceLevel, Scope scope) {
		super(node, referenceLevel, scope, Global.CHAR);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new CharTypeInfo(node, newReferenceLevel, rawType);
	}
}
