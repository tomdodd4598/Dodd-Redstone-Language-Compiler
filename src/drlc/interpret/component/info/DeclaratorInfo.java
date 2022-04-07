package drlc.interpret.component.info;

import drlc.*;
import drlc.interpret.component.*;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class DeclaratorInfo {
	
	public final Variable variable;
	public final int dereferenceLevel;
	public final TypeInfo typeInfo;
	
	public DeclaratorInfo(Node node, Variable variable, int dereferenceLevel) {
		this.variable = variable;
		if (dereferenceLevel < 0) {
			throw new IllegalArgumentException(String.format("Can not use the address of the variable \"%s\" as a declarator!", variable.name));
		}
		else {
			this.dereferenceLevel = dereferenceLevel;
		}
		typeInfo = variable.baseTypeInfo.copy(node, variable.baseTypeInfo.referenceLevel - dereferenceLevel);
	}
	
	public DataId dataId() {
		return new DataId(toString(), variable.scopeId);
	}
	
	@Override
	public String toString() {
		return Helpers.addDereferences(variable.name, dereferenceLevel);
	}
	
	public String toDeclarationString() {
		return variable.modifierInfo.toString().concat(Helpers.isDiscardParam(variable.name) ? Global.DISCARD : toString()).concat(" ").concat(typeInfo.toString());
	}
}
