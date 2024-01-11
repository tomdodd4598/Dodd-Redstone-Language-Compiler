package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;

public class FunctionPointerTypeInfo extends FunctionTypeInfo {
	
	public FunctionPointerTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceMutability, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new FunctionPointerTypeInfo(node, referenceMutability, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof FunctionPointerTypeInfo) {
			return super.equalsOther(obj, ignoreReferenceMutability);
		}
		else {
			return false;
		}
	}
}
