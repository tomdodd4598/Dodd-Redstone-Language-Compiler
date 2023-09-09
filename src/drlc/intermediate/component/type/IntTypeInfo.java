package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class IntTypeInfo extends BasicTypeInfo {
	
	protected IntTypeInfo(ASTNode node, @NonNull RawType rawType, int referenceLevel) {
		super(node, rawType, referenceLevel);
	}
	
	public IntTypeInfo(ASTNode node, Scope scope, int referenceLevel) {
		super(node, scope, Global.INT, referenceLevel);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode node, int newReferenceLevel) {
		return new IntTypeInfo(node, rawType, newReferenceLevel);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
