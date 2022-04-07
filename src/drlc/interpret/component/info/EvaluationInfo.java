package drlc.interpret.component.info;

import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.interpret.component.info.expression.RvalueExpressionInfo;
import drlc.node.Node;

public class EvaluationInfo {
	
	public RvalueExpressionInfo expressionInfo;
	public long value;
	
	public EvaluationInfo(Node node, RvalueExpressionInfo expressionInfo, long value) {
		this.expressionInfo = expressionInfo;
		this.value = value;
		cast();
	}
	
	public Generator generator() {
		return expressionInfo.generator;
	}
	
	public void cast() {
		value = generator().castInteger(value);
	}
	
	public void binaryOp(Node node, BinaryOpType opType, EvaluationInfo otherInfo) {
		expressionInfo.binaryOp(node, opType, otherInfo.expressionInfo);
		value = generator().binaryOp(node, opType, value, otherInfo.value);
		cast();
	}
	
	public EvaluationInfo unaryOp(Node node, UnaryOpType opType) {
		expressionInfo.unaryOp(node, opType);
		value = generator().unaryOp(node, opType, value);
		cast();
		return this;
	}
}
