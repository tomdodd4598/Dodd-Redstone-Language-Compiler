package drlc.interpret.component.info.expression;

import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class LvalueExpressionInfo extends ExpressionInfo<LvalueExpressionInfo> {
	
	public LvalueExpressionInfo(Generator generator) {
		super(generator);
	}
	
	protected LvalueExpressionInfo(Generator generator, TypeInfo typeInfo) {
		super(generator, typeInfo);
	}
	
	@Override
	public LvalueExpressionInfo copy(Node node) {
		return new LvalueExpressionInfo(generator, typeInfo.copy(node, typeInfo.referenceLevel));
	}
	
	@Override
	public boolean isLvalue() {
		return true;
	}
	
	@Override
	public boolean isRvalue() {
		return false;
	}
	
	@Override
	public void setTypeInfo(Node node, TypeInfo typeInfo) {
		super.setTypeInfo(node, typeInfo);
	}
	
	@Override
	public void binaryOp(Node node, BinaryOpType opType, ExpressionInfo<?> otherInfo) {
		throw new IllegalArgumentException(String.format("Binary ops are not valid for lvalue expressions! %s", node));
	}
	
	@Override
	public void unaryOp(Node node, UnaryOpType opType) {
		throw new IllegalArgumentException(String.format("Unary ops are not valid for lvalue expressions! %s", node));
	}
	
	@Override
	public void incrementReferenceLevel(Node node) {
		throw new IllegalArgumentException(String.format("Reference level increment is not valid for lvalue expressions! %s", node));
	}
	
	@Override
	public void decrementReferenceLevel(Node node) {
		super.decrementReferenceLevel(node);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LvalueExpressionInfo) {
			return typeInfo.equals(((LvalueExpressionInfo) obj).typeInfo);
		}
		else {
			return false;
		}
	}
}
