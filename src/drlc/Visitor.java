package drlc;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Helpers.Pair;
import drlc.analysis.AnalysisAdapter;
import drlc.intermediate.ast.*;
import drlc.intermediate.ast.element.*;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.ast.section.*;
import drlc.intermediate.ast.stop.*;
import drlc.intermediate.ast.type.*;
import drlc.intermediate.component.*;
import drlc.node.*;

public class Visitor extends AnalysisAdapter {
	
	public final String fileName;
	public final String contents;
	
	public StartNode ast;
	
	public final Deque<ModuleNode> moduleStack = new ArrayDeque<>();
	
	public final Deque<StaticSectionNode<?>> staticSectionStack = new ArrayDeque<>();
	public final Deque<RuntimeSectionNode<?>> runtimeSectionStack = new ArrayDeque<>();
	
	public final Deque<ConditionalSectionNode> conditionalSectionStack = new ArrayDeque<>();
	public final Deque<ASTNode<?>> conditionalEndStack = new ArrayDeque<>();
	
	public final Deque<UseTreeNode> useTreeStack = new ArrayDeque<>();
	
	public final Deque<ScopedBodyNode> scopedBodyNodeStack = new ArrayDeque<>();
	
	public final Deque<StopNode> stopStack = new ArrayDeque<>();
	
	public final Deque<TypeNode> typeStack = new ArrayDeque<>();
	
	public final Deque<DeclaratorNode> declaratorStack = new ArrayDeque<>();
	
	public final Deque<ExpressionNode> expressionStack = new ArrayDeque<>();
	
	public final Deque<Pair<String, ExpressionNode>> labelledExpressionPairStack = new ArrayDeque<>();
	
	public Visitor(String fileName, String contents) {
		this.fileName = fileName;
		this.contents = contents;
	}
	
	private Source source(Node... parseNodes) {
		return new Source(fileName, contents, parseNodes);
	}
	
	@SuppressWarnings("null")
	private <T> @NonNull T traverse(Node node, Deque<T> stack) {
		node.apply(this);
		return stack.pop();
	}
	
	private <T> @Nullable T traverseNullable(Node node, Deque<T> stack) {
		return node == null ? null : traverse(node, stack);
	}
	
	private @NonNull ScopedBodyNode scope(Node node) {
		return traverse(node, scopedBodyNodeStack);
	}
	
	private @NonNull TypeNode type(PType node) {
		return traverse(node, typeStack);
	}
	
	private @Nullable TypeNode typeAnnotation(PTypeAnnotation node) {
		return traverseNullable(node, typeStack);
	}
	
	private @Nullable TypeNode returnType(PReturnType node) {
		return traverseNullable(node, typeStack);
	}
	
	private @NonNull DeclaratorNode declarator(Node node) {
		return traverse(node, declaratorStack);
	}
	
	private @NonNull ExpressionNode expression(Node node) {
		return traverse(node, expressionStack);
	}
	
	private @NonNull ExpressionNode binaryExpression(Node node, Node left, Node op, Node right) {
		return new BinaryExpressionNode(source(node), expression(left), BinaryOpType.get(trim(op)), expression(right));
	}
	
	private <T extends Node, V> @NonNull List<V> traverseList(List<T> node, Deque<V> stack) {
		List<V> out = new ArrayList<>();
		for (T e : node) {
			out.add(traverse(e, stack));
		}
		return out;
	}
	
	private <T extends Node, U extends Node, V> @NonNull List<V> traverseList(T head, List<U> tail, Deque<V> stack) {
		List<V> out = new ArrayList<>();
		out.add(traverse(head, stack));
		for (U e : tail) {
			out.add(traverse(e, stack));
		}
		return out;
	}
	
	private @NonNull List<TypeNode> tupleTypeList(PTupleTypeList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			ATupleTypeList tupleTypeList = (ATupleTypeList) node;
			List<TypeNode> out = traverseList(tupleTypeList.getTupleTypeListHead(), typeStack);
			PType tail = tupleTypeList.getType();
			if (tail != null) {
				out.add(type(tail));
			}
			return out;
		}
	}
	
	private @NonNull List<TypeNode> typeList(PTypeList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			ATypeList typeList = (ATypeList) node;
			return traverseList(typeList.getType(), typeList.getTypeListTail(), typeStack);
		}
	}
	
	private @Nullable TypeNode closureReturnType(PClosureBody node) {
		return node instanceof AExpressionClosureBody ? null : returnType(((ABlockClosureBody) node).getReturnType());
	}
	
	private @NonNull Pair<@NonNull ScopedBodyNode, @Nullable ReturnNode> closureBodyPair(PClosureBody node) {
		if (node instanceof AExpressionClosureBody) {
			return expressionClosureBodyPair(((AExpressionClosureBody) node).getExpression());
		}
		else {
			return new Pair<>(scope(((ABlockClosureBody) node).getScopedBody()), null);
		}
	}
	
	private @NonNull Pair<@NonNull ScopedBodyNode, @Nullable ReturnNode> expressionClosureBodyPair(Node node) {
		ReturnNode returnNode = new ReturnNode(source(node), expression(node));
		return new Pair<>(new ScopedBodyNode(source(node), new ArrayList<>(), returnNode), returnNode);
	}
	
	private @NonNull List<DeclaratorNode> closureDeclaratorList(PClosureDeclaratorList node) {
		if (node instanceof AStandardClosureDeclaratorList) {
			return declaratorList(((AStandardClosureDeclaratorList) node).getDeclaratorList());
		}
		else {
			return new ArrayList<>();
		}
	}
	
	private @NonNull List<DeclaratorNode> declaratorList(PDeclaratorList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			ADeclaratorList declaratorList = (ADeclaratorList) node;
			return traverseList(declaratorList.getDeclarator(), declaratorList.getDeclaratorListTail(), declaratorStack);
		}
	}
	
	private @NonNull List<ExpressionNode> expressionList(PExpressionList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			AExpressionList expressionList = (AExpressionList) node;
			return traverseList(expressionList.getExpression(), expressionList.getExpressionListTail(), expressionStack);
		}
	}
	
	private @NonNull List<ExpressionNode> tupleExpressionList(PTupleExpressionList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			ATupleExpressionList tupleExpressionList = (ATupleExpressionList) node;
			List<ExpressionNode> out = traverseList(tupleExpressionList.getTupleExpressionListHead(), expressionStack);
			PExpression tail = tupleExpressionList.getExpression();
			if (tail != null) {
				out.add(expression(tail));
			}
			return out;
		}
	}
	
	private @NonNull List<UseTreeNode> useTreeList(PUseTreeList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			AUseTreeList useTreeList = (AUseTreeList) node;
			return traverseList(useTreeList.getUseTree(), useTreeList.getUseTreeListTail(), useTreeStack);
		}
	}
	
	private @NonNull Pair<List<String>, @NonNull List<ExpressionNode>> structExpressionListPair(PStructExpressionList node) {
		if (node == null) {
			return new Pair<>(new ArrayList<>(), new ArrayList<>());
		}
		else if (node instanceof ABasicStructExpressionList) {
			return new Pair<>(null, expressionList(((ABasicStructExpressionList) node).getExpressionList()));
		}
		else {
			return labelledExpressionListPair(((ALabelledStructExpressionList) node).getLabelledExpressionList());
		}
	}
	
	private @NonNull Pair<List<String>, @NonNull List<ExpressionNode>> labelledExpressionListPair(PLabelledExpressionList node) {
		Pair<@NonNull List<String>, @NonNull List<ExpressionNode>> listPair = new Pair<>(new ArrayList<>(), new ArrayList<>());
		if (node != null) {
			ALabelledExpressionList labelledExpressionList = (ALabelledExpressionList) node;
			List<Pair<String, ExpressionNode>> pairList = traverseList(labelledExpressionList.getLabelledExpression(), labelledExpressionList.getLabelledExpressionListTail(), labelledExpressionPairStack);
			for (Pair<String, ExpressionNode> pair : pairList) {
				listPair.left.add(pair.left);
				listPair.right.add(pair.right);
			}
		}
		return listPair;
	}
	
	private @NonNull VariableModifier variableModifier(Node node, List<PVariableModifier> variableModifiers) {
		boolean _static = false, mut = false;
		for (PVariableModifier variableModifier : variableModifiers) {
			String str = trim(variableModifier);
			if (str.equals(Global.STATIC)) {
				if (_static) {
					throw Helpers.sourceError(source(node), "Repeated modifier \"%s\" in variable declarator!", Global.STATIC);
				}
				_static = true;
			}
			else if (str.equals(Global.MUT)) {
				if (mut) {
					throw Helpers.sourceError(source(node), "Repeated modifier \"%s\" in variable declarator!", Global.MUT);
				}
				mut = true;
			}
		}
		return new VariableModifier(_static, mut);
	}
	
	@SuppressWarnings("null")
	private @NonNull String text(Token token) {
		return token.getText();
	}
	
	private @Nullable String textNullable(Token token) {
		return token == null ? null : token.getText();
	}
	
	@SuppressWarnings("null")
	private @NonNull List<String> pathPrefix(List<PPathPrefix> pathPrefix) {
		return Helpers.map(pathPrefix, x -> trim(((APathPrefix) x).getPathSegment()));
	}
	
	private @Nullable String useAlias(PUseAlias useAlias) {
		return useAlias == null ? null : text(((AUseAlias) useAlias).getName());
	}
	
	private @NonNull String label(PLabel label) {
		return text(((ALabel) label).getName());
	}
	
	private @Nullable String labelNullable(PLabel label) {
		return label == null ? null : text(((ALabel) label).getName());
	}
	
	private @NonNull List<String> path(PPath node) {
		@NonNull List<String> out = new ArrayList<>();
		APath path = (APath) node;
		out.add(trim(path.getPathSegment()));
		List<PPathTail> tailList = path.getPathTail();
		for (PPathTail tail : tailList) {
			out.add(trim(((APathTail) tail).getPathSegment()));
		}
		return out;
	}
	
	private boolean unless(Token token) {
		return text(token).equals(Global.UNLESS);
	}
	
	private boolean until(Token token) {
		return text(token).equals(Global.UNTIL);
	}
	
	@SuppressWarnings("null")
	private <T> @NonNull String trim(T node) {
		return node.toString().trim();
	}
	
	@Override
	public void defaultCase(Node node) {
		throw Helpers.sourceError(source(node), "%s parse tree node not supported!", node.getClass().getSimpleName());
	}
	
	@Override
	public void caseStart(Start node) {
		ast = new StartNode(source(node), traverse(node.getPModule(), moduleStack));
	}
	
	@Override
	public void caseAModule(AModule node) {
		moduleStack.push(new ModuleNode(source(node), traverseList(node.getStaticSection(), staticSectionStack)));
	}
	
	@Override
	public void caseAModuleDeclarationStaticSection(AModuleDeclarationStaticSection node) {
		node.getModuleDeclaration().apply(this);
	}
	
	@Override
	public void caseAModuleDefinitionStaticSection(AModuleDefinitionStaticSection node) {
		node.getModuleDefinition().apply(this);
	}
	
	@Override
	public void caseAUseDeclarationStaticSection(AUseDeclarationStaticSection node) {
		node.getUseDeclaration().apply(this);
	}
	
	@Override
	public void caseAFunctionDefinitionStaticSection(AFunctionDefinitionStaticSection node) {
		node.getFunctionDefinition().apply(this);
	}
	
	@Override
	public void caseATypealiasDefinitionStaticSection(ATypealiasDefinitionStaticSection node) {
		node.getTypealiasDefinition().apply(this);
	}
	
	@Override
	public void caseAStructDefinitionStaticSection(AStructDefinitionStaticSection node) {
		node.getStructDefinition().apply(this);
	}
	
	@Override
	public void caseAConstantDefinitionStaticSection(AConstantDefinitionStaticSection node) {
		node.getConstantDefinition().apply(this);
	}
	
	@Override
	public void caseAVariableDeclarationStaticSection(AVariableDeclarationStaticSection node) {
		node.getVariableDeclaration().apply(this);
	}
	
	@Override
	public void caseAEmptySectionStaticSection(AEmptySectionStaticSection node) {
		node.getEmptySection().apply(this);
	}
	
	@Override
	public void caseAStaticSectionRuntimeSection(AStaticSectionRuntimeSection node) {
		runtimeSectionStack.push(traverse(node.getStaticSection(), staticSectionStack));
	}
	
	@Override
	public void caseAScopedSectionRuntimeSection(AScopedSectionRuntimeSection node) {
		node.getScopedSection().apply(this);
	}
	
	@Override
	public void caseAExpressionStatementRuntimeSection(AExpressionStatementRuntimeSection node) {
		node.getExpressionStatement().apply(this);
	}
	
	@Override
	public void caseAConditionalSectionRuntimeSection(AConditionalSectionRuntimeSection node) {
		runtimeSectionStack.push(traverse(node.getConditionalSection(), conditionalSectionStack));
	}
	
	@Override
	public void caseAIterativeSectionRuntimeSection(AIterativeSectionRuntimeSection node) {
		node.getIterativeSection().apply(this);
	}
	
	/* SECTION SPECIFICATIONS */
	
	@Override
	public void caseAModuleDeclaration(AModuleDeclaration node) {
		staticSectionStack.push(new ModuleDeclarationNode(source(node), text(node.getName())));
	}
	
	@Override
	public void caseAModuleDefinition(AModuleDefinition node) {
		staticSectionStack.push(new ModuleDefinitionNode(source(node), text(node.getName()), traverse(node.getModule(), moduleStack)));
	}
	
	@Override
	public void caseAUseDeclaration(AUseDeclaration node) {
		staticSectionStack.push(new UseDeclarationNode(source(node), traverse(node.getUseTree(), useTreeStack)));
	}
	
	@Override
	public void caseANestedUseTree(ANestedUseTree node) {
		useTreeStack.push(new NestedUseTreeNode(source(node), pathPrefix(node.getPathPrefix()), useTreeList(node.getUseTreeList())));
	}
	
	@Override
	public void caseAWildcardUseTree(AWildcardUseTree node) {
		useTreeStack.push(new WildcardUseTreeNode(source(node), pathPrefix(node.getPathPrefix())));
	}
	
	@Override
	public void caseALeafUseTree(ALeafUseTree node) {
		List<String> pathSuffix = pathPrefix(node.getPathPrefix());
		pathSuffix.add(trim(node.getPathSegment()));
		useTreeStack.push(new LeafUseTreeNode(source(node), pathSuffix, useAlias(node.getUseAlias())));
	}
	
	@Override
	public void caseAUseTreeList(AUseTreeList node) {
		node.getUseTree().apply(this);
	}
	
	@Override
	public void caseAUseTreeListTail(AUseTreeListTail node) {
		node.getUseTree().apply(this);
	}
	
	@Override
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		staticSectionStack.push(new FunctionDefinitionNode(source(node), text(node.getName()), declaratorList(node.getDeclaratorList()), returnType(node.getReturnType()), scope(node.getScopedBody()), false));
	}
	
	@Override
	public void caseATypealiasDefinition(ATypealiasDefinition node) {
		staticSectionStack.push(new TypealiasDefinitionNode(source(node), text(node.getName()), type(node.getType())));
	}
	
	@Override
	public void caseAStructDefinition(AStructDefinition node) {
		staticSectionStack.push(new StructDefinitionNode(source(node), text(node.getName()), declaratorList(node.getDeclaratorList())));
	}
	
	@Override
	public void caseAConstantDefinition(AConstantDefinition node) {
		staticSectionStack.push(new ConstantDefinitionNode(source(node), text(node.getName()), typeAnnotation(node.getTypeAnnotation()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {
		staticSectionStack.push(new VariableDeclarationNode(source(node), declarator(node.getDeclarator()), null));
	}
	
	@Override
	public void caseAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {
		staticSectionStack.push(new VariableDeclarationNode(source(node), declarator(node.getDeclarator()), expression(node.getExpression())));
	}
	
	@Override
	public void caseAEmptySection(AEmptySection node) {
		staticSectionStack.push(new EmptySectionNode(source(node)));
	}
	
	@Override
	public void caseAScopedSection(AScopedSection node) {
		runtimeSectionStack.push(scope(node.getScopedBody()));
	}
	
	@Override
	public void caseAExpressionStatement(AExpressionStatement node) {
		runtimeSectionStack.push(new ExpressionStatementNode(source(node), expression(node.getExpression())));
	}
	
	@Override
	public void caseAConditionalSection(AConditionalSection node) {
		conditionalSectionStack.push(new ConditionalSectionNode(source(node), unless(node.getConditionalBranchKeyword()), expression(node.getBraceExpression()), scope(node.getScopedBody()), traverseNullable(node.getElseSection(), conditionalEndStack)));
	}
	
	@Override
	public void caseAExcludingBranchElseSection(AExcludingBranchElseSection node) {
		conditionalEndStack.push(scope(node.getScopedBody()));
	}
	
	@Override
	public void caseAIncludingBranchElseSection(AIncludingBranchElseSection node) {
		conditionalEndStack.push(traverse(node.getConditionalSection(), conditionalSectionStack));
	}
	
	@Override
	public void caseALoopIterativeSection(ALoopIterativeSection node) {
		runtimeSectionStack.push(new LoopIterativeSectionNode(source(node), labelNullable(node.getLabel()), scope(node.getScopedBody())));
	}
	
	@Override
	public void caseAConditionalIterativeSection(AConditionalIterativeSection node) {
		runtimeSectionStack.push(new ConditionalIterativeSectionNode(source(node), labelNullable(node.getLabel()), false, until(node.getConditionalIterativeKeyword()), expression(node.getBraceExpression()), scope(node.getScopedBody())));
	}
	
	@Override
	public void caseADoConditionalIterativeSection(ADoConditionalIterativeSection node) {
		runtimeSectionStack.push(new ConditionalIterativeSectionNode(source(node), labelNullable(node.getLabel()), true, until(node.getConditionalIterativeKeyword()), expression(node.getExpression()), scope(node.getScopedBody())));
	}
	
	@Override
	public void caseAScopedBody(AScopedBody node) {
		scopedBodyNodeStack.push(new ScopedBodyNode(source(node), traverseList(node.getRuntimeSection(), runtimeSectionStack), traverseNullable(node.getStopStatement(), stopStack)));
	}
	
	@Override
	public void caseAExitStopStatement(AExitStopStatement node) {
		stopStack.push(new ExitNode(source(node), traverseNullable(node.getExpression(), expressionStack)));
	}
	
	@Override
	public void caseAReturnStopStatement(AReturnStopStatement node) {
		stopStack.push(new ReturnNode(source(node), traverseNullable(node.getExpression(), expressionStack)));
	}
	
	@Override
	public void caseAContinueStopStatement(AContinueStopStatement node) {
		stopStack.push(new ContinueNode(source(node), textNullable(node.getName())));
	}
	
	@Override
	public void caseABreakStopStatement(ABreakStopStatement node) {
		stopStack.push(new BreakNode(source(node), textNullable(node.getName())));
	}
	
	@Override
	public void caseADead0DeadSection(ADead0DeadSection node) {
		
	}
	
	@Override
	public void caseADead1DeadSection(ADead1DeadSection node) {
		
	}
	
	@Override
	public void caseADead2DeadSection(ADead2DeadSection node) {
		
	}
	
	@Override
	public void caseADead3DeadSection(ADead3DeadSection node) {
		
	}
	
	@Override
	public void caseADead4DeadSection(ADead4DeadSection node) {
		
	}
	
	/* COMPONENT SPECIFICATIONS */
	
	@Override
	public void caseARawType(ARawType node) {
		node.getRawType().apply(this);
	}
	
	@Override
	public void caseAAddressOfType(AAddressOfType node) {
		typeStack.push(new AddressTypeNode(source(node), node.getMut() != null, type(node.getType())));
	}
	
	@Override
	public void caseADoubleAddressOfType(ADoubleAddressOfType node) {
		typeStack.push(new AddressTypeNode(source(node), false, new AddressTypeNode(source(node), node.getMut() != null, type(node.getType()))));
	}
	
	@Override
	public void caseANominalRawType(ANominalRawType node) {
		typeStack.push(new NominalTypeNode(source(node), path(node.getPath())));
	}
	
	@Override
	public void caseAArrayRawType(AArrayRawType node) {
		typeStack.push(new ArrayTypeNode(source(node), type(node.getType()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseATupleRawType(ATupleRawType node) {
		typeStack.push(new TupleTypeNode(source(node), tupleTypeList(node.getTupleTypeList())));
	}
	
	@Override
	public void caseAFunctionRawType(AFunctionRawType node) {
		typeStack.push(new FunctionTypeNode(source(node), typeList(node.getTypeList()), returnType(node.getReturnType())));
	}
	
	@Override
	public void caseATupleTypeList(ATupleTypeList node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseATupleTypeListHead(ATupleTypeListHead node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseATypeList(ATypeList node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseATypeListTail(ATypeListTail node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseAReturnType(AReturnType node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseATypeAnnotation(ATypeAnnotation node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseADeclarator(ADeclarator node) {
		declaratorStack.push(new DeclaratorNode(source(node), variableModifier(node, node.getVariableModifier()), text(node.getName()), typeAnnotation(node.getTypeAnnotation())));
	}
	
	@Override
	public void caseADeclaratorList(ADeclaratorList node) {
		node.getDeclarator().apply(this);
	}
	
	@Override
	public void caseADeclaratorListTail(ADeclaratorListTail node) {
		node.getDeclarator().apply(this);
	}
	
	@Override
	public void caseAPrioritizedExpression(APrioritizedExpression node) {
		node.getAssignmentExpression().apply(this);
	}
	
	@Override
	public void caseAClosureExpression(AClosureExpression node) {
		long id = Main.rootScope.nextLocalId();
		@NonNull Pair<@NonNull ScopedBodyNode, @Nullable ReturnNode> closureBodyPair = closureBodyPair(node.getClosureBody());
		@NonNull FunctionDefinitionNode functionNode = new FunctionDefinitionNode(source(node), "\\fn" + id, closureDeclaratorList(node.getClosureDeclaratorList()), closureReturnType(node.getClosureBody()), closureBodyPair.left, true);
		if (closureBodyPair.right != null) {
			closureBodyPair.right.closureDefinition = functionNode;
		}
		expressionStack.push(new ClosureExpressionNode(source(node), "\\Closure" + id, functionNode));
	}
	
	@Override
	public void caseAPrioritizedAssignmentExpression(APrioritizedAssignmentExpression node) {
		node.getTernaryExpression().apply(this);
	}
	
	@Override
	public void caseAAssignmentAssignmentExpression(AAssignmentAssignmentExpression node) {
		expressionStack.push(new AssignmentExpressionNode(source(node), expression(node.getUnaryExpression()), AssignmentOpType.get(trim(node.getAssignmentOp())), expression(node.getExpression())));
	}
	
	@Override
	public void caseAPrioritizedTernaryExpression(APrioritizedTernaryExpression node) {
		node.getCastExpression().apply(this);
	}
	
	@Override
	public void caseATernaryTernaryExpression(ATernaryTernaryExpression node) {
		expressionStack.push(new TernaryExpressionNode(source(node), expression(node.getCastExpression()), expression(node.getExpression()), expression(node.getTernaryExpression())));
	}
	
	@Override
	public void caseAPrioritizedCastExpression(APrioritizedCastExpression node) {
		node.getLogicalExpression().apply(this);
	}
	
	@Override
	public void caseACastCastExpression(ACastCastExpression node) {
		expressionStack.push(new CastExpressionNode(source(node), expression(node.getUnaryExpression()), type(node.getType())));
	}
	
	@Override
	public void caseAPrioritizedLogicalExpression(APrioritizedLogicalExpression node) {
		node.getEqualityExpression().apply(this);
	}
	
	@Override
	public void caseABinaryLogicalExpression(ABinaryLogicalExpression node) {
		expressionStack.push(binaryExpression(node, node.getLogicalExpression(), node.getLogicalBinaryOp(), node.getEqualityExpression()));
	}
	
	@Override
	public void caseAPrioritizedEqualityExpression(APrioritizedEqualityExpression node) {
		node.getComparativeExpression().apply(this);
	}
	
	@Override
	public void caseABinaryEqualityExpression(ABinaryEqualityExpression node) {
		expressionStack.push(binaryExpression(node, node.getEqualityExpression(), node.getEqualityBinaryOp(), node.getComparativeExpression()));
	}
	
	@Override
	public void caseAPrioritizedComparativeExpression(APrioritizedComparativeExpression node) {
		node.getAdditiveExpression().apply(this);
	}
	
	@Override
	public void caseABinaryComparativeExpression(ABinaryComparativeExpression node) {
		expressionStack.push(binaryExpression(node, node.getComparativeExpression(), node.getComparativeBinaryOp(), node.getAdditiveExpression()));
	}
	
	@Override
	public void caseAPrioritizedAdditiveExpression(APrioritizedAdditiveExpression node) {
		node.getMultiplicativeExpression().apply(this);
	}
	
	@Override
	public void caseABinaryAdditiveExpression(ABinaryAdditiveExpression node) {
		expressionStack.push(binaryExpression(node, node.getAdditiveExpression(), node.getAdditiveBinaryOp(), node.getMultiplicativeExpression()));
	}
	
	@Override
	public void caseAPrioritizedMultiplicativeExpression(APrioritizedMultiplicativeExpression node) {
		node.getShiftExpression().apply(this);
	}
	
	@Override
	public void caseABinaryMultiplicativeExpression(ABinaryMultiplicativeExpression node) {
		expressionStack.push(binaryExpression(node, node.getMultiplicativeExpression(), node.getMultiplicativeBinaryOp(), node.getShiftExpression()));
	}
	
	@Override
	public void caseAPrioritizedShiftExpression(APrioritizedShiftExpression node) {
		node.getUnaryExpression().apply(this);
	}
	
	@Override
	public void caseABinaryShiftExpression(ABinaryShiftExpression node) {
		expressionStack.push(binaryExpression(node, node.getShiftExpression(), node.getShiftBinaryOp(), node.getUnaryExpression()));
	}
	
	@Override
	public void caseAPrioritizedUnaryExpression(APrioritizedUnaryExpression node) {
		node.getSuffixExpression().apply(this);
	}
	
	@Override
	public void caseAUnaryUnaryExpression(AUnaryUnaryExpression node) {
		expressionStack.push(new UnaryExpressionNode(source(node), UnaryOpType.get(trim(node.getUnaryOp())), expression(node.getUnaryExpression())));
	}
	
	@Override
	public void caseADereferenceUnaryExpression(ADereferenceUnaryExpression node) {
		expressionStack.push(new DereferenceExpressionNode(source(node), expression(node.getUnaryExpression())));
	}
	
	@Override
	public void caseAAddressOfUnaryExpression(AAddressOfUnaryExpression node) {
		expressionStack.push(new AddressExpressionNode(source(node), node.getMut() != null, expression(node.getUnaryExpression())));
	}
	
	@Override
	public void caseADoubleAddressOfUnaryExpression(ADoubleAddressOfUnaryExpression node) {
		expressionStack.push(new AddressExpressionNode(source(node), false, new AddressExpressionNode(source(node), node.getMut() != null, expression(node.getUnaryExpression()))));
	}
	
	@Override
	public void caseAPrioritizedSuffixExpression(APrioritizedSuffixExpression node) {
		node.getCallExpression().apply(this);
	}
	
	@Override
	public void caseAIndexSuffixExpression(AIndexSuffixExpression node) {
		expressionStack.push(new IndexExpressionNode(source(node), expression(node.getSuffixExpression()), expression(node.getExpression())));
	}
	
	@Override
	public void caseAMemberSuffixExpression(AMemberSuffixExpression node) {
		expressionStack.push(new MemberExpressionNode(source(node), expression(node.getSuffixExpression()), trim(node.getMember())));
	}
	
	@Override
	public void caseAMethodSuffixExpression(AMethodSuffixExpression node) {
		expressionStack.push(new MethodExpressionNode(source(node), expression(node.getSuffixExpression()), path(node.getPath()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAPrioritizedCallExpression(APrioritizedCallExpression node) {
		node.getCompoundExpression().apply(this);
	}
	
	@Override
	public void caseACallCallExpression(ACallCallExpression node) {
		expressionStack.push(new CallExpressionNode(source(node), expression(node.getCallExpression()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAParenthesesCompoundExpression(AParenthesesCompoundExpression node) {
		node.getParenthesesExpression().apply(this);
	}
	
	@Override
	public void caseAPrimaryCompoundExpression(APrimaryCompoundExpression node) {
		node.getPrimaryExpression().apply(this);
	}
	
	@Override
	public void caseAArrayListCompoundExpression(AArrayListCompoundExpression node) {
		expressionStack.push(new ArrayListExpressionNode(source(node), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAArrayRepeatCompoundExpression(AArrayRepeatCompoundExpression node) {
		expressionStack.push(new ArrayRepeatExpressionNode(source(node), expression(node.getExpression()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseATupleCompoundExpression(ATupleCompoundExpression node) {
		expressionStack.push(new TupleExpressionNode(source(node), tupleExpressionList(node.getTupleExpressionList())));
	}
	
	@Override
	public void caseAStructCompoundExpression(AStructCompoundExpression node) {
		expressionStack.push(new StructExpressionNode(source(node), path(node.getPath()), structExpressionListPair(node.getStructExpressionList())));
	}
	
	@Override
	public void caseAParenthesesExpression(AParenthesesExpression node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseAScalarPrimaryExpression(AScalarPrimaryExpression node) {
		node.getScalar().apply(this);
	}
	
	@Override
	public void caseAPathPrimaryExpression(APathPrimaryExpression node) {
		expressionStack.push(new PathExpressionNode(source(node), path(node.getPath())));
	}
	
	@Override
	public void caseAExpressionList(AExpressionList node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseAExpressionListTail(AExpressionListTail node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseATupleExpressionList(ATupleExpressionList node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseATupleExpressionListHead(ATupleExpressionListHead node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseALabelledExpressionList(ALabelledExpressionList node) {
		node.getLabelledExpression().apply(this);
	}
	
	@Override
	public void caseALabelledExpressionListTail(ALabelledExpressionListTail node) {
		node.getLabelledExpression().apply(this);
	}
	
	@Override
	public void caseALabelledExpression(ALabelledExpression node) {
		labelledExpressionPairStack.push(new Pair<>(label(node.getLabel()), expression(node.getExpression())));
	}
	
	@Override
	public void caseANullScalar(ANullScalar node) {
		expressionStack.push(new NullExpressionNode(source(node)));
	}
	
	@Override
	public void caseABoolScalar(ABoolScalar node) {
		expressionStack.push(new BoolExpressionNode(source(node), Boolean.parseBoolean(text(node.getBoolValue()))));
	}
	
	@Override
	public void caseAIntScalar(AIntScalar node) {
		expressionStack.push(new IntExpressionNode(source(node), Helpers.parseBigInt(text(node.getIntValue())).longValue()));
	}
	
	@Override
	public void caseANatScalar(ANatScalar node) {
		expressionStack.push(new NatExpressionNode(source(node), Helpers.parseBigInt(text(node.getNatValue())).longValue()));
	}
	
	@Override
	public void caseAWordScalar(AWordScalar node) {
		expressionStack.push(new WordExpressionNode(source(node), Helpers.parseBigInt(text(node.getWordValue())).longValue()));
	}
	
	@Override
	public void caseACharScalar(ACharScalar node) {
		expressionStack.push(new CharExpressionNode(source(node), Helpers.unescapeChar(node.getCharValue().getText())));
	}
	
	@Override
	public void caseASizeofScalar(ASizeofScalar node) {
		expressionStack.push(new SizeofExpressionNode(source(node), type(node.getType())));
	}
	
	@Override
	public void caseAConstantExpression(AConstantExpression node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseAPrioritizedBraceExpression(APrioritizedBraceExpression node) {
		node.getBraceAssignmentExpression().apply(this);
	}
	
	@Override
	public void caseAClosureBraceExpression(AClosureBraceExpression node) {
		long id = Main.rootScope.nextLocalId();
		@NonNull Pair<@NonNull ScopedBodyNode, @Nullable ReturnNode> closureBodyPair = expressionClosureBodyPair(node.getBraceExpression());
		@NonNull FunctionDefinitionNode functionNode = new FunctionDefinitionNode(source(node), "\\fn" + id, closureDeclaratorList(node.getClosureDeclaratorList()), null, closureBodyPair.left, true);
		if (closureBodyPair.right != null) {
			closureBodyPair.right.closureDefinition = functionNode;
		}
		expressionStack.push(new ClosureExpressionNode(source(node), "\\Closure" + id, functionNode));
	}
	
	@Override
	public void caseAPrioritizedBraceAssignmentExpression(APrioritizedBraceAssignmentExpression node) {
		node.getBraceTernaryExpression().apply(this);
	}
	
	@Override
	public void caseAAssignmentBraceAssignmentExpression(AAssignmentBraceAssignmentExpression node) {
		expressionStack.push(new AssignmentExpressionNode(source(node), expression(node.getBraceUnaryExpression()), AssignmentOpType.get(trim(node.getAssignmentOp())), expression(node.getBraceExpression())));
	}
	
	@Override
	public void caseAPrioritizedBraceTernaryExpression(APrioritizedBraceTernaryExpression node) {
		node.getBraceCastExpression().apply(this);
	}
	
	@Override
	public void caseATernaryBraceTernaryExpression(ATernaryBraceTernaryExpression node) {
		expressionStack.push(new TernaryExpressionNode(source(node), expression(node.getBraceCastExpression()), expression(node.getExpression()), expression(node.getBraceTernaryExpression())));
	}
	
	@Override
	public void caseAPrioritizedBraceCastExpression(APrioritizedBraceCastExpression node) {
		node.getBraceLogicalExpression().apply(this);
	}
	
	@Override
	public void caseACastBraceCastExpression(ACastBraceCastExpression node) {
		expressionStack.push(new CastExpressionNode(source(node), expression(node.getBraceUnaryExpression()), type(node.getType())));
	}
	
	@Override
	public void caseAPrioritizedBraceLogicalExpression(APrioritizedBraceLogicalExpression node) {
		node.getBraceEqualityExpression().apply(this);
	}
	
	@Override
	public void caseABinaryBraceLogicalExpression(ABinaryBraceLogicalExpression node) {
		expressionStack.push(binaryExpression(node, node.getBraceLogicalExpression(), node.getLogicalBinaryOp(), node.getBraceEqualityExpression()));
	}
	
	@Override
	public void caseAPrioritizedBraceEqualityExpression(APrioritizedBraceEqualityExpression node) {
		node.getBraceComparativeExpression().apply(this);
	}
	
	@Override
	public void caseABinaryBraceEqualityExpression(ABinaryBraceEqualityExpression node) {
		expressionStack.push(binaryExpression(node, node.getBraceEqualityExpression(), node.getEqualityBinaryOp(), node.getBraceComparativeExpression()));
	}
	
	@Override
	public void caseAPrioritizedBraceComparativeExpression(APrioritizedBraceComparativeExpression node) {
		node.getBraceAdditiveExpression().apply(this);
	}
	
	@Override
	public void caseABinaryBraceComparativeExpression(ABinaryBraceComparativeExpression node) {
		expressionStack.push(binaryExpression(node, node.getBraceComparativeExpression(), node.getComparativeBinaryOp(), node.getBraceAdditiveExpression()));
	}
	
	@Override
	public void caseAPrioritizedBraceAdditiveExpression(APrioritizedBraceAdditiveExpression node) {
		node.getBraceMultiplicativeExpression().apply(this);
	}
	
	@Override
	public void caseABinaryBraceAdditiveExpression(ABinaryBraceAdditiveExpression node) {
		expressionStack.push(binaryExpression(node, node.getBraceAdditiveExpression(), node.getAdditiveBinaryOp(), node.getBraceMultiplicativeExpression()));
	}
	
	@Override
	public void caseAPrioritizedBraceMultiplicativeExpression(APrioritizedBraceMultiplicativeExpression node) {
		node.getBraceShiftExpression().apply(this);
	}
	
	@Override
	public void caseABinaryBraceMultiplicativeExpression(ABinaryBraceMultiplicativeExpression node) {
		expressionStack.push(binaryExpression(node, node.getBraceMultiplicativeExpression(), node.getMultiplicativeBinaryOp(), node.getBraceShiftExpression()));
	}
	
	@Override
	public void caseAPrioritizedBraceShiftExpression(APrioritizedBraceShiftExpression node) {
		node.getBraceUnaryExpression().apply(this);
	}
	
	@Override
	public void caseABinaryBraceShiftExpression(ABinaryBraceShiftExpression node) {
		expressionStack.push(binaryExpression(node, node.getBraceShiftExpression(), node.getShiftBinaryOp(), node.getBraceUnaryExpression()));
	}
	
	@Override
	public void caseAPrioritizedBraceUnaryExpression(APrioritizedBraceUnaryExpression node) {
		node.getBraceSuffixExpression().apply(this);
	}
	
	@Override
	public void caseAUnaryBraceUnaryExpression(AUnaryBraceUnaryExpression node) {
		expressionStack.push(new UnaryExpressionNode(source(node), UnaryOpType.get(trim(node.getUnaryOp())), expression(node.getBraceUnaryExpression())));
	}
	
	@Override
	public void caseADereferenceBraceUnaryExpression(ADereferenceBraceUnaryExpression node) {
		expressionStack.push(new DereferenceExpressionNode(source(node), expression(node.getBraceUnaryExpression())));
	}
	
	@Override
	public void caseAAddressOfBraceUnaryExpression(AAddressOfBraceUnaryExpression node) {
		expressionStack.push(new AddressExpressionNode(source(node), node.getMut() != null, expression(node.getBraceUnaryExpression())));
	}
	
	@Override
	public void caseADoubleAddressOfBraceUnaryExpression(ADoubleAddressOfBraceUnaryExpression node) {
		expressionStack.push(new AddressExpressionNode(source(node), false, new AddressExpressionNode(source(node), node.getMut() != null, expression(node.getBraceUnaryExpression()))));
	}
	
	@Override
	public void caseAPrioritizedBraceSuffixExpression(APrioritizedBraceSuffixExpression node) {
		node.getBraceCallExpression().apply(this);
	}
	
	@Override
	public void caseAIndexBraceSuffixExpression(AIndexBraceSuffixExpression node) {
		expressionStack.push(new IndexExpressionNode(source(node), expression(node.getBraceSuffixExpression()), expression(node.getExpression())));
	}
	
	@Override
	public void caseAMemberBraceSuffixExpression(AMemberBraceSuffixExpression node) {
		expressionStack.push(new MemberExpressionNode(source(node), expression(node.getBraceSuffixExpression()), trim(node.getMember())));
	}
	
	@Override
	public void caseAMethodBraceSuffixExpression(AMethodBraceSuffixExpression node) {
		expressionStack.push(new MethodExpressionNode(source(node), expression(node.getBraceSuffixExpression()), path(node.getPath()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAPrioritizedBraceCallExpression(APrioritizedBraceCallExpression node) {
		node.getBraceCompoundExpression().apply(this);
	}
	
	@Override
	public void caseACallBraceCallExpression(ACallBraceCallExpression node) {
		expressionStack.push(new CallExpressionNode(source(node), expression(node.getBraceCallExpression()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAParenthesesBraceCompoundExpression(AParenthesesBraceCompoundExpression node) {
		node.getParenthesesExpression().apply(this);
	}
	
	@Override
	public void caseAPrimaryBraceCompoundExpression(APrimaryBraceCompoundExpression node) {
		node.getPrimaryExpression().apply(this);
	}
	
	@Override
	public void caseAArrayListBraceCompoundExpression(AArrayListBraceCompoundExpression node) {
		expressionStack.push(new ArrayListExpressionNode(source(node), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAArrayRepeatBraceCompoundExpression(AArrayRepeatBraceCompoundExpression node) {
		expressionStack.push(new ArrayRepeatExpressionNode(source(node), expression(node.getExpression()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseATupleBraceCompoundExpression(ATupleBraceCompoundExpression node) {
		expressionStack.push(new TupleExpressionNode(source(node), tupleExpressionList(node.getTupleExpressionList())));
	}
}
