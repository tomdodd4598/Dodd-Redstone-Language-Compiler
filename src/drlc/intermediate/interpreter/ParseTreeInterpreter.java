package drlc.intermediate.interpreter;

import static drlc.Helpers.array;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.analysis.AnalysisAdapter;
import drlc.intermediate.ast.*;
import drlc.intermediate.ast.conditional.*;
import drlc.intermediate.ast.element.*;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.ast.section.*;
import drlc.intermediate.ast.stop.*;
import drlc.intermediate.ast.type.*;
import drlc.intermediate.component.*;
import drlc.node.*;

public class ParseTreeInterpreter extends AnalysisAdapter {
	
	public StartNode ast;
	
	public final Deque<UnitNode> unitStack = new ArrayDeque<>();
	
	public final Deque<SetupNode> setupStack = new ArrayDeque<>();
	public final Deque<ProgramNode> programStack = new ArrayDeque<>();
	
	public final Deque<DirectiveNode> directiveStack = new ArrayDeque<>();
	
	public final Deque<ProgramSectionNode> programSectionStack = new ArrayDeque<>();
	
	public final Deque<BasicSectionNode> basicSectionStack = new ArrayDeque<>();
	
	public final Deque<ScopeContentsNode> scopeContentsStack = new ArrayDeque<>();
	
	public final Deque<ConditionalEndNode> conditionalEndStack = new ArrayDeque<>();
	public final Deque<ConditionalSectionNode> conditionalSectionStack = new ArrayDeque<>();
	
	public final Deque<StopNode> stopStack = new ArrayDeque<>();
	
	public final Deque<TypeNode> typeStack = new ArrayDeque<>();
	public final Deque<RawTypeNode> rawTypeStack = new ArrayDeque<>();
	
	public final Deque<VariableDeclaratorNode> variableDeclaratorStack = new ArrayDeque<>();
	
	public final Deque<ParameterNode> parameterStack = new ArrayDeque<>();
	
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
	
	private @NonNull ConditionalStartNode conditionalStart(Node node) {
		return new ConditionalStartNode(array(node), scope(node));
	}
	
	private @NonNull TypeNode type(Node node) {
		return traverse(node, typeStack);
	}
	
	private @Nullable TypeNode returnType(PReturnType node) {
		return traverseNullable(node, typeStack);
	}
	
	private @NonNull VariableDeclaratorNode variableDeclarator(Node node) {
		return traverse(node, variableDeclaratorStack);
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
	
	private @NonNull List<TypeNode> typeList(PTypeList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			ATypeList typeList = (ATypeList) node;
			return traverseList(typeList.getType(), typeList.getTypeListTail(), typeStack);
		}
	}
	
	private @NonNull List<ParameterNode> parameterList(PParameterList node) {
		if (node == null) {
			return new ArrayList<>();
		}
		else {
			AParameterList parameterList = (AParameterList) node;
			return traverseList(parameterList.getParameterDeclarator(), parameterList.getParameterListTail(), parameterStack);
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
	
	private @NonNull VariableModifier variableModifier(List<TVariableModifier> variableModifiers) {
		boolean _static = false, mut = false;
		for (TVariableModifier variableModifier : variableModifiers) {
			String str = variableModifier.getText();
			_static |= str.equals(Global.STATIC);
			mut |= str.equals(Global.MUT);
		}
		return new VariableModifier(_static, mut);
	}
	
	@SuppressWarnings("null")
	private @NonNull String text(Token token) {
		return token.getText();
	}
	
	private @Nullable String identifier(PIdentifier identifier) {
		return identifier instanceof ANameIdentifier ? text(((ANameIdentifier) identifier).getName()) : null;
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
		Helpers.nodeError(array(node), "%s parse tree node not supported!", node.getClass().getSimpleName());
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
		programStack.push(new ProgramNode(array(node), traverseList(node.getProgramSection(), programSectionStack)));
	}
	
	@Override
	public void caseADirective(ADirective node) {
		directiveStack.push(new DirectiveNode(array(node), text(node.getName()), expressionList(node.getExpressionList())));
	}
	
	@Override
	public void caseAFunctionDefinitionProgramSection(AFunctionDefinitionProgramSection node) {
		node.getFunctionDefinition().apply(this);
	}
	
	@Override
	public void caseABasicSectionProgramSection(ABasicSectionProgramSection node) {
		programSectionStack.push(traverse(node.getBasicSection(), basicSectionStack));
	}
	
	@Override
	public void caseAScopedSectionBasicSection(AScopedSectionBasicSection node) {
		node.getScopedSection().apply(this);
	}
	
	@Override
	public void caseAConstantDefinitionBasicSection(AConstantDefinitionBasicSection node) {
		node.getConstantDefinition().apply(this);
	}
	
	@Override
	public void caseAVariableDeclarationBasicSection(AVariableDeclarationBasicSection node) {
		node.getVariableDeclaration().apply(this);
	}
	
	@Override
	public void caseAExpressionStatementBasicSection(AExpressionStatementBasicSection node) {
		node.getExpressionStatement().apply(this);
	}
	
	@Override
	public void caseAConditionalSectionBasicSection(AConditionalSectionBasicSection node) {
		basicSectionStack.push(traverse(node.getConditionalSection(), conditionalSectionStack));
	}
	
	@Override
	public void caseAIterativeSectionBasicSection(AIterativeSectionBasicSection node) {
		node.getIterativeSection().apply(this);
	}
	
	@Override
	public void caseAGotoStatementBasicSection(AGotoStatementBasicSection node) {
		node.getGotoStatement().apply(this);
	}
	
	@Override
	public void caseASectionLabelBasicSection(ASectionLabelBasicSection node) {
		node.getSectionLabel().apply(this);
	}
	
	@Override
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		programSectionStack.push(new FunctionDefinitionNode(array(node), text(node.getName()), parameterList(node.getParameterList()), returnType(node.getReturnType()), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAScopedSection(AScopedSection node) {
		basicSectionStack.push(new ScopedSectionNode(array(node), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAConstantDefinition(AConstantDefinition node) {
		basicSectionStack.push(new ConstantDefinitionNode(array(node), text(node.getName()), type(node.getTypeAnnotation()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {
		basicSectionStack.push(new VariableDeclarationNode(array(node), variableDeclarator(node.getVariableDeclarator())));
	}
	
	@Override
	public void caseAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {
		basicSectionStack.push(new VariableInitializationNode(array(node), variableDeclarator(node.getVariableDeclarator()), expression(node.getExpression())));
	}
	
	@Override
	public void caseABasicExpressionStatement(ABasicExpressionStatement node) {
		basicSectionStack.push(new ExpressionStatementNode(array(node), expression(node.getExpression())));
	}
	
	@Override
	public void caseAAssignmentExpressionStatement(AAssignmentExpressionStatement node) {
		basicSectionStack.push(new AssignmentStatementNode(array(node), expression(node.getAssignmentExpression()), AssignmentOpType.get(trim(node.getAssignmentOp())), expression(node.getExpression())));
	}
	
	@Override
	public void caseAConditionalSection(AConditionalSection node) {
		conditionalSectionStack.push(new ConditionalSectionNode(array(node), unless(node.getConditionalBranchKeyword()), expression(node.getExpression()), conditionalStart(node.getScopeContents()), traverseNullable(node.getElseSection(), conditionalEndStack)));
	}
	
	@Override
	public void caseAExcludingBranchElseSection(AExcludingBranchElseSection node) {
		conditionalEndStack.push(new ElseNode(array(node), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAIncludingBranchElseSection(AIncludingBranchElseSection node) {
		conditionalEndStack.push(new ConditionalElseNode(array(node), traverse(node.getConditionalSection(), conditionalSectionStack)));
	}
	
	@Override
	public void caseALoopIterativeSection(ALoopIterativeSection node) {
		basicSectionStack.push(new LoopIterativeSectionNode(array(node), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseAConditionalIterativeSection(AConditionalIterativeSection node) {
		basicSectionStack.push(new ConditionalIterativeSectionNode(array(node), until(node.getConditionalIterativeKeyword()), expression(node.getExpression()), scope(node.getScopeContents())));
	}
	
	@Override
	public void caseADoConditionalIterativeSection(ADoConditionalIterativeSection node) {
		basicSectionStack.push(new DoConditionalIterativeSectionNode(array(node), scope(node.getScopeContents()), until(node.getConditionalIterativeKeyword()), expression(node.getExpression())));
	}
	
	@Override
	public void caseAGotoStatement(AGotoStatement node) {
		basicSectionStack.push(new GotoSectionNode(array(node), text(node.getName())));
	}
	
	@Override
	public void caseASectionLabel(ASectionLabel node) {
		basicSectionStack.push(new SectionLabelNode(array(node), text(node.getName())));
	}
	
	@Override
	public void caseAScopeContents(AScopeContents node) {
		basicSectionStack.push(new ScopeContentsNode(array(node), traverseList(node.getBasicSection(), basicSectionStack), traverseNullable(node.getStopStatement(), stopStack)));
	}
	
	@Override
	public void caseAExitStopStatement(AExitStopStatement node) {
		stopStack.push(new ExitNode(array(node)));
	}
	
	@Override
	public void caseAReturnStopStatement(AReturnStopStatement node) {
		stopStack.push(new ReturnNode(array(node)));
	}
	
	@Override
	public void caseAContinueStopStatement(AContinueStopStatement node) {
		stopStack.push(new ContinueNode(array(node)));
	}
	
	@Override
	public void caseABreakStopStatement(ABreakStopStatement node) {
		stopStack.push(new BreakNode(array(node)));
	}
	
	@Override
	public void caseAExitExpressionStopStatement(AExitExpressionStopStatement node) {
		stopStack.push(new ExitExpressionNode(array(node), expression(node.getExpression())));
	}
	
	@Override
	public void caseAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {
		stopStack.push(new ReturnExpressionNode(array(node), expression(node.getExpression())));
	}
	
	@Override
	public void caseAType(AType node) {
		typeStack.push(new TypeNode(array(node), node.getAnd().size(), traverse(node.getRawType(), rawTypeStack)));
	}
	
	@Override
	public void caseABasicRawType(ABasicRawType node) {
		rawTypeStack.push(new BasicRawTypeNode(array(node), text(node.getName())));
	}
	
	@Override
	public void caseAArrayRawType(AArrayRawType node) {
		rawTypeStack.push(new ArrayRawTypeNode(array(node), type(node.getType()), expression(node.getConstantExpression())));
	}
	
	@Override
	public void caseAFunctionRawType(AFunctionRawType node) {
		rawTypeStack.push(new FunctionRawTypeNode(array(node), typeList(node.getTypeList()), returnType(node.getReturnType())));
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
	public void caseAVariableDeclarator(AVariableDeclarator node) {
		variableDeclaratorStack.push(new VariableDeclaratorNode(array(node), variableModifier(node.getVariableModifier()), text(node.getName()), type(node.getTypeAnnotation())));
	}
	
	@Override
	public void caseATypeAnnotation(ATypeAnnotation node) {
		node.getType().apply(this);
	}
	
	@Override
	public void caseAParameterList(AParameterList node) {
		node.getParameterDeclarator().apply(this);
	}
	
	@Override
	public void caseAParameterListTail(AParameterListTail node) {
		node.getParameterDeclarator().apply(this);
	}
	
	@Override
	public void caseAParameterDeclarator(AParameterDeclarator node) {
		parameterStack.push(new ParameterNode(array(node), variableModifier(node.getVariableModifier()), identifier(node.getIdentifier()), type(node.getTypeAnnotation())));
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
		expressionStack.push(binaryExpression(node, node.getExpression4(), node.getShiftBinaryOp(), node.getExpression5()));
	}
	
	@Override
	public void caseAPrioritizedExpression5(APrioritizedExpression5 node) {
		node.getExpression6().apply(this);
	}
	
	@Override
	public void caseABinaryExpression5(ABinaryExpression5 node) {
		expressionStack.push(binaryExpression(node, node.getExpression5(), node.getMultiplicativeBinaryOp(), node.getExpression6()));
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
		expressionStack.push(new AddressExpressionNode(array(node), expression(node.getExpression6())));
	}
	
	@Override
	public void caseAPrioritizedExpression7(APrioritizedExpression7 node) {
		node.getExpression8().apply(this);
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
	public void caseAFunctionExpression7(AFunctionExpression7 node) {
		expressionStack.push(new FunctionCallExpressionNode(array(node), expression(node.getExpression7()), expressionList(node.getExpressionList())));
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
	public void caseAValueExpression8(AValueExpression8 node) {
		node.getValue().apply(this);
	}
	
	@Override
	public void caseAVariableExpression8(AVariableExpression8 node) {
		expressionStack.push(new VariableExpressionNode(array(node), text(node.getName())));
	}
	
	@Override
	public void caseAParenthesesExpression8(AParenthesesExpression8 node) {
		node.getExpression().apply(this);
	}
	
	@Override
	public void caseABoolValue(ABoolValue node) {
		expressionStack.push(new BoolExpressionNode(array(node), Boolean.parseBoolean(text(node.getBoolValue()))));
	}
	
	@Override
	public void caseAIntValue(AIntValue node) {
		expressionStack.push(new IntExpressionNode(array(node), Helpers.parseInt(text(node.getIntValue()))));
	}
	
	@Override
	public void caseANatValue(ANatValue node) {
		expressionStack.push(new NatExpressionNode(array(node), Helpers.parseInt(text(node.getNatValue()))));
	}
	
	@Override
	public void caseACharValue(ACharValue node) {
		expressionStack.push(new CharExpressionNode(array(node), Helpers.unescapeChar(node.getCharValue().getText())));
	}
	
	@Override
	public void caseANullValue(ANullValue node) {
		expressionStack.push(new NullExpressionNode(array(node)));
	}
	
	@Override
	public void caseASizeofValue(ASizeofValue node) {
		expressionStack.push(new SizeofExpressionNode(array(node), type(node.getType())));
	}
}
