package drlc;

import static drlc.Helpers.array;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.analysis.AnalysisAdapter;
import drlc.intermediate.ast.*;
import drlc.intermediate.ast.element.*;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.ast.section.*;
import drlc.intermediate.ast.stop.*;
import drlc.intermediate.ast.type.*;
import drlc.intermediate.component.*;
import drlc.node.*;

public class ParseVisitor extends AnalysisAdapter {
	
	public StartNode ast;
	
	public final Deque<UnitNode> unitStack = new ArrayDeque<>();
	
	public final Deque<SetupNode> setupStack = new ArrayDeque<>();
	public final Deque<ProgramNode> programStack = new ArrayDeque<>();
	
	public final Deque<DirectiveNode> directiveStack = new ArrayDeque<>();
	
	public final Deque<StaticSectionNode<?, ?>> staticSectionStack = new ArrayDeque<>();
	public final Deque<RuntimeSectionNode<?, ?>> runtimeSectionStack = new ArrayDeque<>();
	
	public final Deque<ScopeContentsNode> scopeContentsStack = new ArrayDeque<>();
	
	public final Deque<ASTNode<?, ?>> conditionalEndStack = new ArrayDeque<>();
	public final Deque<ConditionalSectionNode> conditionalSectionStack = new ArrayDeque<>();
	
	public final Deque<StopNode> stopStack = new ArrayDeque<>();
	
	public final Deque<TypeNode> typeStack = new ArrayDeque<>();
	
	public final Deque<DeclaratorNode> declaratorStack = new ArrayDeque<>();
	
	public final Deque<ExpressionNode> expressionStack = new ArrayDeque<>();
	
	@SuppressWarnings("null")
	private <T> @NonNull T traverse(Node node, Deque<T> stack) {
		node.apply(this);
		return stack.pop();
	}
	
	private <T> @Nullable T traverseNullable(Node node, Deque<T> stack) {
		return node == null ? null : traverse(node, stack);
	}
	
	private @NonNull ScopeContentsNode scope(Node node) {
		return traverse(node, scopeContentsStack);
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
		return new BinaryExpressionNode(array(node), expression(left), BinaryOpType.get(trim(op)), expression(right));
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
		List<TypeNode> typeList = new ArrayList<>();
		if (node == null) {
			return typeList;
		}
		else {
			ATupleTypeList tupleTypeList = (ATupleTypeList) node;
			typeList.add(type(tupleTypeList.getType()));
			typeList.addAll(typeList(tupleTypeList.getTypeList()));
			return typeList;
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
	
	private @NonNull List<DeclaratorNode> declaratorList(PDeclaratorList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			ADeclaratorList declaratorList = (ADeclaratorList) node;
			return traverseList(declaratorList.getDeclarator(), declaratorList.getDeclaratorListTail(), declaratorStack);
		}
	}
	
	private @NonNull List<ExpressionNode> tupleExpressionList(PTupleExpressionList node) {
		List<ExpressionNode> expressionList = new ArrayList<>();
		if (node == null) {
			return expressionList;
		}
		else {
			ATupleExpressionList tupleExpressionList = (ATupleExpressionList) node;
			expressionList.add(expression(tupleExpressionList.getExpression()));
			expressionList.addAll(expressionList(tupleExpressionList.getExpressionList()));
			return expressionList;
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
	
	private @NonNull VariableModifier variableModifier(Node node, List<PVariableModifier> variableModifiers) {
		boolean _static = false, mut = false;
		for (PVariableModifier variableModifier : variableModifiers) {
			String str = trim(variableModifier);
			if (str.equals(Global.STATIC)) {
				if (_static) {
					throw Helpers.nodeError(node, "Repeated modifier \"%s\" in variable declarator!", Global.STATIC);
				}
				_static = true;
			}
			else if (str.equals(Global.MUT)) {
				if (mut) {
					throw Helpers.nodeError(node, "Repeated modifier \"%s\" in variable declarator!", Global.MUT);
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
	
	private @Nullable String label(PIterativeSectionLabel label) {
		return label == null ? null : text(((AIterativeSectionLabel) label).getName());
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
		Helpers.nodeError(node, "%s parse tree node not supported!", node.getClass().getSimpleName());
	}
	
	@Override
	public void caseStart(Start node) {
		ast = new StartNode(array(node), traverse(node.getPUnit(), unitStack));
	}
	
	@Override
	public void caseAUnit(AUnit node) {
		unitStack.push(new UnitNode(array(node), traverse(node.getSetup(), setupStack), traverse(node.getProgram(), programStack)));
	}
	
	@Override
	public void caseASetup(ASetup node) {
		setupStack.push(new SetupNode(array(node), traverseList(node.getDirective(), directiveStack)));
	}
	
	@Override
	public void caseAProgram(AProgram node) {
		programStack.push(new ProgramNode(array(node), traverseList(node.getStaticSection(), staticSectionStack)));
	}
	
	/* UNIT SPECIFICATION */
	
	@Override
	public void caseADirective(ADirective node) {
		directiveStack.push(new DirectiveNode(array(node), text(node.getName()), expressionList(node.getExpressionList())));
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
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		staticSectionStack.push(new FunctionDefinitionNode(array(node), text(node.getName()), declaratorList(node.getDeclaratorList()), returnType(node.getReturnType()), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseATypealiasDefinition(ATypealiasDefinition node) {
		staticSectionStack.push(new TypealiasDefinitionNode(array(node), text(node.getName()), type(node.getType())));
	}
	
	@Override
	public void caseAStructDefinition(AStructDefinition node) {
		staticSectionStack.push(new StructDefinitionNode(array(node), text(node.getName()), declaratorList(node.getDeclaratorList())));
	}
	
	@Override
	public void caseAConstantDefinition(AConstantDefinition node) {
		staticSectionStack.push(new ConstantDefinitionNode(array(node), text(node.getName()), typeAnnotation(node.getTypeAnnotation()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {
		staticSectionStack.push(new VariableDeclarationNode(array(node), declarator(node.getDeclarator()), null));
	}
	
	@Override
	public void caseAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {
		staticSectionStack.push(new VariableDeclarationNode(array(node), declarator(node.getDeclarator()), expression(node.getExpression())));
	}
	
	@Override
	public void caseAEmptySection(AEmptySection node) {
		
	}
	
	@Override
	public void caseAScopedSection(AScopedSection node) {
		runtimeSectionStack.push(new ScopedSectionNode(array(node), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseABasicExpressionStatement(ABasicExpressionStatement node) {
		runtimeSectionStack.push(new ExpressionStatementNode(array(node), expression(node.getExpression())));
	}
	
	@Override
	public void caseAAssignmentExpressionStatement(AAssignmentExpressionStatement node) {
		runtimeSectionStack.push(new AssignmentStatementNode(array(node), expression(node.getAssignmentExpression()), AssignmentOpType.get(trim(node.getAssignmentOp())), expression(node.getExpression())));
	}
	
	@Override
	public void caseAConditionalSection(AConditionalSection node) {
		conditionalSectionStack.push(new ConditionalSectionNode(array(node), unless(node.getConditionalBranchKeyword()), expression(node.getConditionalExpression()), scope(node.getScopeContents()), traverseNullable(node.getElseSection(), conditionalEndStack)));
	}
	
	@Override
	public void caseAExcludingBranchElseSection(AExcludingBranchElseSection node) {
		conditionalEndStack.push(new ScopedSectionNode(array(node), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAIncludingBranchElseSection(AIncludingBranchElseSection node) {
		conditionalEndStack.push(traverse(node.getConditionalSection(), conditionalSectionStack));
	}
	
	@Override
	public void caseALoopIterativeSection(ALoopIterativeSection node) {
		runtimeSectionStack.push(new LoopIterativeSectionNode(array(node), label(node.getIterativeSectionLabel()), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAConditionalIterativeSection(AConditionalIterativeSection node) {
		runtimeSectionStack.push(new ConditionalIterativeSectionNode(array(node), label(node.getIterativeSectionLabel()), false, until(node.getConditionalIterativeKeyword()), expression(node.getConditionalExpression()), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseADoConditionalIterativeSection(ADoConditionalIterativeSection node) {
		runtimeSectionStack.push(new ConditionalIterativeSectionNode(array(node), label(node.getIterativeSectionLabel()), true, until(node.getConditionalIterativeKeyword()), expression(node.getExpression()), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAScopeContents(AScopeContents node) {
		scopeContentsStack.push(new ScopeContentsNode(array(node), traverseList(node.getRuntimeSection(), runtimeSectionStack), traverseNullable(node.getStopStatement(), stopStack)));
	}
	
	@Override
	public void caseAExitStopStatement(AExitStopStatement node) {
		stopStack.push(new ExitNode(array(node), traverseNullable(node.getExpression(), expressionStack)));
	}
	
	@Override
	public void caseAReturnStopStatement(AReturnStopStatement node) {
		stopStack.push(new ReturnNode(array(node), traverseNullable(node.getExpression(), expressionStack)));
	}
	
	@Override
	public void caseAContinueStopStatement(AContinueStopStatement node) {
		stopStack.push(new ContinueNode(array(node), textNullable(node.getName())));
	}
	
	@Override
	public void caseABreakStopStatement(ABreakStopStatement node) {
		stopStack.push(new BreakNode(array(node), textNullable(node.getName())));
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
		typeStack.push(new AddressTypeNode(array(node), node.getMut() != null, type(node.getType())));
	}
	
	@Override
	public void caseADoubleAddressOfType(ADoubleAddressOfType node) {
		typeStack.push(new AddressTypeNode(array(node), false, new AddressTypeNode(array(node), node.getMut() != null, type(node.getType()))));
	}
	
	@Override
	public void caseANominalRawType(ANominalRawType node) {
		typeStack.push(new NominalTypeNode(array(node), text(node.getName())));
	}
	
	@Override
	public void caseAArrayRawType(AArrayRawType node) {
		typeStack.push(new ArrayTypeNode(array(node), type(node.getType()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseATupleRawType(ATupleRawType node) {
		typeStack.push(new TupleTypeNode(array(node), tupleTypeList(node.getTupleTypeList())));
	}
	
	@Override
	public void caseAFunctionRawType(AFunctionRawType node) {
		typeStack.push(new FunctionTypeNode(array(node), typeList(node.getTypeList()), returnType(node.getReturnType())));
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
		declaratorStack.push(new DeclaratorNode(array(node), variableModifier(node, node.getVariableModifier()), text(node.getName()), typeAnnotation(node.getTypeAnnotation())));
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
	public void caseAExpression(AExpression node) {
		node.getExpression0().apply(this);
	}
	
	@Override
	public void caseAConstantExpression(AConstantExpression node) {
		node.getExpression0().apply(this);
	}
	
	@Override
	public void caseAAssignmentExpression(AAssignmentExpression node) {
		node.getExpression0().apply(this);
	}
	
	@Override
	public void caseAConditionalExpression(AConditionalExpression node) {
		node.getConditionalExpression0().apply(this);
	}
	
	@Override
	public void caseAPrioritizedExpression0(APrioritizedExpression0 node) {
		node.getExpression1().apply(this);
	}
	
	@Override
	public void caseABinaryExpression0(ABinaryExpression0 node) {
		expressionStack.push(binaryExpression(node, node.getExpression0(), node.getLogicalBinaryOp(), node.getExpression1()));
	}
	
	@Override
	public void caseAPrioritizedExpression1(APrioritizedExpression1 node) {
		node.getExpression2().apply(this);
	}
	
	@Override
	public void caseABinaryExpression1(ABinaryExpression1 node) {
		expressionStack.push(binaryExpression(node, node.getExpression1(), node.getEqualityBinaryOp(), node.getExpression2()));
	}
	
	@Override
	public void caseAPrioritizedExpression2(APrioritizedExpression2 node) {
		node.getExpression3().apply(this);
	}
	
	@Override
	public void caseABinaryExpression2(ABinaryExpression2 node) {
		expressionStack.push(binaryExpression(node, node.getExpression2(), node.getComparativeBinaryOp(), node.getExpression3()));
	}
	
	@Override
	public void caseAPrioritizedExpression3(APrioritizedExpression3 node) {
		node.getExpression4().apply(this);
	}
	
	@Override
	public void caseABinaryExpression3(ABinaryExpression3 node) {
		expressionStack.push(binaryExpression(node, node.getExpression3(), node.getAdditiveBinaryOp(), node.getExpression4()));
	}
	
	@Override
	public void caseAPrioritizedExpression4(APrioritizedExpression4 node) {
		node.getExpression5().apply(this);
	}
	
	@Override
	public void caseABinaryExpression4(ABinaryExpression4 node) {
		expressionStack.push(binaryExpression(node, node.getExpression4(), node.getMultiplicativeBinaryOp(), node.getExpression5()));
	}
	
	@Override
	public void caseAPrioritizedExpression5(APrioritizedExpression5 node) {
		node.getExpression6().apply(this);
	}
	
	@Override
	public void caseABinaryExpression5(ABinaryExpression5 node) {
		expressionStack.push(binaryExpression(node, node.getExpression5(), node.getShiftBinaryOp(), node.getExpression6()));
	}
	
	@Override
	public void caseAPrioritizedExpression6(APrioritizedExpression6 node) {
		node.getExpression7().apply(this);
	}
	
	@Override
	public void caseAUnaryExpression6(AUnaryExpression6 node) {
		expressionStack.push(new UnaryExpressionNode(array(node), UnaryOpType.get(trim(node.getUnaryOp())), expression(node.getExpression6())));
	}
	
	@Override
	public void caseADereferenceExpression6(ADereferenceExpression6 node) {
		expressionStack.push(new DereferenceExpressionNode(array(node), expression(node.getExpression6())));
	}
	
	@Override
	public void caseAAddressOfExpression6(AAddressOfExpression6 node) {
		expressionStack.push(new AddressExpressionNode(array(node), node.getMut() != null, expression(node.getExpression6())));
	}
	
	@Override
	public void caseADoubleAddressOfExpression6(ADoubleAddressOfExpression6 node) {
		expressionStack.push(new AddressExpressionNode(array(node), false, new AddressExpressionNode(array(node), node.getMut() != null, expression(node.getExpression6()))));
	}
	
	@Override
	public void caseAParenthesesExpression7(AParenthesesExpression7 node) {
		node.getParenthesesExpression().apply(this);
	}
	
	@Override
	public void caseASimpleExpression7(ASimpleExpression7 node) {
		node.getSimpleExpression().apply(this);
	}
	
	@Override
	public void caseAArrayListExpression7(AArrayListExpression7 node) {
		expressionStack.push(new ArrayListExpressionNode(array(node), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAArrayRepeatExpression7(AArrayRepeatExpression7 node) {
		expressionStack.push(new ArrayRepeatExpressionNode(array(node), expression(node.getExpression()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseAIndexExpression7(AIndexExpression7 node) {
		expressionStack.push(new IndexExpressionNode(array(node), expression(node.getExpression7()), expression(node.getExpression())));
	}
	
	@Override
	public void caseATupleExpression7(ATupleExpression7 node) {
		expressionStack.push(new TupleExpressionNode(array(node), tupleExpressionList(node.getTupleExpressionList())));
	}
	
	@Override
	public void caseAStructExpression7(AStructExpression7 node) {
		expressionStack.push(new StructExpressionNode(array(node), text(node.getName()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAMemberExpression7(AMemberExpression7 node) {
		@NonNull ExpressionNode expression = expression(node.getExpression7());
		expressionStack.push(new MemberExpressionNode(array(node), expression, trim(node.getSimpleExpression())));
	}
	
	@Override
	public void caseAFunctionExpression7(AFunctionExpression7 node) {
		expressionStack.push(new FunctionCallExpressionNode(array(node), expression(node.getExpression7()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAParenthesesExpression(AParenthesesExpression node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseAValueSimpleExpression(AValueSimpleExpression node) {
		node.getValue().apply(this);
	}
	
	@Override
	public void caseAVariableSimpleExpression(AVariableSimpleExpression node) {
		expressionStack.push(new VariableExpressionNode(array(node), text(node.getName())));
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
	public void caseABoolValue(ABoolValue node) {
		expressionStack.push(new BoolExpressionNode(array(node), Boolean.parseBoolean(text(node.getBoolValue()))));
	}
	
	@Override
	public void caseAIntValue(AIntValue node) {
		expressionStack.push(new IntExpressionNode(array(node), Helpers.parseBigInt(text(node.getIntValue())).longValue()));
	}
	
	@Override
	public void caseANatValue(ANatValue node) {
		expressionStack.push(new NatExpressionNode(array(node), Helpers.parseBigInt(text(node.getNatValue())).longValue()));
	}
	
	@Override
	public void caseACharValue(ACharValue node) {
		expressionStack.push(new CharExpressionNode(array(node), Helpers.unescapeChar(node.getCharValue().getText())));
	}
	
	@Override
	public void caseASizeofValue(ASizeofValue node) {
		expressionStack.push(new SizeofExpressionNode(array(node), type(node.getType())));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression0(APrioritizedConditionalExpression0 node) {
		node.getConditionalExpression1().apply(this);
	}
	
	@Override
	public void caseABinaryConditionalExpression0(ABinaryConditionalExpression0 node) {
		expressionStack.push(binaryExpression(node, node.getConditionalExpression0(), node.getLogicalBinaryOp(), node.getConditionalExpression1()));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression1(APrioritizedConditionalExpression1 node) {
		node.getConditionalExpression2().apply(this);
	}
	
	@Override
	public void caseABinaryConditionalExpression1(ABinaryConditionalExpression1 node) {
		expressionStack.push(binaryExpression(node, node.getConditionalExpression1(), node.getEqualityBinaryOp(), node.getConditionalExpression2()));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression2(APrioritizedConditionalExpression2 node) {
		node.getConditionalExpression3().apply(this);
	}
	
	@Override
	public void caseABinaryConditionalExpression2(ABinaryConditionalExpression2 node) {
		expressionStack.push(binaryExpression(node, node.getConditionalExpression2(), node.getComparativeBinaryOp(), node.getConditionalExpression3()));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression3(APrioritizedConditionalExpression3 node) {
		node.getConditionalExpression4().apply(this);
	}
	
	@Override
	public void caseABinaryConditionalExpression3(ABinaryConditionalExpression3 node) {
		expressionStack.push(binaryExpression(node, node.getConditionalExpression3(), node.getAdditiveBinaryOp(), node.getConditionalExpression4()));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression4(APrioritizedConditionalExpression4 node) {
		node.getConditionalExpression5().apply(this);
	}
	
	@Override
	public void caseABinaryConditionalExpression4(ABinaryConditionalExpression4 node) {
		expressionStack.push(binaryExpression(node, node.getConditionalExpression4(), node.getMultiplicativeBinaryOp(), node.getConditionalExpression5()));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression5(APrioritizedConditionalExpression5 node) {
		node.getConditionalExpression6().apply(this);
	}
	
	@Override
	public void caseABinaryConditionalExpression5(ABinaryConditionalExpression5 node) {
		expressionStack.push(binaryExpression(node, node.getConditionalExpression5(), node.getShiftBinaryOp(), node.getConditionalExpression6()));
	}
	
	@Override
	public void caseAPrioritizedConditionalExpression6(APrioritizedConditionalExpression6 node) {
		node.getConditionalExpression7().apply(this);
	}
	
	@Override
	public void caseAUnaryConditionalExpression6(AUnaryConditionalExpression6 node) {
		expressionStack.push(new UnaryExpressionNode(array(node), UnaryOpType.get(trim(node.getUnaryOp())), expression(node.getConditionalExpression6())));
	}
	
	@Override
	public void caseADereferenceConditionalExpression6(ADereferenceConditionalExpression6 node) {
		expressionStack.push(new DereferenceExpressionNode(array(node), expression(node.getConditionalExpression6())));
	}
	
	@Override
	public void caseAAddressOfConditionalExpression6(AAddressOfConditionalExpression6 node) {
		expressionStack.push(new AddressExpressionNode(array(node), node.getMut() != null, expression(node.getConditionalExpression6())));
	}
	
	@Override
	public void caseADoubleAddressOfConditionalExpression6(ADoubleAddressOfConditionalExpression6 node) {
		expressionStack.push(new AddressExpressionNode(array(node), false, new AddressExpressionNode(array(node), node.getMut() != null, expression(node.getConditionalExpression6()))));
	}
	
	@Override
	public void caseAParenthesesConditionalExpression7(AParenthesesConditionalExpression7 node) {
		node.getParenthesesExpression().apply(this);
	}
	
	@Override
	public void caseASimpleConditionalExpression7(ASimpleConditionalExpression7 node) {
		node.getSimpleExpression().apply(this);
	}
	
	@Override
	public void caseAArrayListConditionalExpression7(AArrayListConditionalExpression7 node) {
		expressionStack.push(new ArrayListExpressionNode(array(node), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAArrayRepeatConditionalExpression7(AArrayRepeatConditionalExpression7 node) {
		expressionStack.push(new ArrayRepeatExpressionNode(array(node), expression(node.getExpression()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseAIndexConditionalExpression7(AIndexConditionalExpression7 node) {
		expressionStack.push(new IndexExpressionNode(array(node), expression(node.getConditionalExpression7()), expression(node.getExpression())));
	}
	
	@Override
	public void caseATupleConditionalExpression7(ATupleConditionalExpression7 node) {
		expressionStack.push(new TupleExpressionNode(array(node), tupleExpressionList(node.getTupleExpressionList())));
	}
	
	@Override
	public void caseAMemberConditionalExpression7(AMemberConditionalExpression7 node) {
		@NonNull ExpressionNode expression = expression(node.getConditionalExpression7());
		expressionStack.push(new MemberExpressionNode(array(node), expression, trim(node.getSimpleExpression())));
	}
	
	@Override
	public void caseAFunctionConditionalExpression7(AFunctionConditionalExpression7 node) {
		expressionStack.push(new FunctionCallExpressionNode(array(node), expression(node.getConditionalExpression7()), expressionList(node.getExpressionList())));
	}
}
