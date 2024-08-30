package drlc.intermediate.component;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.component.data.VariableDataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public class Variable {
	
	public final @NonNull String name;
	public final @NonNull VariableModifier modifier;
	public final @NonNull TypeInfo typeInfo;
	
	public Scope scope = null;
	
	public Variable(@NonNull String name, @NonNull VariableModifier modifier, @NonNull TypeInfo typeInfo) {
		this.name = name;
		this.modifier = modifier;
		this.typeInfo = typeInfo;
	}
	
	public @NonNull Variable copy() {
		return new Variable(name, modifier, typeInfo);
	}
	
	public @NonNull VariableDataId dataId() {
		return new VariableDataId(0, this);
	}
	
	public int hashCode(boolean reduced) {
		return Objects.hash(name, reduced ? null : modifier, reduced ? null : typeInfo, scope);
	}
	
	@Override
	public int hashCode() {
		return hashCode(false);
	}
	
	public boolean equalsOther(Object obj, boolean raw) {
		if (obj instanceof Variable other) {
			boolean equalModifiers = raw || modifier.equals(other.modifier);
			boolean equalTypeInfos = raw || typeInfo.equals(other.typeInfo);
			return name.equals(other.name) && equalModifiers && equalTypeInfos && scope.equals(other.scope);
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return equalsOther(obj, false);
	}
	
	@Override
	public String toString() {
		return modifier + name + Global.TYPE_ANNOTATION_PREFIX + " " + typeInfo;
	}
	
	public String routineString() {
		return modifier.routineString() + name + Global.TYPE_ANNOTATION_PREFIX + " " + typeInfo.routineString();
	}
}
