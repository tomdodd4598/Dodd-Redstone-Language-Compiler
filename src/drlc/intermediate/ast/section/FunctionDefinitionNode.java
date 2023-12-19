package drlc.intermediate.ast.section;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.*;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.FunctionRoutine;
import drlc.intermediate.scope.*;
import drlc.node.Node;

public class FunctionDefinitionNode extends ProgramSectionNode<Scope, FunctionRoutine> {
	
	public final @NonNull String name;
	public final @NonNull List<ParameterNode> paramNodes;
	public final @Nullable TypeNode returnTypeNode;
	public final @NonNull ScopeContentsNode bodyNode;
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	public FunctionDefinitionNode(Node[] parseNodes, @NonNull String name, @NonNull List<ParameterNode> paramNodes, @Nullable TypeNode returnTypeNode, @NonNull ScopeContentsNode bodyNode) {
		super(parseNodes);
		this.name = name;
		this.paramNodes = paramNodes;
		this.returnTypeNode = returnTypeNode;
		this.bodyNode = bodyNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = new StandardScope(parent.scope);
		
		for (int i = 0; i < paramNodes.size(); ++i) {
			ParameterNode paramNode = paramNodes.get(i);
			paramNode.setScopes(this);
			paramNode.index = i;
		}
		if (returnTypeNode != null) {
			returnTypeNode.setScopes(this);
		}
		bodyNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (ParameterNode paramNode : paramNodes) {
			paramNode.defineTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.defineTypes(this);
		}
		bodyNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		for (ParameterNode paramNode : paramNodes) {
			paramNode.declareExpressions(this);
			scope.addVariable(this, paramNode.declaratorInfo.variable, false);
		}
		if (returnTypeNode != null) {
			returnTypeNode.declareExpressions(this);
		}
		
		@NonNull TypeInfo returnType = returnTypeNode != null ? returnTypeNode.typeInfo : Main.generator.voidTypeInfo;
		
		function = new Function(this, name, false, returnType, paramNodes.stream().map(x -> x.declaratorInfo).collect(Collectors.toList()));
		scope.parent.addFunction(this, function, false);
		
		routine = new FunctionRoutine(this, function);
		Main.program.routineMap.put(name, routine);
		
		bodyNode.declareExpressions(this);
		
		if (!returnType.isVoid() && !scope.checkCompleteReturn()) {
			throw error("Function \"%s\" does not always return value of expected type \"%s\"!", name, returnType);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		for (ParameterNode paramNode : paramNodes) {
			paramNode.checkTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.checkTypes(this);
		}
		bodyNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		for (ParameterNode paramNode : paramNodes) {
			paramNode.foldConstants(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.foldConstants(this);
		}
		bodyNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		for (ParameterNode paramNode : paramNodes) {
			paramNode.trackFunctions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.trackFunctions(this);
		}
		bodyNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		for (ParameterNode paramNode : paramNodes) {
			paramNode.generateIntermediate(this);
		}
		bodyNode.generateIntermediate(this);
	}
}
