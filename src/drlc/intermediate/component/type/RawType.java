package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.scope.Scope;

public class RawType {
	
	public final @NonNull String name;
	public int size;
	public Map<String, MemberInfo> memberMap;
	public @NonNull TypeInfoFunction supplier;
	
	public Scope scope = null;
	
	public RawType(@NonNull String name, int size, Map<String, MemberInfo> memberMap, @NonNull TypeInfoFunction supplier) {
		this.name = name;
		this.size = size;
		this.memberMap = memberMap;
		this.supplier = supplier;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, Scope scope) {
		return supplier.create(node, referenceMutability, scope);
	}
	
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return memberMap.get(memberName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, size, memberMap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RawType) {
			RawType other = (RawType) obj;
			return name.equals(other.name) && size == other.size && memberMap.equals(other.memberMap) && Objects.equals(scope, other.scope);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return Helpers.scopeStringPrefix(scope) + name;
	}
}
