package drlc.intermediate.interpreter;

import drlc.Generator;
import drlc.intermediate.component.*;
import drlc.intermediate.component.constant.Constant;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.*;

public class FirstPassInterpreter extends AbstractInterpreter {
	
	public FirstPassInterpreter(Generator generator) {
		super(generator);
	}
	
	// Productions
	
	@Override
	public void inStart(Start node) {
		scope = generator.program.rootScope;
		scope.setExpectingFunctionReturn(false);
		
		generator.addBuiltInTypes(node);
		generator.addBuiltInConstants(node);
		generator.addBuiltInVariables(node);
		generator.addBuiltInFunctions(node);
	}
	
	@Override
	public void outStart(Start node) {
		scope = scope.previous;
		if (scope != null) {
			throw new IllegalArgumentException(String.format("Interpreter unexpectedly finished with non-null scope!"));
		}
	}
	
	@Override
	public void inAUnit(AUnit node) {}
	
	@Override
	public void outAUnit(AUnit node) {}
	
	@Override
	public void inASetup(ASetup node) {}
	
	@Override
	public void outASetup(ASetup node) {}
	
	@Override
	public void inAProgram(AProgram node) {}
	
	@Override
	public void outAProgram(AProgram node) {}
	
	@Override
	public void caseADirective(ADirective node) {
		generator.handleDirectiveCall(node, node.getName().getText(), getArgumentEvaluationInfoList(node.getExpressionList()));
	}
	
	@Override
	public void inAFunctionDefinitionProgramSection(AFunctionDefinitionProgramSection node) {}
	
	@Override
	public void outAFunctionDefinitionProgramSection(AFunctionDefinitionProgramSection node) {}
	
	@Override
	public void inABasicSectionProgramSection(ABasicSectionProgramSection node) {}
	
	@Override
	public void outABasicSectionProgramSection(ABasicSectionProgramSection node) {}
	
	@Override
	public void inAVariableDeclarationBasicSection(AVariableDeclarationBasicSection node) {}
	
	@Override
	public void outAVariableDeclarationBasicSection(AVariableDeclarationBasicSection node) {}
	
	@Override
	public void inAExpressionStatementBasicSection(AExpressionStatementBasicSection node) {}
	
	@Override
	public void outAExpressionStatementBasicSection(AExpressionStatementBasicSection node) {}
	
	@Override
	public void inAConditionalSectionBasicSection(AConditionalSectionBasicSection node) {}
	
	@Override
	public void outAConditionalSectionBasicSection(AConditionalSectionBasicSection node) {}
	
	@Override
	public void inAIterativeSectionBasicSection(AIterativeSectionBasicSection node) {}
	
	@Override
	public void outAIterativeSectionBasicSection(AIterativeSectionBasicSection node) {}
	
	@Override
	public void inAGotoStatementBasicSection(AGotoStatementBasicSection node) {}
	
	@Override
	public void outAGotoStatementBasicSection(AGotoStatementBasicSection node) {}
	
	@Override
	public void inASectionLabelBasicSection(ASectionLabelBasicSection node) {}
	
	@Override
	public void outASectionLabelBasicSection(ASectionLabelBasicSection node) {}
	
	@Override
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		/*for (TFunctionModifier modifier : node.getFunctionModifier()) {
			modifier.apply(this);
		}*/
		FunctionModifierInfo modifierInfo = getFunctionModifierInfo(/*node.getFunctionModifier()*/);
		
		node.getFn().apply(this);
		node.getName().apply(this);
		node.getParParameterList().apply(this);
		int argc = getParameterListLength(node.getParParameterList());
		
		if (node.getReturnType() != null) {
			node.getReturnType().apply(this);
		}
		TypeInfo returnTypeInfo = createReturnTypeInfo(node, scope, node.getReturnType());
		generator.program.declareFunction(node, scope, node.getName().getText(), modifierInfo, argc, returnTypeInfo);
	}
	
	@Override
	public void inAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {}
	
	@Override
	public void outAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {}
	
	@Override
	public void inAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {}
	
	@Override
	public void outAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {}
	
	@Override
	public void inABasicInitializationExpression(ABasicInitializationExpression node) {}
	
	@Override
	public void outABasicInitializationExpression(ABasicInitializationExpression node) {}
	
	@Override
	public void inABasicExpressionStatement(ABasicExpressionStatement node) {}
	
	@Override
	public void outABasicExpressionStatement(ABasicExpressionStatement node) {}
	
	@Override
	public void inAAssignmentExpressionStatement(AAssignmentExpressionStatement node) {}
	
	@Override
	public void outAAssignmentExpressionStatement(AAssignmentExpressionStatement node) {}
	
	@Override
	public void inAConditionalSection(AConditionalSection node) {}
	
	@Override
	public void outAConditionalSection(AConditionalSection node) {}
	
	@Override
	public void inAExcludingBranchElseSection(AExcludingBranchElseSection node) {}
	
	@Override
	public void outAExcludingBranchElseSection(AExcludingBranchElseSection node) {}
	
	@Override
	public void inAIncludingBranchElseSection(AIncludingBranchElseSection node) {}
	
	@Override
	public void outAIncludingBranchElseSection(AIncludingBranchElseSection node) {}
	
	@Override
	public void inALoopIterativeSection(ALoopIterativeSection node) {}
	
	@Override
	public void outALoopIterativeSection(ALoopIterativeSection node) {}
	
	@Override
	public void inAConditionalIterativeSection(AConditionalIterativeSection node) {}
	
	@Override
	public void outAConditionalIterativeSection(AConditionalIterativeSection node) {}
	
	@Override
	public void inADoConditionalIterativeSection(ADoConditionalIterativeSection node) {}
	
	@Override
	public void outADoConditionalIterativeSection(ADoConditionalIterativeSection node) {}
	
	@Override
	public void inAGotoStatement(AGotoStatement node) {}
	
	@Override
	public void outAGotoStatement(AGotoStatement node) {}
	
	@Override
	public void inASectionLabel(ASectionLabel node) {}
	
	@Override
	public void outASectionLabel(ASectionLabel node) {}
	
	@Override
	public void inAExitStopStatement(AExitStopStatement node) {}
	
	@Override
	public void outAExitStopStatement(AExitStopStatement node) {}
	
	@Override
	public void inAReturnStopStatement(AReturnStopStatement node) {}
	
	@Override
	public void outAReturnStopStatement(AReturnStopStatement node) {}
	
	@Override
	public void inAContinueStopStatement(AContinueStopStatement node) {}
	
	@Override
	public void outAContinueStopStatement(AContinueStopStatement node) {}
	
	@Override
	public void inABreakStopStatement(ABreakStopStatement node) {}
	
	@Override
	public void outABreakStopStatement(ABreakStopStatement node) {}
	
	@Override
	public void inAExitExpressionStopStatement(AExitExpressionStopStatement node) {}
	
	@Override
	public void outAExitExpressionStopStatement(AExitExpressionStopStatement node) {}
	
	@Override
	public void inAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {}
	
	@Override
	public void outAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {}
	
	@Override
	public void caseADead0DeadSection(ADead0DeadSection node) {}
	
	@Override
	public void caseADead1DeadSection(ADead1DeadSection node) {}
	
	@Override
	public void caseADead2DeadSection(ADead2DeadSection node) {}
	
	@Override
	public void caseADead3DeadSection(ADead3DeadSection node) {}
	
	@Override
	public void caseADead4DeadSection(ADead4DeadSection node) {}
	
	@Override
	public void caseADead5DeadSection(ADead5DeadSection node) {}
	
	@Override
	public void caseADead6DeadSection(ADead6DeadSection node) {}
	
	@Override
	public void caseAType(AType node) {}
	
	@Override
	public void caseABasicRawType(ABasicRawType node) {}
	
	@Override
	public void caseAArrayRawType(AArrayRawType node) {}
	
	@Override
	public void caseAFunctionRawType(AFunctionRawType node) {}
	
	@Override
	public void caseAArrayTypeTail(AArrayTypeTail node) {}
	
	@Override
	public void caseAReturnType(AReturnType node) {}
	
	@Override
	public void inAParParameterList(AParParameterList node) {
		generator.program.pushParamList(node);
	}
	
	@Override
	public void outAParParameterList(AParParameterList node) {}
	
	@Override
	public void inAParameterList(AParameterList node) {
		generator.program.addParam(node, createDeclaratorInfo(node, node.getParameter(), getVariableModifierInfo(node.getVariableModifier()), null));
	}
	
	@Override
	public void outAParameterList(AParameterList node) {}
	
	@Override
	public void inAParameterListTail(AParameterListTail node) {
		generator.program.addParam(node, createDeclaratorInfo(node, node.getParameter(), getVariableModifierInfo(node.getVariableModifier()), null));
	}
	
	@Override
	public void outAParameterListTail(AParameterListTail node) {}
	
	@Override
	public void caseAExcludingIdentifierParameter(AExcludingIdentifierParameter node) {}
	
	@Override
	public void caseAIncludingIdentifierParameter(AIncludingIdentifierParameter node) {}
	
	@Override
	public void caseADeclarator(ADeclarator node) {}
	
	@Override
	public void caseANameIdentifier(ANameIdentifier node) {}
	
	@Override
	public void caseADiscardIdentifier(ADiscardIdentifier node) {}
	
	@Override
	public void caseATypeAnnotation(ATypeAnnotation node) {}
	
	@Override
	public void inAExpressionLvalue(AExpressionLvalue node) {}
	
	@Override
	public void outAExpressionLvalue(AExpressionLvalue node) {}
	
	@Override
	public void inAExpressionRvalue(AExpressionRvalue node) {}
	
	@Override
	public void outAExpressionRvalue(AExpressionRvalue node) {}
	
	@Override
	public void inAPrioritizedExpression0(APrioritizedExpression0 node) {}
	
	@Override
	public void outAPrioritizedExpression0(APrioritizedExpression0 node) {}
	
	@Override
	public void caseABinaryExpression0(ABinaryExpression0 node) {
		binaryExpression(node, node.getExpression0(), node.getLogicalBinaryOp().toString(), node.getExpression1());
	}
	
	@Override
	public void inAPrioritizedExpression1(APrioritizedExpression1 node) {}
	
	@Override
	public void outAPrioritizedExpression1(APrioritizedExpression1 node) {}
	
	@Override
	public void caseABinaryExpression1(ABinaryExpression1 node) {
		binaryExpression(node, node.getExpression1(), node.getEqualityBinaryOp().toString(), node.getExpression2());
	}
	
	@Override
	public void inAPrioritizedExpression2(APrioritizedExpression2 node) {}
	
	@Override
	public void outAPrioritizedExpression2(APrioritizedExpression2 node) {}
	
	@Override
	public void caseABinaryExpression2(ABinaryExpression2 node) {
		binaryExpression(node, node.getExpression2(), node.getComparativeBinaryOp().toString(), node.getExpression3());
	}
	
	@Override
	public void inAPrioritizedExpression3(APrioritizedExpression3 node) {}
	
	@Override
	public void outAPrioritizedExpression3(APrioritizedExpression3 node) {}
	
	@Override
	public void caseABinaryExpression3(ABinaryExpression3 node) {
		binaryExpression(node, node.getExpression3(), node.getAdditiveBinaryOp().toString(), node.getExpression4());
	}
	
	@Override
	public void inAPrioritizedExpression4(APrioritizedExpression4 node) {}
	
	@Override
	public void outAPrioritizedExpression4(APrioritizedExpression4 node) {}
	
	@Override
	public void caseABinaryExpression4(ABinaryExpression4 node) {
		binaryExpression(node, node.getExpression4(), node.getShiftBinaryOp().toString(), node.getExpression5());
	}
	
	@Override
	public void inAPrioritizedExpression5(APrioritizedExpression5 node) {}
	
	@Override
	public void outAPrioritizedExpression5(APrioritizedExpression5 node) {}
	
	@Override
	public void caseABinaryExpression5(ABinaryExpression5 node) {
		binaryExpression(node, node.getExpression5(), node.getMultiplicativeBinaryOp().toString(), node.getExpression6());
	}
	
	protected void binaryExpression(Node node, Node left, String op, Node right) {
		ConstantParseInfo info = generator.program.currentRoutine().getConstantParseInfo(node, false);
		if (info != null) {
			left.apply(this);
			right.apply(this);
			
			Constant[] popConstantStack = info.popConstantStack(2);
			info.constantStack.push(generator.binaryOp(node, scope, popConstantStack[0], BinaryOpType.getOpType(op), popConstantStack[1]));
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered expression node! %s", node));
		}
	}
	
	@Override
	public void inAPrioritizedExpression6(APrioritizedExpression6 node) {}
	
	@Override
	public void outAPrioritizedExpression6(APrioritizedExpression6 node) {}
	
	@Override
	public void caseAUnaryExpression6(AUnaryExpression6 node) {
		ConstantParseInfo info = generator.program.currentRoutine().getConstantParseInfo(node, false);
		if (info != null) {
			node.getUnaryOp().apply(this);
			node.getExpression6().apply(this);
			
			info.constantStack.push(generator.unaryOp(node, scope, UnaryOpType.getOpType(node.getUnaryOp().toString().trim()), info.constantStack.pop()));
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered expression node! %s", node));
		}
	}
	
	@Override
	public void caseADereferenceExpression6(ADereferenceExpression6 node) {
		throw new IllegalArgumentException(String.format("Can not dereference a compile-time constant expression! %s", node));
	}
	
	@Override
	public void caseAAddressOfExpression6(AAddressOfExpression6 node) {
		throw new IllegalArgumentException(String.format("Can not take the address of a compile-time constant expression! %s", node));
	}
	
	@Override
	public void inAPrioritizedExpression7(APrioritizedExpression7 node) {}
	
	@Override
	public void outAPrioritizedExpression7(APrioritizedExpression7 node) {}
	
	@Override
	public void caseAFunctionExpression7(AFunctionExpression7 node) {
		throw new IllegalArgumentException(String.format("Can not call a function in a compile-time constant expression! %s", node));
	}
	
	@Override
	public void inABracketExpressionList(ABracketExpressionList node) {}
	
	@Override
	public void outABracketExpressionList(ABracketExpressionList node) {}
	
	@Override
	public void inAParExpressionList(AParExpressionList node) {}
	
	@Override
	public void outAParExpressionList(AParExpressionList node) {}
	
	@Override
	public void caseAExpressionList(AExpressionList node) {
		throw new IllegalArgumentException(String.format("Unexpectedly encountered expression list node! %s", node));
	}
	
	@Override
	public void caseAExpressionListTail(AExpressionListTail node) {
		throw new IllegalArgumentException(String.format("Unexpectedly encountered expression list node! %s", node));
	}
	
	@Override
	public void inAValueExpression8(AValueExpression8 node) {}
	
	@Override
	public void outAValueExpression8(AValueExpression8 node) {}
	
	@Override
	public void caseAVariableExpression8(AVariableExpression8 node) {
		ConstantParseInfo info = generator.program.currentRoutine().getConstantParseInfo(node, false);
		if (info != null) {
			node.getName().apply(this);
			info.constantStack.push(scope.getConstant(node, node.getName().getText()));
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered expression node! %s", node));
		}
	}
	
	@Override
	public void inAParenthesesExpression8(AParenthesesExpression8 node) {}
	
	@Override
	public void outAParenthesesExpression8(AParenthesesExpression8 node) {}
	
	@Override
	public void caseABoolValue(ABoolValue node) {
		ConstantParseInfo info = generator.program.currentRoutine().getConstantParseInfo(node, false);
		if (info != null) {
			node.getBoolValue().apply(this);
			info.constantStack.push(generator.boolConstant(Boolean.parseBoolean(node.getBoolValue().getText())));
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered expression node! %s", node));
		}
	}
	
	// TODO
	@Override
	public void caseAIntValue(AIntValue node) {}
	
	// TODO
	@Override
	public void caseANatValue(ANatValue node) {}
	
	// TODO
	@Override
	public void caseACharValue(ACharValue node) {}
	
	@Override
	public void caseANullValue(ANullValue node) {
		ConstantParseInfo info = generator.program.currentRoutine().getConstantParseInfo(node, false);
		if (info != null) {
			node.getNull().apply(this);
			info.constantStack.push(generator.nullConstant);
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered expression node! %s", node));
		}
	}
	
	@Override
	public void caseASizeofValue(ASizeofValue node) {
		ConstantParseInfo info = generator.program.currentRoutine().getConstantParseInfo(node, false);
		if (info != null) {
			node.getSizeof().apply(this);
			node.getType().apply(this);
			info.constantStack.push(generator.natConstant(createTypeInfo(node, scope, node.getType()).getSize(node, generator)));
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered expression node! %s", node));
		}
	}
	
	@Override
	public void inAEqualsAssignmentOp(AEqualsAssignmentOp node) {}
	
	@Override
	public void outAEqualsAssignmentOp(AEqualsAssignmentOp node) {}
	
	@Override
	public void inALogicalAndAssignmentOp(ALogicalAndAssignmentOp node) {}
	
	@Override
	public void outALogicalAndAssignmentOp(ALogicalAndAssignmentOp node) {}
	
	@Override
	public void inALogicalOrAssignmentOp(ALogicalOrAssignmentOp node) {}
	
	@Override
	public void outALogicalOrAssignmentOp(ALogicalOrAssignmentOp node) {}
	
	@Override
	public void inAPlusAssignmentOp(APlusAssignmentOp node) {}
	
	@Override
	public void outAPlusAssignmentOp(APlusAssignmentOp node) {}
	
	@Override
	public void inAAndAssignmentOp(AAndAssignmentOp node) {}
	
	@Override
	public void outAAndAssignmentOp(AAndAssignmentOp node) {}
	
	@Override
	public void inAOrAssignmentOp(AOrAssignmentOp node) {}
	
	@Override
	public void outAOrAssignmentOp(AOrAssignmentOp node) {}
	
	@Override
	public void inAXorAssignmentOp(AXorAssignmentOp node) {}
	
	@Override
	public void outAXorAssignmentOp(AXorAssignmentOp node) {}
	
	@Override
	public void inAMinusAssignmentOp(AMinusAssignmentOp node) {}
	
	@Override
	public void outAMinusAssignmentOp(AMinusAssignmentOp node) {}
	
	@Override
	public void inALeftShiftAssignmentOp(ALeftShiftAssignmentOp node) {}
	
	@Override
	public void outALeftShiftAssignmentOp(ALeftShiftAssignmentOp node) {}
	
	@Override
	public void inARightShiftAssignmentOp(ARightShiftAssignmentOp node) {}
	
	@Override
	public void outARightShiftAssignmentOp(ARightShiftAssignmentOp node) {}
	
	@Override
	public void inALeftRotateAssignmentOp(ALeftRotateAssignmentOp node) {}
	
	@Override
	public void outALeftRotateAssignmentOp(ALeftRotateAssignmentOp node) {}
	
	@Override
	public void inARightRotateAssignmentOp(ARightRotateAssignmentOp node) {}
	
	@Override
	public void outARightRotateAssignmentOp(ARightRotateAssignmentOp node) {}
	
	@Override
	public void inAMultiplyAssignmentOp(AMultiplyAssignmentOp node) {}
	
	@Override
	public void outAMultiplyAssignmentOp(AMultiplyAssignmentOp node) {}
	
	@Override
	public void inADivideAssignmentOp(ADivideAssignmentOp node) {}
	
	@Override
	public void outADivideAssignmentOp(ADivideAssignmentOp node) {}
	
	@Override
	public void inARemainderAssignmentOp(ARemainderAssignmentOp node) {}
	
	@Override
	public void outARemainderAssignmentOp(ARemainderAssignmentOp node) {}
	
	@Override
	public void inALogicalAndLogicalBinaryOp(ALogicalAndLogicalBinaryOp node) {}
	
	@Override
	public void outALogicalAndLogicalBinaryOp(ALogicalAndLogicalBinaryOp node) {}
	
	@Override
	public void inALogicalOrLogicalBinaryOp(ALogicalOrLogicalBinaryOp node) {}
	
	@Override
	public void outALogicalOrLogicalBinaryOp(ALogicalOrLogicalBinaryOp node) {}
	
	@Override
	public void inAEqualToEqualityBinaryOp(AEqualToEqualityBinaryOp node) {}
	
	@Override
	public void outAEqualToEqualityBinaryOp(AEqualToEqualityBinaryOp node) {}
	
	@Override
	public void inANotEqualToEqualityBinaryOp(ANotEqualToEqualityBinaryOp node) {}
	
	@Override
	public void outANotEqualToEqualityBinaryOp(ANotEqualToEqualityBinaryOp node) {}
	
	@Override
	public void inALessThanComparativeBinaryOp(ALessThanComparativeBinaryOp node) {}
	
	@Override
	public void outALessThanComparativeBinaryOp(ALessThanComparativeBinaryOp node) {}
	
	@Override
	public void inALessOrEqualComparativeBinaryOp(ALessOrEqualComparativeBinaryOp node) {}
	
	@Override
	public void outALessOrEqualComparativeBinaryOp(ALessOrEqualComparativeBinaryOp node) {}
	
	@Override
	public void inAMoreThanComparativeBinaryOp(AMoreThanComparativeBinaryOp node) {}
	
	@Override
	public void outAMoreThanComparativeBinaryOp(AMoreThanComparativeBinaryOp node) {}
	
	@Override
	public void inAMoreOrEqualComparativeBinaryOp(AMoreOrEqualComparativeBinaryOp node) {}
	
	@Override
	public void outAMoreOrEqualComparativeBinaryOp(AMoreOrEqualComparativeBinaryOp node) {}
	
	@Override
	public void inAPlusAdditiveBinaryOp(APlusAdditiveBinaryOp node) {}
	
	@Override
	public void outAPlusAdditiveBinaryOp(APlusAdditiveBinaryOp node) {}
	
	@Override
	public void inAAndAdditiveBinaryOp(AAndAdditiveBinaryOp node) {}
	
	@Override
	public void outAAndAdditiveBinaryOp(AAndAdditiveBinaryOp node) {}
	
	@Override
	public void inAOrAdditiveBinaryOp(AOrAdditiveBinaryOp node) {}
	
	@Override
	public void outAOrAdditiveBinaryOp(AOrAdditiveBinaryOp node) {}
	
	@Override
	public void inAXorAdditiveBinaryOp(AXorAdditiveBinaryOp node) {}
	
	@Override
	public void outAXorAdditiveBinaryOp(AXorAdditiveBinaryOp node) {}
	
	@Override
	public void inAMinusAdditiveBinaryOp(AMinusAdditiveBinaryOp node) {}
	
	@Override
	public void outAMinusAdditiveBinaryOp(AMinusAdditiveBinaryOp node) {}
	
	@Override
	public void inALeftShiftShiftBinaryOp(ALeftShiftShiftBinaryOp node) {}
	
	@Override
	public void outALeftShiftShiftBinaryOp(ALeftShiftShiftBinaryOp node) {}
	
	@Override
	public void inARightShiftShiftBinaryOp(ARightShiftShiftBinaryOp node) {}
	
	@Override
	public void outARightShiftShiftBinaryOp(ARightShiftShiftBinaryOp node) {}
	
	@Override
	public void inALeftRotateShiftBinaryOp(ALeftRotateShiftBinaryOp node) {}
	
	@Override
	public void outALeftRotateShiftBinaryOp(ALeftRotateShiftBinaryOp node) {}
	
	@Override
	public void inARightRotateShiftBinaryOp(ARightRotateShiftBinaryOp node) {}
	
	@Override
	public void outARightRotateShiftBinaryOp(ARightRotateShiftBinaryOp node) {}
	
	@Override
	public void inAMultiplyMultiplicativeBinaryOp(AMultiplyMultiplicativeBinaryOp node) {}
	
	@Override
	public void outAMultiplyMultiplicativeBinaryOp(AMultiplyMultiplicativeBinaryOp node) {}
	
	@Override
	public void inADivideMultiplicativeBinaryOp(ADivideMultiplicativeBinaryOp node) {}
	
	@Override
	public void outADivideMultiplicativeBinaryOp(ADivideMultiplicativeBinaryOp node) {}
	
	@Override
	public void inARemainderMultiplicativeBinaryOp(ARemainderMultiplicativeBinaryOp node) {}
	
	@Override
	public void outARemainderMultiplicativeBinaryOp(ARemainderMultiplicativeBinaryOp node) {}
	
	@Override
	public void inAMinusUnaryOp(AMinusUnaryOp node) {}
	
	@Override
	public void outAMinusUnaryOp(AMinusUnaryOp node) {}
	
	@Override
	public void inANotUnaryOp(ANotUnaryOp node) {}
	
	@Override
	public void outANotUnaryOp(ANotUnaryOp node) {}
}
