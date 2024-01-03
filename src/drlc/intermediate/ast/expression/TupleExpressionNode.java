package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class TupleExpressionNode extends ExpressionNode {
	
	public final @NonNull List<ExpressionNode> expressionNodes;
	
	public final int count;
	
	@SuppressWarnings("null")
	public @NonNull TupleTypeInfo typeInfo = null;
	
	public @Nullable TupleValue constantValue = null;
	
	public TupleExpressionNode(Node[] parseNodes, @NonNull List<ExpressionNode> expressionNodes) {
		super(parseNodes);
		this.expressionNodes = expressionNodes;
		count = expressionNodes.size();
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineExpressions(this);
		}
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.foldConstants(this);
		}
		
		for (int i = 0; i < count; ++i) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNodes.get(i).constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNodes.set(i, constantExpressionNode);
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.generateIntermediate(this);
		}
		
		routine.addCompoundAssignmentAction(this, dataId = routine.nextRegId(typeInfo), Helpers.map(expressionNodes, x -> x.dataId));
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = new TupleTypeInfo(this, new ArrayList<>(), Helpers.map(expressionNodes, ExpressionNode::getTypeInfo));
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (count == 0) {
			constantValue = Main.generator.unitValue;
			return;
		}
		
		List<Value> values = new ArrayList<>();
		for (ExpressionNode expressionNode : expressionNodes) {
			@Nullable Value elementConstantValue = expressionNode.getConstantValue();
			if (elementConstantValue == null) {
				return;
			}
			values.add(elementConstantValue);
		}
		constantValue = new TupleValue(this, typeInfo, values);
	}
}
