package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;

public class FunctionPointerTypeInfo extends FunctionTypeInfo {
	
	public FunctionPointerTypeInfo(ASTNode node, int referenceLevel, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceLevel, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode node, int newReferenceLevel) {
		return new FunctionPointerTypeInfo(node, newReferenceLevel, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof FunctionPointerTypeInfo) {
			return super.equalsOther(obj, ignoreReferenceLevels);
		}
		else {
			return false;
		}
	}
}
