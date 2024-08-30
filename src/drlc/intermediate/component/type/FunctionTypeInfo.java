package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;

public abstract class FunctionTypeInfo extends TypeInfo {
	
	public @NonNull TypeInfo returnTypeInfo;
	protected final List<TypeInfo> paramTypeInfos;
	
	protected FunctionTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceMutability);
		this.returnTypeInfo = returnTypeInfo;
		this.paramTypeInfos = paramTypeInfos;
	}
	
	public List<TypeInfo> getArgTypeInfos() {
		return paramTypeInfos;
	}
	
	public void updateReturnType(@NonNull TypeInfo returnType) {
		returnTypeInfo = returnType;
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : Main.generator.getFunctionSize();
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof FunctionTypeInfo other) {
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
	public String rawRoutineString() {
		return Global.FN + Helpers.listString(Helpers.map(paramTypeInfos, TypeInfo::routineString)) + " " + Global.ARROW + " " + returnTypeInfo.routineString();
	}
}
