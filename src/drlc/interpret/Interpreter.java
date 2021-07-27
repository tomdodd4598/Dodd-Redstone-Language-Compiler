package drlc.interpret;

import drlc.*;
import drlc.analysis.DepthFirstAdapter;
import drlc.generate.Generator;
import drlc.interpret.scope.*;
import drlc.interpret.type.*;
import drlc.node.*;

public class Interpreter extends DepthFirstAdapter {
	
	public final Generator generator;
	public Program program = null;
	public Scope scope = null;
	
	public Interpreter(Generator generator) {
		this.generator = generator;
	}
	
	public void generate() {
		generator.generate(program, new StringBuilder());
	}
	
	@Override
	public void defaultIn(Node node) {
		System.out.println(node.getClass().getSimpleName().concat(" node in logic not supported!"));
	}
	
	@Override
	public void defaultOut(Node node) {
		System.out.println(node.getClass().getSimpleName().concat(" node out logic not supported!"));
	}
	
	// Productions
	
	@Override
	public void inStart(Start node) {
		program = new Program();
	}
	
	@Override
	public void outStart(Start node) {
		program.trim();
	}
	
	@Override
	public void inAUnit(AUnit node) {
		scope = new Scope(node, scope);
		scope.setExpectingFunctionReturn(false);
		program.addBuiltInMethods(node, scope);
		program.addBuiltInFunctions(node, scope);
	}
	
	@Override
	public void outAUnit(AUnit node) {
		scope = scope.previous;
	}
	
	@Override
	public void inASetupSection(ASetupSection node) {}
	
	@Override
	public void outASetupSection(ASetupSection node) {}
	
	@Override
	public void caseAInputSpecification(AInputSpecification node) {
		node.getSetupPrefix().apply(this);
		node.getSetArgc().apply(this);
		node.getLPar().apply(this);
		// node.getExpression().apply(this);
		program.rootRoutine.argc = Evaluator.tryEvaluate(node, generator, scope, node.getExpression().toString());
		node.getRPar().apply(this);
		node.getSemicolon().apply(this);
	}
	
	@Override
	public void inAMethodDefinitionGeneralSection(AMethodDefinitionGeneralSection node) {}
	
	@Override
	public void outAMethodDefinitionGeneralSection(AMethodDefinitionGeneralSection node) {}
	
	@Override
	public void inAFunctionDefinitionGeneralSection(AFunctionDefinitionGeneralSection node) {}
	
	@Override
	public void outAFunctionDefinitionGeneralSection(AFunctionDefinitionGeneralSection node) {}
	
	@Override
	public void inABasicGeneralSection(ABasicGeneralSection node) {}
	
	@Override
	public void outABasicGeneralSection(ABasicGeneralSection node) {}
	
	@Override
	public void inAConstantDefinitionBasicSection(AConstantDefinitionBasicSection node) {}
	
	@Override
	public void outAConstantDefinitionBasicSection(AConstantDefinitionBasicSection node) {}
	
	@Override
	public void inAVariableDeclarationBasicSection(AVariableDeclarationBasicSection node) {}
	
	@Override
	public void outAVariableDeclarationBasicSection(AVariableDeclarationBasicSection node) {}
	
	@Override
	public void inAVariableModificationBasicSection(AVariableModificationBasicSection node) {}
	
	@Override
	public void outAVariableModificationBasicSection(AVariableModificationBasicSection node) {}
	
	@Override
	public void inAMethodCallBasicSection(AMethodCallBasicSection node) {}
	
	@Override
	public void outAMethodCallBasicSection(AMethodCallBasicSection node) {}
	
	@Override
	public void inAConditionalBasicSection(AConditionalBasicSection node) {}
	
	@Override
	public void outAConditionalBasicSection(AConditionalBasicSection node) {}
	
	@Override
	public void inAIterativeBasicSection(AIterativeBasicSection node) {}
	
	@Override
	public void outAIterativeBasicSection(AIterativeBasicSection node) {}
	
	@Override
	public void caseAMethodDefinition(AMethodDefinition node) {
		scope.methodName = node.getName().getText();
		PParameterList params = node.getParameterList();
		scope.methodArgs = params == null ? 0 : 1 + ((AParameterList) params).getParameterListTail().size();
		
		node.getVoid().apply(this);
		node.getName().apply(this);
		node.getLPar().apply(this);
		if (node.getParameterList() != null) {
			node.getParameterList().apply(this);
		}
		node.getRPar().apply(this);
		node.getLBrace().apply(this);
		for (VariableReferenceInfo param : program.getParamArray(node, scope.previous.methodArgs, false)) {
			scope.addVariable(node, param.variable);
		}
		program.createAndSetMethodRoutine(node, scope);
		scope.setExpectingFunctionReturn(false);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			scope.setExpectingFunctionReturn(false);
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		program.returnToRootRoutine();
	}
	
	@Override
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		scope.functionName = node.getName().getText();
		PParameterList params = node.getParameterList();
		scope.functionArgs = params == null ? 0 : 1 + ((AParameterList) params).getParameterListTail().size();
		
		node.getInt().apply(this);
		node.getName().apply(this);
		node.getLPar().apply(this);
		if (node.getParameterList() != null) {
			node.getParameterList().apply(this);
		}
		node.getRPar().apply(this);
		node.getLBrace().apply(this);
		for (VariableReferenceInfo param : program.getParamArray(node, scope.previous.functionArgs, false)) {
			scope.addVariable(node, param.variable);
		}
		program.createAndSetFunctionRoutine(node, scope);
		scope.setExpectingFunctionReturn(true);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			scope.setExpectingFunctionReturn(true);
			node.getStopStatement().apply(this);
		}
		scope.checkExpectingFunctionReturn(node, false);
		node.getRBrace().apply(this);
		
		program.returnToRootRoutine();
	}
	
	@Override
	public void caseAConstantDefinition(AConstantDefinition node) {
		node.getConst().apply(this);
		node.getInt().apply(this);
		node.getName().apply(this);
		String name = node.getName().getText();
		node.getEquals().apply(this);
		// node.getNumericalExpression().apply(this);
		int value = Evaluator.evaluate(node, generator, scope, node.getExpression().toString());
		generator.checkInteger(node, value);
		scope.addConstant(node, name, value);
		node.getSemicolon().apply(this);
	}
	
	@Override
	public void inANoInitialisationVariableDeclaration(ANoInitialisationVariableDeclaration node) {
		VariableReferenceInfo info = createLvalueVariableInfo(node.getLvalueVariable(), 0, false);
		scope.addVariable(node, info.variable);
		program.currentRoutine().addStackDeclarationAction(node, scope, info.toString());
	}
	
	@Override
	public void outANoInitialisationVariableDeclaration(ANoInitialisationVariableDeclaration node) {}
	
	@Override
	public void caseAWithInitialisationVariableDeclaration(AWithInitialisationVariableDeclaration node) {
		String expression = node.getExpression().toString();
		boolean hasAddressPrefix = Helper.hasAddressPrefix(expression), isValidAddress = false;
		if (hasAddressPrefix) {
			String removedAddressPrefix = Helper.removeAddressPrefix(expression);
			if (scope.variableExists(removedAddressPrefix)) {
				isValidAddress = true;
			}
		}
		node.getInt().apply(this);
		VariableReferenceInfo info = createLvalueVariableInfo(node.getLvalueVariable(), isValidAddress ? 1 : 0, true);
		node.getEquals().apply(this);
		if (isValidAddress) {
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.removeWhitespace(expression));
		}
		else {
			node.getExpression().apply(this);
		}
		program.currentRoutine().pushCurrentRegIdToStack(node);
		program.currentRoutine().addStackInitialisationAction(node, scope, info.toString());
		scope.addVariable(node, info.variable);
		node.getSemicolon().apply(this);
	}
	
	@Override
	public void caseAVariableModification(AVariableModification node) {
		VariableReferenceInfo info = createLvalueVariableInfo(node.getLvalueVariable(), 0, true);
		node.getEquals().apply(this);
		node.getExpression().apply(this);
		program.currentRoutine().pushCurrentRegIdToStack(node);
		program.currentRoutine().addStackVariableAssignmentAction(node, scope, info.toString());
		node.getSemicolon().apply(this);
	}
	
	@Override
	public void caseABuiltInOutMethodCall(ABuiltInOutMethodCall node) {
		node.getOut().apply(this);
		node.getLPar().apply(this);
		node.getExpression().apply(this);
		program.currentRoutine().pushCurrentRegIdToStack(node);
		node.getRPar().apply(this);
		node.getSemicolon().apply(this);
		String name = node.getOut().getText();
		program.currentRoutine().addBuiltInMethodCallAction(node, scope, name);
	}
	
	@Override
	public void inADefinedMethodCall(ADefinedMethodCall node) {}
	
	@Override
	public void outADefinedMethodCall(ADefinedMethodCall node) {
		program.currentRoutine().addMethodSubroutineCallAction(node, scope, node.getName().getText());
	}
	
	@Override
	public void inAConditionalSection(AConditionalSection node) {
		ConditionalSectionInfo info = new ConditionalSectionInfo();
		boolean elseBlock = node.getElseBlock() != null;
		info.setHasElseBlock(node, elseBlock);
		info.setSectionLength(node, 1 + (node.getConditionalMiddleBlock() == null ? 0 : node.getConditionalMiddleBlock().size()) + (elseBlock ? 1 : 0));
		program.currentRoutine().conditionalSectionInfoStack.push(info);
	}
	
	@Override
	public void outAConditionalSection(AConditionalSection node) {
		program.currentRoutine().incrementSectionId();
		if (!program.currentRoutine().currentConditionalSectionInfo(node).getHasElseBlock(node)) {
			program.currentRoutine().addConditionalSectionElseJumpAction(node, scope);
		}
		program.currentRoutine().addConditionalSectionExitJumpActions(node, scope);
		program.currentRoutine().conditionalSectionInfoStack.pop();
	}
	
	@Override
	public void caseAConditionalStartBlock(AConditionalStartBlock node) {
		program.currentRoutine().incrementSectionId();
		
		node.getConditionalStartBlockKeyword().apply(this);
		program.currentRoutine().currentConditionalSectionInfo(node).setExecuteIfCondition(node, node.getConditionalStartBlockKeyword().toString().trim().equals(Global.IF));
		node.getExpression().apply(this);
		
		program.currentRoutine().currentConditionalSectionInfo(node).setElseJumpSectionId(node, program.currentRoutine());
		program.currentRoutine().incrementSectionId();
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		program.currentRoutine().currentConditionalSectionInfo(node).addExitJumpSection(node, program.currentRoutine());
		node.getRBrace().apply(this);
	}
	
	@Override
	public void caseAConditionalMiddleBlock(AConditionalMiddleBlock node) {
		program.currentRoutine().incrementSectionId();
		program.currentRoutine().addConditionalSectionElseJumpAction(node, scope);
		
		node.getConditionalMiddleBlockKeyword().apply(this);
		program.currentRoutine().currentConditionalSectionInfo(node).setExecuteIfCondition(node, node.getConditionalMiddleBlockKeyword().toString().trim().equals(Global.ELSIF));
		node.getExpression().apply(this);
		
		program.currentRoutine().currentConditionalSectionInfo(node).setElseJumpSectionId(node, program.currentRoutine());
		program.currentRoutine().incrementSectionId();
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		program.currentRoutine().currentConditionalSectionInfo(node).addExitJumpSection(node, program.currentRoutine());
		node.getRBrace().apply(this);
	}
	
	@Override
	public void caseAElseBlock(AElseBlock node) {
		program.currentRoutine().incrementSectionId();
		program.currentRoutine().addConditionalSectionElseJumpAction(node, scope);
		
		node.getElse().apply(this);
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			scope.previous.expectingFunctionReturn = false;
			node.getStopStatement().apply(this);
		}
		program.currentRoutine().currentConditionalSectionInfo(node).addExitJumpSection(node, program.currentRoutine());
		node.getRBrace().apply(this);
	}
	
	@Override
	public void caseALoopIterativeBlock(ALoopIterativeBlock node) {
		program.currentRoutine().incrementSectionId();
		IterativeSectionInfo info = new IterativeSectionInfo();
		info.setContinueJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		program.currentRoutine().iterativeSectionInfoStack.push(info);
		
		node.getLoop().apply(this);
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		program.currentRoutine().addIterativeSectionContinueJumpAction(node, scope);
		program.currentRoutine().incrementSectionId();
		info.setBreakJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		program.currentRoutine().finalizeIterativeSectionJumpActions(node, scope);
		program.currentRoutine().iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseAConditionalIterativeBlock(AConditionalIterativeBlock node) {
		IterativeSectionInfo info = new IterativeSectionInfo();
		program.currentRoutine().iterativeSectionInfoStack.push(info);
		
		program.currentRoutine().addIterativeSectionContinueJumpAction(node, scope);
		program.currentRoutine().incrementSectionId();
		info.setBodyJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		program.currentRoutine().incrementSectionId();
		info.setContinueJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		
		node.getConditionalIterativeBlockKeyword().apply(this);
		node.getExpression().apply(this);
		
		program.currentRoutine().addIterativeSectionConditionalBodyJumpAction(node, scope, node.getConditionalIterativeBlockKeyword().toString().trim().equals(Global.WHILE));
		program.currentRoutine().incrementSectionId();
		info.setBreakJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		program.currentRoutine().finalizeIterativeSectionJumpActions(node, scope);
		program.currentRoutine().iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseARepeatConditionalIterativeBlock(ARepeatConditionalIterativeBlock node) {
		IterativeSectionInfo info = new IterativeSectionInfo();
		program.currentRoutine().iterativeSectionInfoStack.push(info);
		
		program.currentRoutine().incrementSectionId();
		info.setBodyJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		
		node.getRepeat().apply(this);
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		program.currentRoutine().incrementSectionId();
		info.setContinueJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		
		node.getConditionalIterativeBlockKeyword().apply(this);
		node.getExpression().apply(this);
		node.getSemicolon().apply(this);
		
		program.currentRoutine().addIterativeSectionConditionalBodyJumpAction(node, scope, node.getConditionalIterativeBlockKeyword().toString().trim().equals(Global.WHILE));
		program.currentRoutine().incrementSectionId();
		info.setBreakJumpTargetSectionId(node, program.currentRoutine().currentSectionId());
		program.currentRoutine().finalizeIterativeSectionJumpActions(node, scope);
		program.currentRoutine().iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void inAReturnStopStatement(AReturnStopStatement node) {}
	
	@Override
	public void outAReturnStopStatement(AReturnStopStatement node) {
		program.currentRoutine().addJumpAction(node, Global.DESTRUCTOR);
	}
	
	@Override
	public void inAContinueStopStatement(AContinueStopStatement node) {}
	
	@Override
	public void outAContinueStopStatement(AContinueStopStatement node) {
		program.currentRoutine().addIterativeSectionContinueJumpAction(node, scope);
	}
	
	@Override
	public void inABreakStopStatement(ABreakStopStatement node) {}
	
	@Override
	public void outABreakStopStatement(ABreakStopStatement node) {
		program.currentRoutine().addIterativeSectionBreakJumpAction(node, scope);
	}
	
	@Override
	public void inAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {}
	
	@Override
	public void outAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {
		program.currentRoutine().addFunctionReturnAction(node, scope);
	}
	
	@Override
	public void caseADead1DeadCode(ADead1DeadCode node) {}
	
	@Override
	public void caseADead2DeadCode(ADead2DeadCode node) {}
	
	@Override
	public void caseADead3DeadCode(ADead3DeadCode node) {}
	
	@Override
	public void caseADead4DeadCode(ADead4DeadCode node) {}
	
	@Override
	public void caseADead5DeadCode(ADead5DeadCode node) {}
	
	@Override
	public void inAPrioritizedExpression(APrioritizedExpression node) {}
	
	@Override
	public void outAPrioritizedExpression(APrioritizedExpression node) {}
	
	@Override
	public void caseABinaryExpression(ABinaryExpression node) {
		Integer value = Evaluator.tryEvaluate(node, generator, scope, node.toString());
		if (value != null) {
			generator.checkInteger(node, value);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.immediateValueString(value));
		}
		else {
			node.getExpression().apply(this);
			program.currentRoutine().pushCurrentRegIdToStack(node);
			node.getBinaryOp().apply(this);
			node.getPrioritizedExpression().apply(this);
			program.currentRoutine().pushCurrentRegIdToStack(node);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addBinaryOpAction(node, scope, node.getBinaryOp().toString().trim());
		}
	}
	
	@Override
	public void inATermPrioritizedExpression(ATermPrioritizedExpression node) {}
	
	@Override
	public void outATermPrioritizedExpression(ATermPrioritizedExpression node) {}
	
	@Override
	public void caseAAddressOfTerm(AAddressOfTerm node) {
		node.getAddressOf().apply(this);
		scope.getVariable(node, node.getRvalueVariable().toString().trim());
		program.currentRoutine().incrementRegId();
		program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.removeWhitespace(node.toString()));
	}
	
	@Override
	public void caseADereferenceTerm(ADereferenceTerm node) {
		Integer value = Evaluator.tryEvaluate(node, generator, scope, node.toString());
		if (value != null) {
			generator.checkInteger(node, value);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.immediateValueString(value));
		}
		else {
			for (TDereference dereference : node.getDereference()) {
				dereference.apply(this);
			}
			node.getRvalueVariable().apply(this);
			program.currentRoutine().pushCurrentRegIdToStack(node);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addDereferenceAction(node, scope, node.getDereference().size());
		}
	}
	
	@Override
	public void caseAUnaryTerm(AUnaryTerm node) {
		Integer value = Evaluator.tryEvaluate(node, generator, scope, node.toString());
		if (value != null) {
			generator.checkInteger(node, value);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.immediateValueString(value));
		}
		else {
			node.getUnaryOp().apply(this);
			node.getTerm().apply(this);
			program.currentRoutine().pushCurrentRegIdToStack(node);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addUnaryOpAction(node, scope, node.getUnaryOp().toString().trim());
		}
	}
	
	@Override
	public void caseABinaryPrioritizedExpression(ABinaryPrioritizedExpression node) {
		Integer value = Evaluator.tryEvaluate(node, generator, scope, node.toString());
		if (value != null) {
			generator.checkInteger(node, value);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.immediateValueString(value));
		}
		else {
			node.getPrioritizedExpression().apply(this);
			program.currentRoutine().pushCurrentRegIdToStack(node);
			node.getPrioritizedBinaryOp().apply(this);
			node.getTerm().apply(this);
			program.currentRoutine().pushCurrentRegIdToStack(node);
			program.currentRoutine().incrementRegId();
			program.currentRoutine().addBinaryOpAction(node, scope, node.getPrioritizedBinaryOp().toString().trim());
		}
	}
	
	@Override
	public void inAValueTerm(AValueTerm node) {}
	
	@Override
	public void outAValueTerm(AValueTerm node) {}
	
	@Override
	public void inAParExpressionTerm(AParExpressionTerm node) {}
	
	@Override
	public void outAParExpressionTerm(AParExpressionTerm node) {}
	
	@Override
	public void caseAIntegerValue(AIntegerValue node) {
		node.getInteger().apply(this);
		program.currentRoutine().incrementRegId();
		int value = Evaluator.evaluate(node, generator, scope, node.toString());
		generator.checkInteger(node, value);
		program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.immediateValueString(value));
	}
	
	@Override
	public void inAVariableValue(AVariableValue node) {}
	
	@Override
	public void outAVariableValue(AVariableValue node) {}
	
	@Override
	public void inAFunctionValue(AFunctionValue node) {}
	
	@Override
	public void outAFunctionValue(AFunctionValue node) {}
	
	@Override
	public void caseABuiltInInFunction(ABuiltInInFunction node) {
		node.getIn().apply(this);
		node.getLPar().apply(this);
		node.getRPar().apply(this);
		program.currentRoutine().incrementRegId();
		program.currentRoutine().addBuiltInFunctionCallAction(node, scope, node.getIn().getText());
	}
	
	// TODO
	@Override
	public void caseABuiltInArgcFunction(ABuiltInArgcFunction node) {
		node.getArgc().apply(this);
		node.getLPar().apply(this);
		node.getRPar().apply(this);
		program.currentRoutine().incrementRegId();
		int argc = program.rootRoutine.argc;
		generator.checkInteger(node, argc);
		program.currentRoutine().addRegisterAssignmentAction(node, scope, Helper.immediateValueString(argc));
	}
	
	// TODO
	@Override
	public void caseABuiltInArgvFunction(ABuiltInArgvFunction node) {
		node.getArgv().apply(this);
		node.getLPar().apply(this);
		node.getExpression().apply(this);
		program.currentRoutine().pushCurrentRegIdToStack(node);
		node.getRPar().apply(this);
		program.currentRoutine().incrementRegId();
		program.currentRoutine().addBuiltInFunctionCallAction(node, scope, node.getArgv().getText());
	}
	
	@Override
	public void inADefinedFunction(ADefinedFunction node) {}
	
	@Override
	public void outADefinedFunction(ADefinedFunction node) {
		program.currentRoutine().incrementRegId();
		program.currentRoutine().addFunctionSubroutineCallAction(node, scope, node.getName().getText());
	}
	
	@Override
	public void caseARvalueVariable(ARvalueVariable node) {
		node.getName().apply(this);
		program.currentRoutine().incrementRegId();
		String name = node.getName().getText();
		Integer value = Evaluator.tryEvaluate(node, generator, scope, name);
		if (value != null) {
			generator.checkInteger(node, value);
			name = Helper.immediateValueString(value);
		}
		program.currentRoutine().addRegisterAssignmentAction(node, scope, name);
	}
	
	@Override
	public void caseALvalueVariable(ALvalueVariable node) {}
	
	public VariableReferenceInfo createLvalueVariableInfo(PLvalueVariable node, int referenceLevelOffset, boolean initialised) {
		ALvalueVariable variableNode = (ALvalueVariable) node;
		VariableReferenceInfo info = new VariableReferenceInfo(new Variable(variableNode.getName().getText(), variableNode.getDereference().size() + referenceLevelOffset, initialised));
		info.dereferenceLevel = info.variable.baseReferenceLevel - referenceLevelOffset;
		return info;
	}
	
	@Override
	public void caseAArgumentList(AArgumentList node) {
		node.getExpression().apply(this);
		program.currentRoutine().pushCurrentRegIdToStack(node);
		for (PArgumentListTail tail : node.getArgumentListTail()) {
			tail.apply(this);
		}
	}
	
	@Override
	public void caseAArgumentListTail(AArgumentListTail node) {
		node.getComma().apply(this);
		node.getExpression().apply(this);
		program.currentRoutine().pushCurrentRegIdToStack(node);
	}
	
	@Override
	public void inAParameterList(AParameterList node) {
		program.addParam(node, createLvalueVariableInfo(node.getLvalueVariable(), 0, true));
	}
	
	@Override
	public void outAParameterList(AParameterList node) {}
	
	@Override
	public void inAParameterListTail(AParameterListTail node) {
		program.addParam(node, createLvalueVariableInfo(node.getLvalueVariable(), 0, true));
	}
	
	@Override
	public void outAParameterListTail(AParameterListTail node) {}
	
	@Override
	public void inAPlusUnaryOp(APlusUnaryOp node) {}
	
	@Override
	public void outAPlusUnaryOp(APlusUnaryOp node) {}
	
	@Override
	public void inAMinusUnaryOp(AMinusUnaryOp node) {}
	
	@Override
	public void outAMinusUnaryOp(AMinusUnaryOp node) {}
	
	@Override
	public void inAComplementUnaryOp(AComplementUnaryOp node) {}
	
	@Override
	public void outAComplementUnaryOp(AComplementUnaryOp node) {}
	
	@Override
	public void inAToBoolUnaryOp(AToBoolUnaryOp node) {}
	
	@Override
	public void outAToBoolUnaryOp(AToBoolUnaryOp node) {}
	
	@Override
	public void inANotUnaryOp(ANotUnaryOp node) {}
	
	@Override
	public void outANotUnaryOp(ANotUnaryOp node) {}
	
	@Override
	public void inAPlusBinaryOp(APlusBinaryOp node) {}
	
	@Override
	public void outAPlusBinaryOp(APlusBinaryOp node) {}
	
	@Override
	public void inAAndBinaryOp(AAndBinaryOp node) {}
	
	@Override
	public void outAAndBinaryOp(AAndBinaryOp node) {}
	
	@Override
	public void inAOrBinaryOp(AOrBinaryOp node) {}
	
	@Override
	public void outAOrBinaryOp(AOrBinaryOp node) {}
	
	@Override
	public void inAXorBinaryOp(AXorBinaryOp node) {}
	
	@Override
	public void outAXorBinaryOp(AXorBinaryOp node) {}
	
	@Override
	public void inAMinusBinaryOp(AMinusBinaryOp node) {}
	
	@Override
	public void outAMinusBinaryOp(AMinusBinaryOp node) {}
	
	@Override
	public void inALeftShiftPrioritizedBinaryOp(ALeftShiftPrioritizedBinaryOp node) {}
	
	@Override
	public void outALeftShiftPrioritizedBinaryOp(ALeftShiftPrioritizedBinaryOp node) {}
	
	@Override
	public void inARightShiftPrioritizedBinaryOp(ARightShiftPrioritizedBinaryOp node) {}
	
	@Override
	public void outARightShiftPrioritizedBinaryOp(ARightShiftPrioritizedBinaryOp node) {}
	
	@Override
	public void inAMultiplyPrioritizedBinaryOp(AMultiplyPrioritizedBinaryOp node) {}
	
	@Override
	public void outAMultiplyPrioritizedBinaryOp(AMultiplyPrioritizedBinaryOp node) {}
	
	@Override
	public void inAEqualToPrioritizedBinaryOp(AEqualToPrioritizedBinaryOp node) {}
	
	@Override
	public void outAEqualToPrioritizedBinaryOp(AEqualToPrioritizedBinaryOp node) {}
	
	@Override
	public void inADividePrioritizedBinaryOp(ADividePrioritizedBinaryOp node) {}
	
	@Override
	public void outADividePrioritizedBinaryOp(ADividePrioritizedBinaryOp node) {}
	
	@Override
	public void inARemainderPrioritizedBinaryOp(ARemainderPrioritizedBinaryOp node) {}
	
	@Override
	public void outARemainderPrioritizedBinaryOp(ARemainderPrioritizedBinaryOp node) {}
	
	@Override
	public void inANotEqualToPrioritizedBinaryOp(ANotEqualToPrioritizedBinaryOp node) {}
	
	@Override
	public void outANotEqualToPrioritizedBinaryOp(ANotEqualToPrioritizedBinaryOp node) {}
	
	@Override
	public void inALessThanPrioritizedBinaryOp(ALessThanPrioritizedBinaryOp node) {}
	
	@Override
	public void outALessThanPrioritizedBinaryOp(ALessThanPrioritizedBinaryOp node) {}
	
	@Override
	public void inALessOrEqualPrioritizedBinaryOp(ALessOrEqualPrioritizedBinaryOp node) {}
	
	@Override
	public void outALessOrEqualPrioritizedBinaryOp(ALessOrEqualPrioritizedBinaryOp node) {}
	
	@Override
	public void inAMoreThanPrioritizedBinaryOp(AMoreThanPrioritizedBinaryOp node) {}
	
	@Override
	public void outAMoreThanPrioritizedBinaryOp(AMoreThanPrioritizedBinaryOp node) {}
	
	@Override
	public void inAMoreOrEqualPrioritizedBinaryOp(AMoreOrEqualPrioritizedBinaryOp node) {}
	
	@Override
	public void outAMoreOrEqualPrioritizedBinaryOp(AMoreOrEqualPrioritizedBinaryOp node) {}
	
	// Tokens
	
	@Override
	public void caseTLBrace(TLBrace node) {
		scope = new Scope(node, scope);
	}
	
	@Override
	public void caseTRBrace(TRBrace node) {
		scope = scope.previous;
	}
}
