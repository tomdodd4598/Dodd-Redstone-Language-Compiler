package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

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
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo instanceof ClosureTypeInfo) {
			return super.canImplicitCastTo(otherInfo) && function.equals(((ClosureTypeInfo) otherInfo).function);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, function);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof ClosureTypeInfo) {
			return super.equalsOther(obj, ignoreReferenceMutability) && function.equals(((ClosureTypeInfo) obj).function);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.FN + " " + function.name + Helpers.captureString(typeInfos) + Helpers.listString((function.value.typeInfo).getArgTypeInfos()) + " " + Global.ARROW + " " + function.returnTypeInfo;
	}
	
	@Override
	public String rawRoutineString() {
		return Global.FN + " " + function.name + Helpers.captureString(Helpers.map(typeInfos, TypeInfo::routineString)) + Helpers.listString(Helpers.map((function.value.typeInfo).getArgTypeInfos(), TypeInfo::routineString)) + " " + Global.ARROW + " " + function.returnTypeInfo.routineString();
	}
}
