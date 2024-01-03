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
	
	protected StructTypeInfo(ASTNode<?, ?> node, int referenceLevel, List<TypeInfo> typeInfos, @NonNull RawType rawType) {
		super(node, referenceLevel, typeInfos);
		this.rawType = rawType;
		
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
		
		tupleTypeInfo = new TupleTypeInfo(null, referenceLevel, typeInfos);
	}
	
	public StructTypeInfo(ASTNode<?, ?> node, int referenceLevel, List<TypeInfo> typeInfos, Scope scope, @NonNull String rawTypeName) {
		this(node, referenceLevel, typeInfos, scope.getRawType(node, rawTypeName));
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new StructTypeInfo(node, newReferenceLevel, typeInfos, rawType);
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
		if (super.equals(otherInfo)) {
			return !(otherInfo instanceof StructTypeInfo) || rawType.equals(((StructTypeInfo) otherInfo).rawType);
		}
		else {
			return otherInfo.equals(tupleTypeInfo) || (isAddress() && otherInfo.equals(Main.generator.wildcardPtrTypeInfo));
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return tupleTypeInfo;
	}
	
	@Override
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return referenceLevel == 0 ? rawType.getMemberInfo(memberName) : null;
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
		return Objects.hash(referenceLevel, nonRecursiveTypeInfos(x -> null), rawType);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof StructTypeInfo) {
			StructTypeInfo other = (StructTypeInfo) obj;
			return super.equalsOther(obj, ignoreReferenceLevels) && rawType.equals(other.rawType);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Helpers.structString(rawType, nonRecursiveTypeInfos(x -> Helpers.charLine(Global.ADDRESS_OF, x.referenceLevel) + ((StructTypeInfo) x).rawType));
	}
}
