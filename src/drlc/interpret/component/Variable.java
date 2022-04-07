package drlc.interpret.component;

import java.util.Objects;

import drlc.Helpers;
import drlc.generate.Generator;
import drlc.interpret.component.info.VariableModifierInfo;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class Variable {
	
	public final String name;
	public final VariableModifierInfo modifierInfo;
	public final TypeInfo baseTypeInfo;
	public Integer scopeId;
	
	public Variable(String name, VariableModifierInfo modifierInfo, TypeInfo baseTypeInfo) {
		this.name = name;
		this.modifierInfo = modifierInfo;
		this.baseTypeInfo = baseTypeInfo;
	}
	
	public int getSize(Node node, Generator generator, int dereferenceLevel) {
		if (dereferenceLevel == 0) {
			return baseTypeInfo.getSize(node, generator);
		}
		else {
			return baseTypeInfo.copy(node, baseTypeInfo.referenceLevel - dereferenceLevel).getSize(node, generator);
		}
	}
	
	public String toLvalueString(int dereferenceLevel) {
		return Helpers.addDereferences(name, dereferenceLevel);
	}
	
	public DataId lvalueDataId(int dereferenceLevel) {
		return new DataId(toLvalueString(dereferenceLevel), scopeId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, modifierInfo, baseTypeInfo, scopeId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			Variable other = (Variable) obj;
			return name.equals(other.name) && modifierInfo.equals(other.modifierInfo) && baseTypeInfo.equals(other.baseTypeInfo) && scopeId.equals(other.scopeId);
		}
		else {
			return false;
		}
	}
}
