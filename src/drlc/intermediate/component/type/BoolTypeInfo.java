package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class BoolTypeInfo extends BasicTypeInfo {
	
	protected BoolTypeInfo(ASTNode node, @NonNull RawType rawType, int referenceLevel) {
		super(node, rawType, referenceLevel);
	}
	
	public BoolTypeInfo(ASTNode node, Scope scope, int referenceLevel) {
		super(node, scope, Global.BOOL, referenceLevel);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode node, int newReferenceLevel) {
		return new BoolTypeInfo(node, rawType, newReferenceLevel);
	}
}
