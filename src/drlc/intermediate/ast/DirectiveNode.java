package drlc.intermediate.ast;

import java.util.List;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.expression.ExpressionNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class DirectiveNode extends ASTNode<Scope, Routine> {
	
	public final @NonNull String name;
	public final @NonNull List<ExpressionNode> constantExpressionNodes;
	
	public DirectiveNode(Node[] parseNodes, @NonNull String name, @NonNull List<ExpressionNode> constantExpressionNodes) {
		super(parseNodes);
		this.name = name;
		this.constantExpressionNodes = constantExpressionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		for (ExpressionNode constantExpressionNode : constantExpressionNodes) {
			constantExpressionNode.setScopes(this);
		}
		
		Directive directive = Main.generator.directiveMap.get(name);
		if (directive == null) {
			// throw error("Encountered undefined directive \"%s\"!", name);
			return;
		}
		
		DeclaratorInfo[] params = directive.params;
		int constantExpressionCount = constantExpressionNodes.size();
		if (params.length != constantExpressionCount) {
			throw error("Directive \"%s\" requires %d arguments but received %d!", name, params.length, constantExpressionCount);
		}
		
		@NonNull Value[] values = new @NonNull Value[constantExpressionCount];
		for (int i = 0; i < constantExpressionCount; ++i) {
			@Nullable Value constantValue = constantExpressionNodes.get(i).getConstantValue();
			@NonNull TypeInfo paramType = params[i].getTypeInfo();
			if (constantValue != null && constantValue.typeInfo.canImplicitCastTo(paramType)) {
				values[i] = constantValue;
			}
			else {
				throw error("Argument %d of directive \"%s\" is not a compile-time \"%s\" constant!", i, name, paramType);
			}
		}
		
		directive.run(values);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		
	}
}
