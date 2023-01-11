package drlc.intermediate.component.info;

import drlc.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class DeclaratorInfo {
	
	public final Variable variable;
	
	public DeclaratorInfo(Node node, Variable variable) {
		this.variable = variable;
	}
	
	public DataId dataId() {
		return new DataId(toString(), variable.scope);
	}
	
	public TypeInfo getTypeInfo() {
		return variable.typeInfo;
	}
	
	@Override
	public String toString() {
		return variable.name;
	}
	
	public String toDeclarationString() {
		return variable.modifierInfo.toString().concat(Helpers.isDiscardParam(variable.name) ? Global.DISCARD : toString()).concat(" ").concat(getTypeInfo().toString());
	}
}
