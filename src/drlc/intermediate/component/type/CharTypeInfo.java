package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class CharTypeInfo extends BasicTypeInfo {
	
	protected CharTypeInfo(ASTNode node, @NonNull RawType rawType, int referenceLevel) {
		super(node, rawType, referenceLevel);
	}
	
	public CharTypeInfo(ASTNode node, Scope scope, int referenceLevel) {
		super(node, scope, Global.CHAR, referenceLevel);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode node, int newReferenceLevel) {
		return new CharTypeInfo(node, rawType, newReferenceLevel);
	}
}
