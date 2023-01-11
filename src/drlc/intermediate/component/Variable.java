package drlc.intermediate.component;

import java.util.Objects;

import drlc.Helpers;
import drlc.intermediate.Scope;
import drlc.intermediate.component.info.VariableModifierInfo;
import drlc.intermediate.component.type.TypeInfo;

public class Variable {
	
	public final String name;
	public final VariableModifierInfo modifierInfo;
	public final TypeInfo typeInfo;
	public Scope scope;
	
	public Variable(String name, VariableModifierInfo modifierInfo, TypeInfo typeInfo) {
		this.name = name;
		this.modifierInfo = modifierInfo;
		this.typeInfo = typeInfo;
	}
	
	public String toLvalueString(int dereferenceLevel) {
		return Helpers.addDereferences(name, dereferenceLevel);
	}
	
	public DataId lvalueDataId(int dereferenceLevel) {
		return new DataId(toLvalueString(dereferenceLevel), scope);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, modifierInfo, typeInfo, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			Variable other = (Variable) obj;
			return name.equals(other.name) && modifierInfo.equals(other.modifierInfo) && typeInfo.equals(other.typeInfo) && scope.equals(other.scope);
		}
		else {
			return false;
		}
	}
}
