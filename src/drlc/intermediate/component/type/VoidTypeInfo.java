package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class VoidTypeInfo extends BasicTypeInfo {
	
	protected VoidTypeInfo(ASTNode<?, ?> node, @NonNull RawType type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public VoidTypeInfo(ASTNode<?, ?> node, Scope scope, int referenceLevel) {
		super(node, scope, Global.VOID, referenceLevel);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new VoidTypeInfo(node, rawType, newReferenceLevel);
	}
	
	@Override
	public boolean isVoid() {
		return !isAddress();
	}
	
	@Override
	public int getAddressOffsetSize(ASTNode<?, ?> node) {
		return equals(Main.generator.wildcardPtrTypeInfo) ? 1 : super.getAddressOffsetSize(node);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (equals(otherInfo)) {
			return true;
		}
		else {
			return equals(Main.generator.wildcardPtrTypeInfo) && otherInfo.isAddress();
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return isAddress() && !equals(Main.generator.wildcardPtrTypeInfo) ? Main.generator.wildcardPtrTypeInfo : null;
	}
}
