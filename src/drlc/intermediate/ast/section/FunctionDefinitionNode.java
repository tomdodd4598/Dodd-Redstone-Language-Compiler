package drlc.intermediate.ast.section;

import java.util.List;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.*;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.FunctionRoutine;
import drlc.intermediate.scope.FunctionScope;
import drlc.node.Node;

public class FunctionDefinitionNode extends StaticSectionNode<FunctionScope, FunctionRoutine> {
	
	public final @NonNull String name;
	public final @NonNull List<DeclaratorNode> parameterNodes;
	public final @Nullable TypeNode returnTypeNode;
	public final @NonNull ScopedBodyNode bodyNode;
	
	public FunctionDefinitionNode(Node[] parseNodes, @NonNull String name, @NonNull List<DeclaratorNode> parameterNodes, @Nullable TypeNode returnTypeNode, @NonNull ScopedBodyNode bodyNode) {
		super(parseNodes);
		this.name = name;
		this.parameterNodes = parameterNodes;
		this.returnTypeNode = returnTypeNode;
		this.bodyNode = bodyNode;
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.functionParameter = true;
			if (parameterNode.typeNode == null) {
				throw error("Function parameter types must be explicitly defined!");
			}
		}
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = new FunctionScope(parent.scope);
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.setScopes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.setScopes(this);
		}
		bodyNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.defineTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.defineTypes(this);
		}
		bodyNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.declareExpressions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.declareExpressions(this);
		}
		
		@NonNull TypeInfo returnType = returnTypeNode != null ? returnTypeNode.typeInfo : Main.generator.voidTypeInfo;
		
		scope.function = new Function(this, name, false, returnType, Helpers.map(parameterNodes, x -> x.declaratorInfo));
		scope.parent.addFunction(this, scope.function, false);
		
		routine = new FunctionRoutine(this, scope.function);
		scope.parent.addRoutine(this, routine);
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.routine = routine;
		}
		if (returnTypeNode != null) {
			returnTypeNode.routine = routine;
		}
		
		bodyNode.declareExpressions(this);
		
		if (!returnType.equals(Main.generator.voidTypeInfo) && !scope.hasDefiniteReturn()) {
			throw error("Function \"%s\" does not always return value of expected type \"%s\"!", name, returnType);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.defineExpressions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.defineExpressions(this);
		}
		bodyNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.checkTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.checkTypes(this);
		}
		bodyNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.foldConstants(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.foldConstants(this);
		}
		bodyNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.trackFunctions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.trackFunctions(this);
		}
		bodyNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.generateIntermediate(this);
		}
		bodyNode.generateIntermediate(this);
	}
}
