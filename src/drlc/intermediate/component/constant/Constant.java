package drlc.intermediate.component.constant;

import java.util.Objects;

import drlc.Global;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public abstract class Constant {
	
	public final TypeInfo typeInfo;
	public final Long address;
	
	public Constant(Node node, TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
		
		if (typeInfo.referenceLevel > 0) {
			throw new IllegalArgumentException(String.format("Constant of type \"%s\" must have an address! %s", typeInfo, node));
		}
		else {
			address = null;
		}
	}
	
	public Constant(Node node, TypeInfo typeInfo, Long address) {
		this.typeInfo = typeInfo;
		this.address = typeInfo.referenceLevel > 0 ? address : null;
	}
	
	public int intValue(Node node) {
		throw new IllegalArgumentException(String.format("Constant of type \"%s\" can not be cast to an int! %s", typeInfo, node));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Constant) {
			return typeInfo.equals(((Constant) obj).typeInfo) && Objects.equals(address, ((Constant) obj).address);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return (typeInfo.referenceLevel > 0 ? address.toString() : valueString()).concat(Global.TYPE_ANNOTATION_PREFIX).concat(" ").concat(typeInfo.toString());
	}
	
	public abstract String valueString();
}
