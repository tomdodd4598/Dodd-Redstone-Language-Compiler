package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;

public class ClosureTypeInfo extends CompoundTypeInfo {
	
	public final @NonNull String name;
	public final @NonNull Function function;
	
	public ClosureTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull String name, @NonNull Function function) {
		super(node, referenceMutability, function.captureTypeInfos);
		this.name = name;
		this.function = function;
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new ClosureTypeInfo(node, referenceMutability, name, function);
	}
	
	@Override
	public @Nullable FunctionTypeInfo getFunction() {
		return isAddress() ? null : (FunctionTypeInfo) function.value.typeInfo;
	}
	
	@Override
	public boolean isClosure() {
		return !isAddress();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, function);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof ClosureTypeInfo) {
			ClosureTypeInfo other = (ClosureTypeInfo) obj;
			return super.equalsOther(obj, ignoreReferenceMutability) && function.equals(other.function);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.FN + Helpers.captureString(typeInfos) + Helpers.listString(((FunctionTypeInfo) function.value.typeInfo).getArgTypeInfos()) + " " + Global.ARROW + " " + function.returnTypeInfo + " " + Global.BRACE_START + function.name + Global.BRACE_END;
	}
	
	@Override
	public String routineString() {
		return Global.FN + Helpers.captureString(Helpers.map(typeInfos, TypeInfo::routineString)) + Helpers.listString(Helpers.map(((FunctionTypeInfo) function.value.typeInfo).getArgTypeInfos(), TypeInfo::routineString)) + " " + Global.ARROW + " " + function.returnTypeInfo.routineString() + " " + Global.BRACE_START + function.name + Global.BRACE_END;
	}
}
