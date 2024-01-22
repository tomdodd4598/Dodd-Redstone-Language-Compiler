package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.scope.Scope;

public class TypeDef {
	
	public final @NonNull String name;
	public int size;
	public Map<String, MemberInfo> memberMap;
	public @NonNull TypeInfoFunction supplier;
	
	public Scope scope = null;
	
	public TypeDef(@NonNull String name, int size, Map<String, MemberInfo> memberMap, @NonNull TypeInfoFunction supplier) {
		this.name = name;
		this.size = size;
		this.memberMap = memberMap;
		this.supplier = supplier;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, Scope scope) {
		return supplier.create(node, referenceMutability, scope);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, size, memberMap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeDef) {
			TypeDef other = (TypeDef) obj;
			return name.equals(other.name) && size == other.size && memberMap.equals(other.memberMap) && Objects.equals(scope, other.scope);
		}
		else {
			return false;
		}
	}
	
	public String rawString() {
		return name;
	}
	
	@Override
	public String toString() {
		return Helpers.scopeStringPrefix(scope) + rawString();
	}
}
