package drlc.intermediate.component.type;

import drlc.*;
import drlc.node.Node;

public abstract class TypeInfo {
	
	public final Type type;
	public final int referenceLevel;
	
	protected TypeInfo(Node node, Type type, int referenceLevel) {
		this.type = type;
		this.referenceLevel = referenceLevel;
		if (referenceLevel < 0) {
			throw new IllegalArgumentException(String.format("Reference level of type \"%s\" can not be negative! %s", typeString(), node));
		}
	}
	
	public abstract TypeInfo copy(Node node, int newReferenceLevel);
	
	public boolean isAddress(Node node) {
		if (referenceLevel < 0) {
			throw new IllegalArgumentException(String.format("Reference level of type \"%s\" can not be negative! %s", typeString(), node));
		}
		else {
			return referenceLevel > 0;
		}
	}
	
	public boolean isVoid(Node node) {
		return false;
	}
	
	public boolean isInteger(Node node) {
		return false;
	}
	
	public int getSize(Node node, Generator generator) {
		return isAddress(node) ? generator.getAddressSize() : type.size;
	}
	
	public int getDerefSize(Node node, Generator generator) {
		return copy(node, referenceLevel - 1).getSize(node, generator);
	}
	
	public boolean canImplicitCastTo(Node node, Generator generator, TypeInfo otherInfo) {
		if (equals(otherInfo)) {
			return true;
		}
		
		if (isAddress(node)) {
			return equals(generator.wildcardPtrTypeInfo) || otherInfo.equals(generator.wildcardPtrTypeInfo);
		}
		else {
			return isInteger(node) && otherInfo.equals(generator.wildcardPtrTypeInfo);
		}
	}
	
	public abstract boolean isFunction();
	
	public abstract boolean isAddressable();
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public String toString() {
		return Helpers.charLine(Global.ADDRESS_OF, referenceLevel).concat(typeString());
	}
	
	public abstract String typeString();
}
