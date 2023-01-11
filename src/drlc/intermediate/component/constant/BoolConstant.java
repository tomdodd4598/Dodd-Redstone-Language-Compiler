package drlc.intermediate.component.constant;

import drlc.Helpers;
import drlc.intermediate.component.type.BoolTypeInfo;
import drlc.node.Node;

public class BoolConstant extends BuiltInConstant<BoolConstant, BoolTypeInfo, Boolean> {
	
	public BoolConstant(Node node, BoolTypeInfo typeInfo, Boolean value) {
		super(node, typeInfo, value);
	}
	
	public BoolConstant(Node node, BoolTypeInfo typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address, dummy);
	}
	
	@Override
	public boolean instanceofThis(Object obj) {
		return obj instanceof BoolConstant;
	}
}
