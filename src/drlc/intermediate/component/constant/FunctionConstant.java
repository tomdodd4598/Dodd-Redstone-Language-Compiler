package drlc.intermediate.component.constant;

import drlc.intermediate.component.type.FunctionTypeInfo;
import drlc.node.Node;

public class FunctionConstant extends Constant {
	
	public final String name;
	
	public FunctionConstant(Node node, FunctionTypeInfo typeInfo, String name) {
		super(node, typeInfo);
		this.name = typeInfo.referenceLevel > 0 ? null : name;
	}
	
	public FunctionConstant(Node node, FunctionTypeInfo typeInfo, Long address) {
		super(node, typeInfo, address);
		
		if (typeInfo.referenceLevel > 0) {
			name = null;
		}
		else {
			throw new IllegalArgumentException(String.format("Constant of type \"%s\" must have a value! %s", typeInfo, node));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionConstant) {
			return super.equals(obj) && name.equals(((FunctionConstant) obj).name);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return name;
	}
}
