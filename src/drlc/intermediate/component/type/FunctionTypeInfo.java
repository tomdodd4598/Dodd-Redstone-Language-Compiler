package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class FunctionTypeInfo extends TypeInfo {
	
	public final @NonNull TypeInfo returnTypeInfo;
	public final List<TypeInfo> paramTypeInfos;
	
	protected FunctionTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceMutability);
		this.returnTypeInfo = returnTypeInfo;
		this.paramTypeInfos = paramTypeInfos;
	}
	
	@Override
	public boolean exists(Scope scope) {
		return returnTypeInfo.exists(scope) && paramTypeInfos.stream().allMatch(x -> x.exists(scope));
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : Main.generator.getFunctionSize();
	}
	
	@Override
	public boolean isFunction() {
		return !isAddress();
	}
	
	@Override
	public void collectRawTypes(Set<RawType> rawTypes) {}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof FunctionTypeInfo) {
			FunctionTypeInfo other = (FunctionTypeInfo) obj;
			boolean equalReferenceMutability = ignoreReferenceMutability || referenceMutability.equals(other.referenceMutability);
			return equalReferenceMutability && returnTypeInfo.equals(other.returnTypeInfo) && paramTypeInfos.equals(other.paramTypeInfos);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.FN + Helpers.listString(paramTypeInfos) + " " + Global.ARROW + " " + returnTypeInfo;
	}
	
	@Override
	public String routineString() {
		return getRoutineReferenceString() + Global.FN + Helpers.listString(Helpers.map(paramTypeInfos, TypeInfo::routineString)) + " " + Global.ARROW + " " + returnTypeInfo.routineString();
	}
}
