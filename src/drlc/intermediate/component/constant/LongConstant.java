package drlc.intermediate.component.constant;

import drlc.Helpers;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public abstract class LongConstant<C extends LongConstant<C, T>, T extends TypeInfo> extends BuiltInConstant<C, T, Long> {
	
	public LongConstant(Node node, T typeInfo, Long value) {
		super(node, typeInfo, value);
	}
	
	public LongConstant(Node node, T typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address, dummy);
	}
	
	@Override
	public int intValue(Node node) {
		return value.intValue();
	}
}
