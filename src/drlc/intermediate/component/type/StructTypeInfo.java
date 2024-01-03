package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.scope.Scope;

public class StructTypeInfo extends CompoundTypeInfo {
	
	public final @NonNull RawType rawType;
	
	public final @NonNull TupleTypeInfo tupleTypeInfo;
	
	protected StructTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos, @NonNull RawType rawType) {
		super(node, referenceMutability, typeInfos);
		this.rawType = rawType;
		tupleTypeInfo = new TupleTypeInfo(null, referenceMutability, typeInfos);
	}
	
	public StructTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos, Scope scope, @NonNull String rawTypeName) {
		this(node, referenceMutability, typeInfos, scope.getRawType(node, rawTypeName));
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, List<Boolean> referenceMutability) {
		return new StructTypeInfo(node, referenceMutability, typeInfos, rawType);
	}
	
	@Override
	public boolean exists(Scope scope) {
		return scope.rawTypeExists(rawType.name, false) && super.exists(scope);
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : rawType.size;
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (super.equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo)) {
			return !(otherInfo instanceof StructTypeInfo) || rawType.equals(((StructTypeInfo) otherInfo).rawType);
		}
		else {
			return tupleTypeInfo.canImplicitCastTo(otherInfo) || (isAddress() && otherInfo.equals(Main.generator.wildcardPtrTypeInfo));
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return tupleTypeInfo;
	}
	
	@Override
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return isAddress() ? null : rawType.getMemberInfo(memberName);
	}
	
	@Override
	public void collectRawTypes(Set<RawType> rawTypes) {
		if (!isAddress()) {
			rawTypes.add(rawType);
		}
		super.collectRawTypes(rawTypes);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, nonRecursiveTypeInfos(x -> null), rawType);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof StructTypeInfo) {
			StructTypeInfo other = (StructTypeInfo) obj;
			return super.equalsOther(obj, ignoreReferenceMutability) && rawType.equals(other.rawType);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Helpers.structString(rawType, nonRecursiveTypeInfos(x -> x.getReferenceMutabilityString() + ((StructTypeInfo) x).rawType));
	}
	
	@Override
	public String routineString() {
		return getRoutineReferenceString() + Helpers.structString(rawType, nonRecursiveTypeInfos(x -> x.getRoutineReferenceString() + ((StructTypeInfo) x).rawType));
	}
}
