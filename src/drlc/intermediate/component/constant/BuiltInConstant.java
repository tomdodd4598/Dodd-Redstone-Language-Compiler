package drlc.intermediate.component.constant;

import java.util.Objects;

import drlc.Helpers;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public abstract class BuiltInConstant<C extends BuiltInConstant<C, T, V>, T extends TypeInfo, V> extends Constant {
	
	public final V value;
	
	public BuiltInConstant(Node node, T typeInfo, V value) {
		super(node, typeInfo);
		this.value = typeInfo.referenceLevel > 0 ? null : value;
	}
	
	public BuiltInConstant(Node node, T typeInfo, Long address, Helpers.Dummy dummy) {
		super(node, typeInfo, address);
		
		if (typeInfo.referenceLevel > 0) {
			value = null;
		}
		else {
			throw new IllegalArgumentException(String.format("Constant of type \"%s\" must have a value! %s", typeInfo, node));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (instanceofThis(obj)) {
			return super.equals(obj) && Objects.equals(value, ((C) obj).value);
		}
		else {
			return false;
		}
	}
	
	public abstract boolean instanceofThis(Object obj);
	
	@Override
	public String valueString() {
		return value.toString();
	}
}
