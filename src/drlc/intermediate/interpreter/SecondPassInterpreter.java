package drlc.intermediate.interpreter;

import drlc.*;
import drlc.intermediate.component.Variable;
import drlc.intermediate.component.expression.*;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.routine.Routine;
import drlc.node.*;

public class SecondPassInterpreter extends AbstractInterpreter {
	
	public SecondPassInterpreter(Generator generator) {
		super(generator);
	}
	
	// Productions
	
	@Override
	public void inStart(Start node) {
		scope = generator.program.rootScope;
		scope.setExpectingFunctionReturn(false);
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
	public void inADirective(ADirective node) {}
	
	@Override
	public void outADirective(ADirective node) {}
	
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
	public void inAConditionalSectionBasicSection(AConditionalSectionBasicSection node) {
		ConditionalSectionInfo info = new ConditionalSectionInfo();
		generator.program.currentRoutine().conditionalSectionInfoStack.push(info);
	}
	
	@Override
	public void outAConditionalSectionBasicSection(AConditionalSectionBasicSection node) {
		Routine routine = generator.program.currentRoutine();
		routine.incrementSectionId();
		if (!routine.currentConditionalSectionInfo(node).getHasElseSection(node)) {
			routine.addConditionalSectionElseJumpAction(node);
		}
		routine.addConditionalSectionExitJumpActions(node);
		routine.conditionalSectionInfoStack.pop();
	}
	
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
		boolean isVoid = returnTypeInfo.isVoid(node);
		
		node.getLBrace().apply(this);
		for (DeclaratorInfo paramInfo : generator.program.getParamArray(node, argc, false)) {
			scope.addVariable(node, paramInfo.variable);
		}
		generator.program.defineFunctionAndSetRoutine(node, scope, node.getName().getText(), modifierInfo, argc, returnTypeInfo);
		scope.setExpectingFunctionReturn(!isVoid);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			scope.setExpectingFunctionReturn(!isVoid);
			node.getStopStatement().apply(this);
		}
		scope.checkExpectingFunctionReturn(node, false);
		
		node.getRBrace().apply(this);
		generator.program.returnToRootRoutine();
	}
	
	@Override
	public void caseAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {
		for (TVariableModifier modifier : node.getVariableModifier()) {
			modifier.apply(this);
		}
		VariableModifierInfo modifierInfo = getVariableModifierInfo(node.getVariableModifier());
		
		// TODO: Allow size > 1
		node.getVar().apply(this);
		DeclaratorInfo declaratorInfo = createDeclaratorInfo(node, node.getDeclarator(), modifierInfo, 1);
		scope.addVariable(node, declaratorInfo.variable);
		
		generator.program.currentRoutine().addStackDeclarationAction(node, declaratorInfo);
	}
	
	@Override
	public void caseAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {
		for (TVariableModifier modifier : node.getVariableModifier()) {
			modifier.apply(this);
		}
		
		// TODO: Allow size > 1
		node.getVar().apply(this);
		DeclaratorInfo declaratorInfo = createDeclaratorInfo(node, node.getDeclarator(), getVariableModifierInfo(node.getVariableModifier()), 1);
		scope.addVariable(node, declaratorInfo.variable);
		
		node.getEquals().apply(this);
		node.getInitializationExpression().apply(this);
		
		Routine routine = generator.program.currentRoutine();
		TypeInfo lastInfo = routine.getLastExpressionInfo(node).getTypeInfo();
		if (!lastInfo.canImplicitCastTo(node, generator, declaratorInfo.getTypeInfo())) {
			throw new IllegalArgumentException(String.format("Attempted to use expression of type \"%s\" to initialize variable of incompatible type \"%s\"! %s", lastInfo, declaratorInfo.typeInfo, node));
		}
		
		routine.pushCurrentRegIdToStack(node);
		routine.addStackInitializationAction(node, declaratorInfo);
	}
	
	@Override
	public void inABasicInitializationExpression(ABasicInitializationExpression node) {}
	
	@Override
	public void outABasicInitializationExpression(ABasicInitializationExpression node) {}
	
	@Override
	public void caseABasicExpressionStatement(ABasicExpressionStatement node) {
		node.getExpressionRvalue().apply(this);
		
		Routine routine = generator.program.currentRoutine();
		routine.pushCurrentRegIdToStack(node);
		routine.incrementRegId();
		routine.addStackAssignmentAction(node);
	}
	
	@Override
	public void caseAAssignmentExpressionStatement(AAssignmentExpressionStatement node) {
		node.getAssignmentOp().apply(this);
		String op = node.getAssignmentOp().toString().trim();
		
		node.getExpressionRvalue().apply(this);
		
		Routine routine = generator.program.currentRoutine();
		routine.pushCurrentRegIdToStack(node);
		ExpressionInfo rvalueExpressionInfo = routine.getLastExpressionInfo(node);
		
		node.getExpressionLvalue().apply(this);
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, true);
		lvalueParseInfo.checkIsValid();
		
		ExpressionInfo lvalueExpressionInfo = routine.getLastExpressionInfo(node);
		TypeInfo lvalueTypeInfo = lvalueExpressionInfo.getTypeInfo();
		lvalueTypeInfo = lvalueTypeInfo.copy(node, lvalueTypeInfo.referenceLevel - lvalueParseInfo.dereferenceLevel);
		lvalueExpressionInfo.setTypeInfo(lvalueTypeInfo);
		
		if (!lvalueTypeInfo.isAddressable()) {
			String lvalueString = Helpers.removeWhitespace(node.getExpressionLvalue().toString());
			throw new IllegalArgumentException(String.format("Attempted to assign expression to non-modifiable lvalue \"%s\"! %s", lvalueString, node));
		}
		
		TypeInfo rvalueTypeInfo = rvalueExpressionInfo.getTypeInfo();
		if (!rvalueTypeInfo.canImplicitCastTo(node, generator, lvalueTypeInfo)) {
			throw new IllegalArgumentException(String.format("Attempted to assign expression of type \"%s\" to lvalue of incompatible type \"%s\"! %s", rvalueTypeInfo, lvalueTypeInfo, node));
		}
		
		if (op.equals(Global.EQUALS)) {
			routine.addStackLvalueAssignmentAction(node, scope, lvalueParseInfo);
		}
		else {
			op = op.substring(0, op.length() - Global.EQUALS.length());
			routine.addStackLvalueAssignmentOperationAction(node, scope, op, lvalueParseInfo, lvalueExpressionInfo, rvalueExpressionInfo);
		}
	}
	
	@Override
	public void caseAConditionalSection(AConditionalSection node) {
		Routine routine = generator.program.currentRoutine();
		routine.incrementSectionId();
		routine.addConditionalSectionElseJumpAction(node);
		
		ConditionalSectionInfo conditionalSectionInfo = routine.currentConditionalSectionInfo(node);
		conditionalSectionInfo.sectionStart = false;
		conditionalSectionInfo.incrementSectionLength(node);
		
		node.getConditionalBranchSectionKeyword().apply(this);
		conditionalSectionInfo.setExecuteIfCondition(node, node.getConditionalBranchSectionKeyword().toString().trim().equals(Global.IF));
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsBool(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s", routine.getLastExpressionInfo(node).getTypeInfo());
		
		conditionalSectionInfo.setElseJumpSectionId(node, routine);
		routine.incrementSectionId();
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		conditionalSectionInfo.addExitJumpSection(node, routine);
		node.getRBrace().apply(this);
		
		if (node.getElseSection() != null) {
			node.getElseSection().apply(this);
		}
	}
	
	@Override
	public void caseAExcludingBranchElseSection(AExcludingBranchElseSection node) {
		Routine routine = generator.program.currentRoutine();
		routine.incrementSectionId();
		routine.addConditionalSectionElseJumpAction(node);
		
		ConditionalSectionInfo conditionalSectionInfo = routine.currentConditionalSectionInfo(node);
		conditionalSectionInfo.incrementSectionLength(node);
		
		node.getElse().apply(this);
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			scope.previous.expectingFunctionReturn = false;
			node.getStopStatement().apply(this);
		}
		
		conditionalSectionInfo.addExitJumpSection(node, routine);
		node.getRBrace().apply(this);
		
		conditionalSectionInfo.setHasElseSection(node, true);
	}
	
	@Override
	public void inAIncludingBranchElseSection(AIncludingBranchElseSection node) {}
	
	@Override
	public void outAIncludingBranchElseSection(AIncludingBranchElseSection node) {}
	
	@Override
	public void caseALoopIterativeSection(ALoopIterativeSection node) {
		Routine routine = generator.program.currentRoutine();
		routine.incrementSectionId();
		IterativeSectionInfo info = new IterativeSectionInfo();
		info.setContinueJumpTargetSectionId(node, routine.currentSectionId());
		routine.iterativeSectionInfoStack.push(info);
		
		node.getLoop().apply(this);
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		routine.addIterativeSectionContinueJumpAction(node);
		routine.incrementSectionId();
		info.setBreakJumpTargetSectionId(node, routine.currentSectionId());
		routine.finalizeIterativeSectionJumpActions(node);
		routine.iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseAConditionalIterativeSection(AConditionalIterativeSection node) {
		Routine routine = generator.program.currentRoutine();
		IterativeSectionInfo info = new IterativeSectionInfo();
		routine.iterativeSectionInfoStack.push(info);
		
		routine.addIterativeSectionContinueJumpAction(node);
		routine.incrementSectionId();
		info.setBodyJumpTargetSectionId(node, routine.currentSectionId());
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		routine.incrementSectionId();
		info.setContinueJumpTargetSectionId(node, routine.currentSectionId());
		
		node.getConditionalIterativeSectionKeyword().apply(this);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsBool(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s", routine.getLastExpressionInfo(node).getTypeInfo());
		
		routine.addIterativeSectionConditionalBodyJumpAction(node, node.getConditionalIterativeSectionKeyword().toString().trim().equals(Global.WHILE));
		routine.incrementSectionId();
		info.setBreakJumpTargetSectionId(node, routine.currentSectionId());
		routine.finalizeIterativeSectionJumpActions(node);
		routine.iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseADoConditionalIterativeSection(ADoConditionalIterativeSection node) {
		Routine routine = generator.program.currentRoutine();
		IterativeSectionInfo info = new IterativeSectionInfo();
		routine.iterativeSectionInfoStack.push(info);
		
		routine.incrementSectionId();
		info.setBodyJumpTargetSectionId(node, routine.currentSectionId());
		
		node.getDo().apply(this);
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		routine.incrementSectionId();
		info.setContinueJumpTargetSectionId(node, routine.currentSectionId());
		
		node.getConditionalIterativeSectionKeyword().apply(this);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsBool(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s", routine.getLastExpressionInfo(node).getTypeInfo());
		
		routine.addIterativeSectionConditionalBodyJumpAction(node, node.getConditionalIterativeSectionKeyword().toString().trim().equals(Global.WHILE));
		routine.incrementSectionId();
		info.setBreakJumpTargetSectionId(node, routine.currentSectionId());
		routine.finalizeIterativeSectionJumpActions(node);
		routine.iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void inAGotoStatement(AGotoStatement node) {
		generator.program.currentRoutine().addGotoAction(node, node.getName().getText());
	}
	
	@Override
	public void outAGotoStatement(AGotoStatement node) {}
	
	@Override
	public void inASectionLabel(ASectionLabel node) {
		Routine routine = generator.program.currentRoutine();
		routine.incrementSectionId();
		routine.mapStatementLabel(node, node.getName().getText());
	}
	
	@Override
	public void outASectionLabel(ASectionLabel node) {}
	
	@Override
	public void caseAExitStopStatement(AExitStopStatement node) {
		node.getExit().apply(this);
		generator.program.currentRoutine().addExitAction(node);
	}
	
	@Override
	public void caseAReturnStopStatement(AReturnStopStatement node) {
		node.getReturn().apply(this);
		generator.program.currentRoutine().addReturnAction(node, Global.DESTRUCTOR);
	}
	
	@Override
	public void caseAContinueStopStatement(AContinueStopStatement node) {
		node.getContinue().apply(this);
		generator.program.currentRoutine().addIterativeSectionContinueJumpAction(node);
	}
	
	@Override
	public void caseABreakStopStatement(ABreakStopStatement node) {
		node.getBreak().apply(this);
		generator.program.currentRoutine().addIterativeSectionBreakJumpAction(node);
	}
	
	@Override
	public void inAExitExpressionStopStatement(AExitExpressionStopStatement node) {}
	
	@Override
	public void outAExitExpressionStopStatement(AExitExpressionStopStatement node) {
		generator.program.currentRoutine().addExitValueAction(node);
	}
	
	@Override
	public void caseAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {
		Routine routine = generator.program.currentRoutine();
		if (routine.isRootRoutine()) {
			throw new IllegalArgumentException(String.format("Root routine can not return a value! Use an exit value statement!"));
		}
		else {
			node.getReturn().apply(this);
			node.getExpressionRvalue().apply(this);
			
			TypeInfo lastTypeInfo = routine.getLastExpressionInfo(node).getTypeInfo();
			TypeInfo returnTypeInfo = routine.getReturnTypeInfo();
			if (!lastTypeInfo.canImplicitCastTo(node, generator, returnTypeInfo)) {
				throw new IllegalArgumentException(String.format("Attempted to use expression of type \"%s\" as return value for function of incompatible type \"%s\"! %s", lastTypeInfo, returnTypeInfo, node));
			}
			
			routine.addFunctionReturnValueAction(node, scope);
		}
	}
	
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
	public void inAExpressionLvalue(AExpressionLvalue node) {
		Routine routine = generator.program.currentRoutine();
		routine.setLvalueParseInfo(node, new LvalueParseInfo(node.getExpression6()));
		routine.expressionInfoStack.push(new LvalueExpressionInfo());
	}
	
	@Override
	public void outAExpressionLvalue(AExpressionLvalue node) {
		Routine routine = generator.program.currentRoutine();
		routine.setLastExpressionInfo(node, routine.expressionInfoStack.pop());
	}
	
	@Override
	public void inAExpressionRvalue(AExpressionRvalue node) {
		generator.program.currentRoutine().expressionInfoStack.push(new RvalueExpressionInfo());
	}
	
	@Override
	public void outAExpressionRvalue(AExpressionRvalue node) {
		Routine routine = generator.program.currentRoutine();
		routine.setLastExpressionInfo(node, routine.expressionInfoStack.pop());
	}
	
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
		Routine routine = generator.program.currentRoutine();
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, false);
		if (lvalueParseInfo != null) {
			lvalueParseInfo.setVariable(null);
		}
		
		left.apply(this);
		
		routine.pushCurrentRegIdToStack(node);
		ExpressionInfo leftInfo = routine.currentExpressionInfo(node).copy(node);
		
		right.apply(this);
		routine.pushCurrentRegIdToStack(node);
		
		routine.incrementRegId();
		ExpressionInfo rightInfo = routine.expressionInfoStack.pop();
		routine.expressionInfoStack.push(leftInfo);
		routine.addBinaryOpAction(node, scope, op.trim(), rightInfo);
	}
	
	@Override
	public void inAPrioritizedExpression6(APrioritizedExpression6 node) {}
	
	@Override
	public void outAPrioritizedExpression6(APrioritizedExpression6 node) {}
	
	@Override
	public void caseAUnaryExpression6(AUnaryExpression6 node) {
		Routine routine = generator.program.currentRoutine();
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, false);
		if (lvalueParseInfo != null) {
			lvalueParseInfo.setVariable(null);
		}
		
		node.getUnaryOp().apply(this);
		node.getExpression6().apply(this);
		
		routine.pushCurrentRegIdToStack(node);
		routine.incrementRegId();
		routine.addUnaryOpAction(node, scope, node.getUnaryOp().toString().trim());
	}
	
	@Override
	public void caseADereferenceExpression6(ADereferenceExpression6 node) {
		node.getMultiply().apply(this);
		node.getExpression6().apply(this);
		
		Routine routine = generator.program.currentRoutine();
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, false);
		if (lvalueParseInfo == null || lvalueParseInfo.variableIsNull) {
			routine.currentExpressionInfo(node).decrementReferenceLevel(node);
			routine.pushCurrentRegIdToStack(node);
			routine.incrementRegId();
			routine.addDereferenceAction(node);
		}
		else {
			++lvalueParseInfo.dereferenceLevel;
		}
	}
	
	@Override
	public void caseAAddressOfExpression6(AAddressOfExpression6 node) {
		Routine routine = generator.program.currentRoutine();
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, false);
		if (lvalueParseInfo != null) {
			lvalueParseInfo.setVariable(null);
		}
		
		node.getAnd().apply(this);
		String variableName = node.getName().getText();
		Variable variable = scope.getVariable(node, variableName);
		TypeInfo typeInfo = variable.typeInfo;
		if (!typeInfo.isAddressable()) {
			throw new IllegalArgumentException(String.format("Can not get address of non-addressable variable \"%s\"! %s", variableName, node));
		}
		
		routine.currentExpressionInfo(node).setTypeInfo(typeInfo);
		routine.currentExpressionInfo(node).incrementReferenceLevel(node);
		routine.incrementRegId();
		routine.addAddressOfRegisterAssignmentAction(node, variable);
	}
	
	@Override
	public void inAPrioritizedExpression7(APrioritizedExpression7 node) {}
	
	@Override
	public void outAPrioritizedExpression7(APrioritizedExpression7 node) {}
	
	@Override
	public void caseAFunctionExpression7(AFunctionExpression7 node) {
		Routine routine = generator.program.currentRoutine();
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, false);
		if (lvalueParseInfo != null) {
			lvalueParseInfo.setVariable(null);
		}
		
		routine.expressionListInfoStack.push(new FunctionCallInfo());
		
		FunctionTypeInfo typeInfo = tryGetDirectFunctionTypeInfo(node.getExpression7());
		if (typeInfo == null) {
			node.getExpression7().apply(this);
			TypeInfo currentTypeInfo = routine.currentExpressionInfo(node).getTypeInfo();
			if (currentTypeInfo.isFunction()) {
				typeInfo = (FunctionTypeInfo) currentTypeInfo;
			}
			else {
				throw new IllegalArgumentException(String.format("Attempted to use expression of incompatible type \"%s\" as a function type! %s", typeInfo, node));
			}
		}
		ExpressionListInfo expressionListInfo = routine.currentExpressionListInfo(node);
		if (!expressionListInfo.isFunctionCallInfo()) {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered function expression without function call info! %s", node));
		}
		
		((FunctionCallInfo) expressionListInfo).typeInfo = typeInfo;
		routine.pushCurrentRegIdToStack(node);
		
		node.getParExpressionList().apply(this);
		
		routine.incrementRegId();
		routine.addFunctionAction(node);
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
		Routine routine = generator.program.currentRoutine();
		ExpressionListInfo expressionListInfo = routine.currentExpressionListInfo(node);
		node.getExpressionRvalue().apply(this);
		
		TypeInfo lastTypeInfo = routine.getLastExpressionInfo(node).getTypeInfo();
		TypeInfo nextTypeInfo = expressionListInfo.getNextType(node);
		if (!lastTypeInfo.canImplicitCastTo(node, generator, nextTypeInfo)) {
			expressionListInfo.typeCastError(node, lastTypeInfo, nextTypeInfo);
		}
		
		routine.pushCurrentRegIdToStack(node);
		for (PExpressionListTail tail : node.getExpressionListTail()) {
			tail.apply(this);
		}
		if (node.getComma() != null) {
			node.getComma().apply(this);
		}
	}
	
	@Override
	public void caseAExpressionListTail(AExpressionListTail node) {
		Routine routine = generator.program.currentRoutine();
		ExpressionListInfo expressionListInfo = routine.currentExpressionListInfo(node);
		node.getComma().apply(this);
		node.getExpressionRvalue().apply(this);
		
		TypeInfo lastTypeInfo = routine.getLastExpressionInfo(node).getTypeInfo();
		TypeInfo nextTypeInfo = expressionListInfo.getNextType(node);
		if (!lastTypeInfo.canImplicitCastTo(node, generator, nextTypeInfo)) {
			expressionListInfo.typeCastError(node, lastTypeInfo, nextTypeInfo);
		}
		
		routine.pushCurrentRegIdToStack(node);
	}
	
	@Override
	public void inAValueExpression8(AValueExpression8 node) {}
	
	@Override
	public void outAValueExpression8(AValueExpression8 node) {}
	
	@Override
	public void caseAVariableExpression8(AVariableExpression8 node) {
		node.getName().apply(this);
		String variableName = node.getName().getText();
		Variable variable = scope.getVariable(node, variableName);
		
		Routine routine = generator.program.currentRoutine();
		routine.currentExpressionInfo(node).setTypeInfo(variable.typeInfo);
		LvalueParseInfo lvalueParseInfo = routine.getLvalueParseInfo(node, false);
		if (lvalueParseInfo == null || lvalueParseInfo.variableIsNull) {
			routine.incrementRegId();
			routine.addRegisterAssignmentAction(node, variable);
		}
		else {
			lvalueParseInfo.setVariable(variable);
		}
	}
	
	@Override
	public void inAParenthesesExpression8(AParenthesesExpression8 node) {}
	
	@Override
	public void outAParenthesesExpression8(AParenthesesExpression8 node) {}
	
	@Override
	public void caseABoolValue(ABoolValue node) {}
	
	@Override
	public void caseAIntValue(AIntValue node) {}
	
	@Override
	public void caseANatValue(ANatValue node) {}
	
	@Override
	public void caseACharValue(ACharValue node) {}
	
	@Override
	public void caseANullValue(ANullValue node) {}
	
	@Override
	public void caseASizeofValue(ASizeofValue node) {}
	
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
