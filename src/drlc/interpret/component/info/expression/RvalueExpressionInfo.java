package drlc.interpret.component.info.expression;

import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class RvalueExpressionInfo extends ExpressionInfo<RvalueExpressionInfo> {
	
	public RvalueExpressionInfo(Generator generator) {
		super(generator);
	}
	
	public RvalueExpressionInfo(Generator generator, TypeInfo typeInfo) {
		super(generator, typeInfo);
	}
	
	@Override
	public RvalueExpressionInfo copy(Node node) {
		return new RvalueExpressionInfo(generator, typeInfo.copy(node, typeInfo.referenceLevel));
	}
	
	@Override
	public boolean isLvalue() {
		return false;
	}
	
	@Override
	public boolean isRvalue() {
		return true;
	}
	
	@Override
	public void setTypeInfo(Node node, TypeInfo typeInfo) {
		super.setTypeInfo(node, typeInfo);
		// checkNotVoid(node);
	}
	
	@Override
	public void binaryOp(Node node, BinaryOpType opType, ExpressionInfo<?> otherInfo) {
		super.binaryOp(node, opType, otherInfo);
		// checkNotVoid(node);
	}
	
	@Override
	public void unaryOp(Node node, UnaryOpType opType) {
		super.unaryOp(node, opType);
		// checkNotVoid(node);
	}
	
	@Override
	public void incrementReferenceLevel(Node node) {
		super.incrementReferenceLevel(node);
		// checkNotVoid(node);
	}
	
	@Override
	public void decrementReferenceLevel(Node node) {
		super.decrementReferenceLevel(node);
		// checkNotVoid(node);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RvalueExpressionInfo) {
			return typeInfo.equals(((RvalueExpressionInfo) obj).typeInfo);
		}
		else {
			return false;
		}
	}
}
