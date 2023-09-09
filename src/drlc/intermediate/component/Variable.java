package drlc.intermediate.component;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public class Variable {
	
	public final @NonNull String name;
	public final @NonNull VariableModifier modifier;
	public final @NonNull TypeInfo typeInfo;
	
	public Scope scope;
	
	public Variable(@NonNull String name, @NonNull VariableModifier modifier, @NonNull TypeInfo typeInfo) {
		this.name = name;
		this.modifier = modifier;
		this.typeInfo = typeInfo;
	}
	
	public boolean isStatic() {
		return modifier._static;
	}
	
	public boolean isMutable() {
		return modifier.mutable;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, modifier, typeInfo, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			Variable other = (Variable) obj;
			return name.equals(other.name) && modifier.equals(other.modifier) && typeInfo.equals(other.typeInfo) && scope.equals(other.scope);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return modifier + (Helpers.isDiscardParam(name) ? Global.DISCARD : name) + ": " + typeInfo;
	}
}
