package drlc.intermediate.component.value;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.StructConstructorTypeInfo;
import drlc.intermediate.scope.Scope;

public class StructConstructorValue extends Value<StructConstructorTypeInfo> {
	
	public final @NonNull String name;
	public final @NonNull Scope scope;
	
	public StructConstructorValue(ASTNode<?> node, @NonNull StructConstructorTypeInfo typeInfo, @NonNull String name, @NonNull Scope scope) {
		super(node, typeInfo);
		this.name = name;
		this.scope = scope;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, name, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StructConstructorValue other) {
			return typeInfo.equals(other.typeInfo) && name.equals(other.name) && scope.equals(other.scope);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.scopeStringPrefix(scope) + name;
	}
}
