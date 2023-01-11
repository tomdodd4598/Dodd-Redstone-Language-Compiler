package drlc.intermediate.component.constant;

import drlc.Helpers;
import drlc.intermediate.component.type.IntTypeInfo;
import drlc.node.Node;

public class IntConstant extends LongConstant<IntConstant, IntTypeInfo> {
	
	public IntConstant(Node node, IntTypeInfo typeInfo, Long value) {
		super(node, typeInfo, value);
	}
	
	public IntConstant(Node node, IntTypeInfo typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address, dummy);
	}
	
	@Override
	public boolean instanceofThis(Object obj) {
		return obj instanceof IntConstant;
	}
}
