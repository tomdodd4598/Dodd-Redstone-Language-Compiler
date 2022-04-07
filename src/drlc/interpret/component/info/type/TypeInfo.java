package drlc.interpret.component.info.type;

import drlc.*;
import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.node.Node;

public abstract class TypeInfo {
	
	public final Type type;
	public final int referenceLevel;
	
	protected TypeInfo(Node node, Type type, int referenceLevel) {
		this.type = type;
		if (referenceLevel < 0) {
			throw new IllegalArgumentException(String.format("Reference level of type \"%s\" can not be negative! %s", typeString(), node));
		}
		this.referenceLevel = referenceLevel;
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
	
	public int getSize(Node node, Generator generator) {
		return isAddress(node) ? generator.getAddressSize() : type.size;
	}
	
	public boolean isVoid(Node node, Generator generator) {
		return getSize(node, generator) == 0;
	}
	
	public abstract boolean canCastTo(Node node, Generator generator, TypeInfo other);
	
	public abstract boolean isFunction();
	
	public abstract boolean isNonAddressable();
	
	public abstract boolean isValidForLogicalBinaryOp(Node node, Generator generator, BinaryOpType opType);
	
	public abstract boolean isValidForArithmeticBinaryOp(Node node, Generator generator, BinaryOpType opType);
	
	public abstract boolean isValidForUnaryOp(Node node, Generator generator, UnaryOpType opType);
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeInfo) {
			TypeInfo other = (TypeInfo) obj;
			return type.equals(other.type) && referenceLevel == other.referenceLevel;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return Helpers.charLine(Global.ADDRESS_OF, referenceLevel).concat(typeString());
	}
	
	public abstract String typeString();
}
