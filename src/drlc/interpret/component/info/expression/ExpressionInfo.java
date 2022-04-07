package drlc.interpret.component.info.expression;

import drlc.Global;
import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public abstract class ExpressionInfo<T extends ExpressionInfo<?>> {
	
	public Generator generator;
	
	protected TypeInfo typeInfo;
	
	public boolean isDirectFunction = false;
	
	protected ExpressionInfo(Generator generator) {
		this(generator, null);
	}
	
	protected ExpressionInfo(Generator generator, TypeInfo typeInfo) {
		this.generator = generator;
		this.typeInfo = typeInfo;
	}
	
	public abstract T copy(Node node);
	
	public abstract boolean isLvalue();
	
	public abstract boolean isRvalue();
	
	public TypeInfo getTypeInfo() {
		return typeInfo;
	}
	
	public void setTypeInfo(Node node, TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	public void checkNotVoid(Node node) {
		if (typeInfo.isVoid(node, generator)) {
			throw new IllegalArgumentException(String.format("Expression can not have void type! %s", node));
		}
	}
	
	public void binaryOp(Node node, BinaryOpType opType, ExpressionInfo<?> otherInfo) {
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
			case LOGICAL_XOR:
			case EQUAL_TO:
			case NOT_EQUAL_TO:
			case LESS_THAN:
			case LESS_OR_EQUAL:
			case MORE_THAN:
			case MORE_OR_EQUAL:
				if (!typeInfo.isValidForLogicalBinaryOp(node, generator, opType) || !otherInfo.typeInfo.isValidForLogicalBinaryOp(node, generator, opType)) {
					binaryOpError(node, opType, otherInfo);
				}
				typeInfo = Global.INT_TYPE_INFO;
				break;
			case PLUS:
			case AND:
			case OR:
			case XOR:
			case MINUS:
			case ARITHMETIC_LEFT_SHIFT:
			case ARITHMETIC_RIGHT_SHIFT:
			case LOGICAL_RIGHT_SHIFT:
			case CIRCULAR_LEFT_SHIFT:
			case CIRCULAR_RIGHT_SHIFT:
			case MULTIPLY:
			case DIVIDE:
			case REMAINDER:
				if (!typeInfo.isValidForArithmeticBinaryOp(node, generator, opType) || !otherInfo.typeInfo.isValidForArithmeticBinaryOp(node, generator, opType)) {
					binaryOpError(node, opType, otherInfo);
				}
				break;
			default:
				throw new IllegalArgumentException(String.format("Attempted to write an expression including a binary op of unknown type! %s", node));
		}
	}
	
	public void binaryOpError(Node node, BinaryOpType opType, ExpressionInfo<?> otherInfo) {
		throw new IllegalArgumentException(String.format("Binary op \"%s\" can not act on expressions of types \"%s\" and \"%s\"! %s", opType, otherInfo.typeInfo, typeInfo, node));
	}
	
	public void unaryOp(Node node, UnaryOpType opType) {
		switch (opType) {
			case PLUS:
			case MINUS:
			case COMPLEMENT:
			case TO_BOOL:
			case NOT:
				if (!typeInfo.isValidForUnaryOp(node, generator, opType)) {
					unaryOpError(node, opType);
				}
				break;
			default:
				throw new IllegalArgumentException(String.format("Attempted to write an expression including a unary op of unknown type! %s", node));
		}
	}
	
	public void unaryOpError(Node node, UnaryOpType opType) {
		throw new IllegalArgumentException(String.format("Unary op \"%s\" can not act on an expression of type \"%s\"! %s", opType, typeInfo, node));
	}
	
	public void incrementReferenceLevel(Node node) {
		typeInfo = typeInfo.copy(node, typeInfo.referenceLevel + 1);
	}
	
	public void decrementReferenceLevel(Node node) {
		if (typeInfo.referenceLevel < 1) {
			throw new IllegalArgumentException(String.format("Can not dereference expression of type \"%s\"! %s", typeInfo.typeString(), node));
		}
		else {
			typeInfo = typeInfo.copy(node, typeInfo.referenceLevel - 1);
		}
	}
	
	@Override
	public abstract boolean equals(Object obj);
}
