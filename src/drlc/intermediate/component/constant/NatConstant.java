package drlc.intermediate.component.constant;

import drlc.Helpers;
import drlc.intermediate.component.type.NatTypeInfo;
import drlc.node.Node;

public class NatConstant extends LongConstant<NatConstant, NatTypeInfo> {
	
	public NatConstant(Node node, NatTypeInfo typeInfo, Long value) {
		super(node, typeInfo, value);
	}
	
	public NatConstant(Node node, NatTypeInfo typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address, dummy);
	}
	
	@Override
	public boolean instanceofThis(Object obj) {
		return obj instanceof NatConstant;
	}
}
