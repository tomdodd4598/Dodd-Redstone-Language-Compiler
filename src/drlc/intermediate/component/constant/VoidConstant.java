package drlc.intermediate.component.constant;

import drlc.Helpers;
import drlc.intermediate.component.type.VoidTypeInfo;
import drlc.node.Node;

public class VoidConstant extends BuiltInConstant<VoidConstant, VoidTypeInfo, Helpers.Dummy> {
	
	public VoidConstant(Node node, VoidTypeInfo typeInfo) {
		super(node, typeInfo, Helpers.Dummy.INSTANCE);
	}
	
	public VoidConstant(Node node, VoidTypeInfo typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address, dummy);
	}
	
	@Override
	public boolean instanceofThis(Object obj) {
		return obj instanceof VoidConstant;
	}
	
	@Override
	public String valueString() {
		return "void";
	}
}
