package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;

public class TupleTypeInfo extends CompoundTypeInfo {
	
	protected Map<String, MemberInfo> memberMap = new HashMap<>();
	
	@SuppressWarnings("null")
	public TupleTypeInfo(ASTNode<?, ?> node, int referenceLevel, List<TypeInfo> typeInfos) {
		super(node, referenceLevel, typeInfos);
		
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
		
		if (referenceLevel == 0) {
			int offset = 0;
			for (int i = 0; i < count; ++i) {
				@NonNull String name = Integer.toString(i);
				@NonNull TypeInfo typeInfo = typeInfos.get(i);
				memberMap.put(name, new MemberInfo(name, typeInfo, i, offset));
				offset += typeInfo.getSize();
			}
		}
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new TupleTypeInfo(node, newReferenceLevel, typeInfos);
	}
	
	@Override
	public int getAddressOffsetSize(ASTNode<?, ?> node) {
		return equals(Main.generator.wildcardPtrTypeInfo) ? 1 : super.getAddressOffsetSize(node);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (equals(otherInfo)) {
			return true;
		}
		else {
			return equals(Main.generator.wildcardPtrTypeInfo) && otherInfo.isAddress();
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return isAddress() && !equals(Main.generator.wildcardPtrTypeInfo) ? Main.generator.wildcardPtrTypeInfo : null;
	}
	
	@Override
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return memberMap.get(memberName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceLevel, nonRecursiveTypeInfos(x -> null));
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof TupleTypeInfo) {
			return super.equalsOther(obj, ignoreReferenceLevels);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Helpers.tupleString(typeInfos);
	}
}
