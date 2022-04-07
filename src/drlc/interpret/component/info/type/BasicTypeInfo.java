package drlc.interpret.component.info.type;

import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.node.Node;

public class BasicTypeInfo extends TypeInfo {
	
	public BasicTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new BasicTypeInfo(node, type, newReferenceLevel);
	}
	
	@Override
	public boolean canCastTo(Node node, Generator generator, TypeInfo other) {
		return referenceLevel == other.referenceLevel && getSize(node, generator) == other.getSize(node, generator);
	}
	
	@Override
	public boolean isFunction() {
		return false;
	}
	
	@Override
	public boolean isNonAddressable() {
		return false;
	}
	
	@Override
	public boolean isValidForLogicalBinaryOp(Node node, Generator generator, BinaryOpType opType) {
		return getSize(node, generator) == 1;
	}
	
	@Override
	public boolean isValidForArithmeticBinaryOp(Node node, Generator generator, BinaryOpType opType) {
		return !isAddress(node) && getSize(node, generator) == 1;
	}
	
	@Override
	public boolean isValidForUnaryOp(Node node, Generator generator, UnaryOpType opType) {
		return !isAddress(node) && getSize(node, generator) == 1;
	}
	
	@Override
	public String typeString() {
		return type.toString();
	}
}
