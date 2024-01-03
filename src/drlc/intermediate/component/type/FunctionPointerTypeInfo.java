package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;

public class FunctionPointerTypeInfo extends FunctionTypeInfo {
	
	public FunctionPointerTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceLevel, returnTypeInfo, paramTypeInfos);
		
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new FunctionPointerTypeInfo(node, newReferenceLevel, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceLevel, returnTypeInfo, paramTypeInfos);
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
