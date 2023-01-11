package drlc.intermediate.component.constant;

import drlc.Helpers;
import drlc.intermediate.component.type.CharTypeInfo;
import drlc.node.Node;

public class CharConstant extends BuiltInConstant<CharConstant, CharTypeInfo, Byte> {
	
	public CharConstant(Node node, CharTypeInfo typeInfo, Byte value) {
		super(node, typeInfo, value);
	}
	
	public CharConstant(Node node, CharTypeInfo typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address, dummy);
	}
	
	@Override
	public boolean instanceofThis(Object obj) {
		return obj instanceof CharConstant;
	}
}
