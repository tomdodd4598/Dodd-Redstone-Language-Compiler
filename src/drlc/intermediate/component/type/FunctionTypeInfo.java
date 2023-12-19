package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class FunctionTypeInfo extends TypeInfo {
	
	public final @NonNull TypeInfo returnTypeInfo;
	public final List<TypeInfo> paramTypeInfos;
	
	protected FunctionTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceLevel);
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
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof FunctionTypeInfo) {
			FunctionTypeInfo other = (FunctionTypeInfo) obj;
			boolean equalReferenceLevels = ignoreReferenceLevels || referenceLevel == other.referenceLevel;
			return equalReferenceLevels && returnTypeInfo.equals(other.returnTypeInfo) && paramTypeInfos.equals(other.paramTypeInfos);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Helpers.listString(paramTypeInfos) + " -> " + returnTypeInfo;
	}
}
