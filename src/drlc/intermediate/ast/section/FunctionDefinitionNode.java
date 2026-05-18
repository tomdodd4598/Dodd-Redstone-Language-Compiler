package drlc.intermediate.ast.section;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.ast.pattern.*;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.FunctionScope;

public class FunctionDefinitionNode extends StaticSectionNode<FunctionScope> {
	
	public final @NonNull String name;
	public final @NonNull List<DeclaratorNode> parameterNodes;
	protected final @NonNull List<ParameterPattern> parameterPatterns = new ArrayList<>();
	public final @Nullable TypeNode returnTypeNode;
	public final @NonNull ScopedBodyNode bodyNode;
	public final boolean closure;
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	public FunctionDefinitionNode(Source source, @NonNull String name, @NonNull List<PatternNode> parameterPatternNodes, @Nullable TypeNode returnTypeNode, @NonNull ScopedBodyNode bodyNode, boolean closure) {
		super(source);
		this.name = name;
		this.parameterNodes = new ArrayList<>();
		this.returnTypeNode = returnTypeNode;
		this.bodyNode = bodyNode;
		this.closure = closure;
		
		Set<String> names = new HashSet<>();
		int hiddenCount = 0;
		for (PatternNode patternNode : parameterPatternNodes) {
			patternNode.checkDeclaratorNames();
			for (DeclaratorNode declaratorNode : patternNode.getDeclaratorNodes()) {
				if (!names.add(declaratorNode.name)) {
					throw Helpers.nodeError(declaratorNode, "Repeated parameter name \"%s\"!", declaratorNode.name);
				}
				if (declaratorNode.variableModifier._static) {
					throw Helpers.nodeError(declaratorNode, "Function parameters can not be static!");
				}
			}
			
			DeclaratorNode parameterNode = directParameterNode(patternNode);
			if (parameterNode == null) {
				parameterNode = new DeclaratorNode(patternNode.source, VariableModifier.DEFAULT, Global.PARAM + hiddenCount++, null);
				parameterPatterns.add(new ParameterPattern(parameterNode, patternNode));
			}
			parameterNodes.add(parameterNode);
			parameterNode.functionParameter = true;
		}
	}
	
	protected @Nullable DeclaratorNode directParameterNode(@NonNull PatternNode patternNode) {
		if (patternNode instanceof TypedPatternNode typedPatternNode && typedPatternNode.patternNode instanceof BindingPatternNode bindingPatternNode) {
			DeclaratorNode declaratorNode = bindingPatternNode.declaratorNode;
			return new DeclaratorNode(declaratorNode.source, declaratorNode.variableModifier, declaratorNode.name, typedPatternNode.typeNode);
		}
		else {
			return null;
		}
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new FunctionScope(this, parent.scope);
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.setScopes(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.patternNode.setScopes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.setScopes(this);
		}
		bodyNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.defineTypes(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.patternNode.defineTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.defineTypes(this);
		}
		bodyNode.defineTypes(this);
	}
	
	@SuppressWarnings("unused")
	@Override
	public void declareFunctions(ASTNode<?> parent) {
		if (function != null) {
			return;
		}
		
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.defineParameterType();
		}
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.declareFunctionParameter();
		}
		
		@NonNull TypeInfo returnType = returnTypeNode != null ? returnTypeNode.getTypeInfo() : Main.generator.unitTypeInfo;
		
		function = scope.function = new Function(this, name, false, returnType, Helpers.map(parameterNodes, x -> x.declaratorInfo), closure, scope.parent.isModule);
		function.inferReturnType = closure && returnTypeNode == null;
		function.functionScope = scope;
		scope.parent.addFunction(this, function);
		
		routine = new Routine(function);
		Main.rootScope.addRoutine(this, routine);
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.routine = routine;
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.patternNode.routine = routine;
		}
		if (returnTypeNode != null) {
			returnTypeNode.routine = routine;
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		declareFunctions(parent);
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.declareExpressions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.declareExpressions(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.patternNode.declareExpressions(this);
		}
		
		bodyNode.declareExpressions(this);
		
		@NonNull TypeInfo returnType = returnTypeNode != null ? returnTypeNode.getTypeInfo() : Main.generator.unitTypeInfo;
		
		if (!returnType.equals(Main.generator.unitTypeInfo) && !scope.hasDefiniteReturn()) {
			throw error("Function \"%s\" does not always return value of expected type \"%s\"!", name, returnType);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		function.defined = true;
		
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.defineExpressions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.defineExpressions(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.defineExpressions();
		}
		bodyNode.defineExpressions(this);
		
		@NonNull TypeInfo returnType = routine.getReturnTypeInfo();
		if (!returnType.equals(Main.generator.unitTypeInfo) && !scope.hasDefiniteReturn()) {
			throw error("Function \"%s\" does not always return value of expected type \"%s\"!", name, returnType);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.checkTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.checkTypes(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.patternNode.checkTypes(this);
		}
		bodyNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.foldConstants(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.foldConstants(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.patternNode.foldConstants(this);
		}
		bodyNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		for (DeclaratorNode parameterNode : parameterNodes) {
			parameterNode.generateIntermediate(this);
		}
		for (ParameterPattern parameterPattern : parameterPatterns) {
			parameterPattern.generateIntermediate();
		}
		bodyNode.generateIntermediate(this);
	}
	
	protected final class ParameterPattern {
		
		public final @NonNull DeclaratorNode parameterNode;
		public final @NonNull PatternNode patternNode;
		
		protected ParameterPattern(@NonNull DeclaratorNode parameterNode, @NonNull PatternNode patternNode) {
			this.parameterNode = parameterNode;
			this.patternNode = patternNode;
		}
		
		protected void defineParameterType() {
			TypeInfo typeInfo = patternNode.getExplicitTypeInfo();
			if (typeInfo == null) {
				throw Helpers.nodeError(patternNode, "Function parameter pattern types must be explicitly defined!");
			}
			parameterNode.inferredTypeInfo = typeInfo;
		}
		
		protected void defineExpressions() {
			patternNode.setTypeInfo(parameterNode.declaratorInfo.getTypeInfo());
			patternNode.defineExpressions(FunctionDefinitionNode.this);
			for (DeclaratorNode declaratorNode : patternNode.getDeclaratorNodes()) {
				scope.onVariableInitialization(FunctionDefinitionNode.this, declaratorNode.declaratorInfo.variable);
			}
		}
		
		protected void generateIntermediate() {
			patternNode.dataId = parameterNode.declaratorInfo.dataId();
			patternNode.generateIntermediate(FunctionDefinitionNode.this);
		}
	}
}
