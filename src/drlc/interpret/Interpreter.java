package drlc.interpret;

import java.util.*;
import java.util.stream.Collectors;

import drlc.*;
import drlc.analysis.DepthFirstAdapter;
import drlc.generate.Generator;
import drlc.interpret.component.Variable;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.expression.*;
import drlc.interpret.component.info.type.*;
import drlc.interpret.routine.Routine;
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
	
	public Routine routine() {
		return program.currentRoutine();
	}
	
	public void checkLastExpressionCast(Node node, TypeInfo targetTypeInfo, String error) {
		TypeInfo expressionTypeInfo = routine().getLastExpressionInfo(node).getTypeInfo();
		if (!expressionTypeInfo.canCastTo(node, generator, targetTypeInfo)) {
			throw new IllegalArgumentException(String.format(error, expressionTypeInfo, targetTypeInfo, node));
		}
	}
	
	public void checkLastExpressionIsInt(Node node, String error) {
		TypeInfo expressionTypeInfo = routine().getLastExpressionInfo(node).getTypeInfo();
		if (!expressionTypeInfo.equals(Global.INT_TYPE_INFO)) {
			throw new IllegalArgumentException(String.format(error, expressionTypeInfo, Global.INT_TYPE_INFO, node));
		}
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
		program = new Program(generator);
	}
	
	@Override
	public void outStart(Start node) {
		program.fixUp();
	}
	
	@Override
	public void inAUnit(AUnit node) {
		scope = new Scope(node, program, scope);
		scope.setExpectingFunctionReturn(false);
		program.rootScope = scope;
		
		generator.addBuiltInTypes(node, program);
		generator.addBuiltInConstants(node, program);
		generator.addBuiltInVariables(node, program);
		generator.addBuiltInFunctions(node, program);
	}
	
	@Override
	public void outAUnit(AUnit node) {
		scope = scope.previous;
	}
	
	@Override
	public void inASetup(ASetup node) {}
	
	@Override
	public void outASetup(ASetup node) {}
	
	@Override
	public void inAProgram(AProgram node) {}
	
	@Override
	public void outAProgram(AProgram node) {}
	
	@Override
	public void caseADirectiveFunctionSetupSection(ADirectiveFunctionSetupSection node) {
		generator.handleDirectiveCall(node, program, node.getName().getText(), getArgumentEvaluationInfoList(node.getExpressionList()));
	}
	
	@Override
	public void inAFunctionDeclarationProgramSection(AFunctionDeclarationProgramSection node) {}
	
	@Override
	public void outAFunctionDeclarationProgramSection(AFunctionDeclarationProgramSection node) {}
	
	@Override
	public void inAFunctionDefinitionProgramSection(AFunctionDefinitionProgramSection node) {}
	
	@Override
	public void outAFunctionDefinitionProgramSection(AFunctionDefinitionProgramSection node) {}
	
	@Override
	public void inABasicSectionProgramSection(ABasicSectionProgramSection node) {}
	
	@Override
	public void outABasicSectionProgramSection(ABasicSectionProgramSection node) {}
	
	@Override
	public void inAEmptyStatementBasicSection(AEmptyStatementBasicSection node) {}
	
	@Override
	public void outAEmptyStatementBasicSection(AEmptyStatementBasicSection node) {}
	
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
	public void caseAFunctionDeclaration(AFunctionDeclaration node) {
		node.getFun().apply(this);
		node.getName().apply(this);
		node.getParParameterList().apply(this);
		int argc = getParameterListLength(node.getParParameterList());
		
		if (node.getReturnType() != null) {
			node.getReturnType().apply(this);
		}
		TypeInfo returnTypeInfo = createReturnTypeInfo(node, scope, node.getReturnType(), 0);
		program.declareFunction(node, scope, node.getName().getText(), argc, returnTypeInfo);
		
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		for (TModifier modifier : node.getModifier()) {
			modifier.apply(this);
		}
		FunctionModifierInfo modifierInfo = getFunctionModifierInfo(node.getModifier());
		
		node.getFun().apply(this);
		node.getName().apply(this);
		node.getParParameterList().apply(this);
		int argc = getParameterListLength(node.getParParameterList());
		
		if (node.getReturnType() != null) {
			node.getReturnType().apply(this);
		}
		TypeInfo returnTypeInfo = createReturnTypeInfo(node, scope, node.getReturnType(), 0);
		boolean isVoid = returnTypeInfo.isVoid(node, generator);
		
		node.getLBrace().apply(this);
		for (DeclaratorInfo param : program.getParamArray(node, argc, false)) {
			scope.addVariable(node, param.variable);
		}
		program.createAndSetFunctionRoutine(node, scope, node.getName().getText(), modifierInfo, argc, returnTypeInfo);
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
		program.returnToRootRoutine();
	}
	
	public FunctionModifierInfo getFunctionModifierInfo(List<TModifier> tModifierList) {
		Set<String> modifiers = tModifierList.stream().map(modifier -> modifier.getText()).collect(Collectors.toSet());
		return new FunctionModifierInfo(modifiers.contains(Global.STACK), modifiers.contains(Global.STATIC));
	}
	
	@Override
	public void inAEmptyStatement(AEmptyStatement node) {}
	
	@Override
	public void outAEmptyStatement(AEmptyStatement node) {}
	
	@Override
	public void caseAExcludingInitializationVariableDeclaration(AExcludingInitializationVariableDeclaration node) {
		for (TModifier modifier : node.getModifier()) {
			modifier.apply(this);
		}
		VariableModifierInfo modifierInfo = getVariableModifierInfo(node.getModifier());
		
		// TODO: Allow size > 1 for arrays/structs
		node.getVar().apply(this);
		DeclaratorInfo info = createDeclaratorInfo(node, node.getDeclarator(), modifierInfo, node.getType(), 1);
		scope.addVariable(node, info.variable);
		
		node.getType().apply(this);
		routine().addStackDeclarationAction(node, scope, info);
		
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseAIncludingInitializationVariableDeclaration(AIncludingInitializationVariableDeclaration node) {
		for (TModifier modifier : node.getModifier()) {
			modifier.apply(this);
		}
		VariableModifierInfo modifierInfo = getVariableModifierInfo(node.getModifier());
		modifierInfo.init_ = true;
		
		// TODO: Allow size > 1 for arrays/structs
		node.getVar().apply(this);
		DeclaratorInfo info = createDeclaratorInfo(node, node.getDeclarator(), modifierInfo, node.getType(), 1);
		scope.addVariable(node, info.variable);
		
		node.getType().apply(this);
		node.getEquals().apply(this);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionCast(node, info.typeInfo, "Attempted to use expression of type \"%s\" to initialize variable of incompatible type \"%s\"! %s");
		
		routine().pushCurrentRegIdToStack(node);
		routine().addStackInitializationAction(node, scope, info);
		
		node.getSeparator().apply(this);
	}
	
	public VariableModifierInfo getVariableModifierInfo(List<TModifier> tModifierList) {
		Set<String> modifiers = tModifierList.stream().map(modifier -> modifier.getText()).collect(Collectors.toSet());
		return new VariableModifierInfo(modifiers.contains(Global.INIT), modifiers.contains(Global.STACK), modifiers.contains(Global.STATIC));
	}
	
	@Override
	public void caseABasicExpressionStatement(ABasicExpressionStatement node) {
		node.getExpressionRvalue().apply(this);
		
		routine().pushCurrentRegIdToStack(node);
		routine().incrementRegId();
		routine().addStackAssignmentAction(node, scope);
		
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseAAssignmentExpressionStatement(AAssignmentExpressionStatement node) {
		node.getAssignmentOp().apply(this);
		String op = node.getAssignmentOp().toString().trim();
		
		node.getExpressionRvalue().apply(this);
		routine().pushCurrentRegIdToStack(node);
		ExpressionInfo<?> prevInfo = routine().getLastExpressionInfo(node);
		
		node.getExpressionLvalue().apply(this);
		LvalueParseInfo lvalueParseInfo = routine().getCurrentLvalueParseInfo(node, true);
		lvalueParseInfo.checkIsValid();
		
		ExpressionInfo<?> currentInfo = routine().getLastExpressionInfo(node);
		TypeInfo currentTypeInfo = currentInfo.getTypeInfo();
		currentInfo.setTypeInfo(node, currentTypeInfo.copy(node, currentTypeInfo.referenceLevel - lvalueParseInfo.dereferenceLevel));
		if (currentInfo.getTypeInfo().isNonAddressable()) {
			String lvalueString = Helpers.removeWhitespace(node.getExpressionLvalue().toString());
			throw new IllegalArgumentException(String.format("Attempted to assign expression to non-modifiable lvalue \"%s\"! %s", lvalueString, node));
		}
		checkLastExpressionCast(node, prevInfo.getTypeInfo(), "Attempted to assign expression of type \"%s\" to lvalue of incompatible type \"%s\"! %s");
		
		if (op.equals(Global.EQUALS)) {
			routine().addStackLvalueAssignmentAction(node, scope, lvalueParseInfo);
		}
		else {
			op = op.substring(0, op.length() - Global.EQUALS.length());
			routine().addStackLvalueAssignmentOperationAction(node, scope, op, lvalueParseInfo);
		}
		
		node.getSeparator().apply(this);
	}
	
	@Override
	public void inAConditionalSection(AConditionalSection node) {
		ConditionalSectionInfo info = new ConditionalSectionInfo();
		boolean elseSection = node.getElseSection() != null;
		info.setHasElseSection(node, elseSection);
		int sectionLength = 1 + (node.getConditionalMiddleSection() == null ? 0 : node.getConditionalMiddleSection().size()) + (elseSection ? 1 : 0);
		info.setSectionLength(node, sectionLength);
		routine().conditionalSectionInfoStack.push(info);
	}
	
	@Override
	public void outAConditionalSection(AConditionalSection node) {
		routine().incrementSectionId();
		if (!routine().currentConditionalSectionInfo(node).getHasElseSection(node)) {
			routine().addConditionalSectionElseJumpAction(node);
		}
		routine().addConditionalSectionExitJumpActions(node);
		routine().conditionalSectionInfoStack.pop();
	}
	
	@Override
	public void caseAConditionalStartSection(AConditionalStartSection node) {
		routine().incrementSectionId();
		
		node.getConditionalStartSectionKeyword().apply(this);
		routine().currentConditionalSectionInfo(node).setExecuteIfCondition(node, node.getConditionalStartSectionKeyword().toString().trim().equals(Global.IF));
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsInt(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s");
		
		routine().currentConditionalSectionInfo(node).setElseJumpSectionId(node, routine());
		routine().incrementSectionId();
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		routine().currentConditionalSectionInfo(node).addExitJumpSection(node, routine());
		node.getRBrace().apply(this);
	}
	
	@Override
	public void caseAConditionalMiddleSection(AConditionalMiddleSection node) {
		routine().incrementSectionId();
		routine().addConditionalSectionElseJumpAction(node);
		
		node.getConditionalMiddleSectionKeyword().apply(this);
		routine().currentConditionalSectionInfo(node).setExecuteIfCondition(node, node.getConditionalMiddleSectionKeyword().toString().trim().equals(Global.ELIF));
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsInt(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s");
		
		routine().currentConditionalSectionInfo(node).setElseJumpSectionId(node, routine());
		routine().incrementSectionId();
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		routine().currentConditionalSectionInfo(node).addExitJumpSection(node, routine());
		node.getRBrace().apply(this);
	}
	
	@Override
	public void caseAElseSection(AElseSection node) {
		routine().incrementSectionId();
		routine().addConditionalSectionElseJumpAction(node);
		
		node.getElse().apply(this);
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			scope.previous.expectingFunctionReturn = false;
			node.getStopStatement().apply(this);
		}
		routine().currentConditionalSectionInfo(node).addExitJumpSection(node, routine());
		node.getRBrace().apply(this);
	}
	
	@Override
	public void caseALoopIterativeSection(ALoopIterativeSection node) {
		routine().incrementSectionId();
		IterativeSectionInfo info = new IterativeSectionInfo();
		info.setContinueJumpTargetSectionId(node, routine().currentSectionId());
		routine().iterativeSectionInfoStack.push(info);
		
		node.getLoop().apply(this);
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		routine().addIterativeSectionContinueJumpAction(node);
		routine().incrementSectionId();
		info.setBreakJumpTargetSectionId(node, routine().currentSectionId());
		routine().finalizeIterativeSectionJumpActions(node);
		routine().iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseAConditionalIterativeSection(AConditionalIterativeSection node) {
		IterativeSectionInfo info = new IterativeSectionInfo();
		routine().iterativeSectionInfoStack.push(info);
		
		routine().addIterativeSectionContinueJumpAction(node);
		routine().incrementSectionId();
		info.setBodyJumpTargetSectionId(node, routine().currentSectionId());
		
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		routine().incrementSectionId();
		info.setContinueJumpTargetSectionId(node, routine().currentSectionId());
		
		node.getConditionalIterativeSectionKeyword().apply(this);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsInt(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s");
		
		routine().addIterativeSectionConditionalBodyJumpAction(node, node.getConditionalIterativeSectionKeyword().toString().trim().equals(Global.WHILE));
		routine().incrementSectionId();
		info.setBreakJumpTargetSectionId(node, routine().currentSectionId());
		routine().finalizeIterativeSectionJumpActions(node);
		routine().iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseADoConditionalIterativeSection(ADoConditionalIterativeSection node) {
		IterativeSectionInfo info = new IterativeSectionInfo();
		routine().iterativeSectionInfoStack.push(info);
		
		routine().incrementSectionId();
		info.setBodyJumpTargetSectionId(node, routine().currentSectionId());
		
		node.getDo().apply(this);
		node.getLBrace().apply(this);
		for (PBasicSection section : node.getBasicSection()) {
			section.apply(this);
		}
		if (node.getStopStatement() != null) {
			node.getStopStatement().apply(this);
		}
		node.getRBrace().apply(this);
		
		routine().incrementSectionId();
		info.setContinueJumpTargetSectionId(node, routine().currentSectionId());
		
		node.getConditionalIterativeSectionKeyword().apply(this);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionIsInt(node, "Attempted to use expression of type \"%s\" as conditional expression of incompatible type \"%s\"! %s");
		node.getSeparator().apply(this);
		
		routine().addIterativeSectionConditionalBodyJumpAction(node, node.getConditionalIterativeSectionKeyword().toString().trim().equals(Global.WHILE));
		routine().incrementSectionId();
		info.setBreakJumpTargetSectionId(node, routine().currentSectionId());
		routine().finalizeIterativeSectionJumpActions(node);
		routine().iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseAGotoStatement(AGotoStatement node) {
		node.getGoto().apply(this);
		node.getName().apply(this);
		routine().addGotoAction(node, node.getName().getText());
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseASectionLabel(ASectionLabel node) {
		node.getName().apply(this);
		routine().incrementSectionId();
		routine().mapStatementLabel(node, node.getName().getText());
		node.getColon().apply(this);
	}
	
	@Override
	public void caseAExitStopStatement(AExitStopStatement node) {
		node.getExit().apply(this);
		routine().addExitAction(node);
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseAReturnStopStatement(AReturnStopStatement node) {
		node.getReturn().apply(this);
		routine().addReturnAction(node, Global.DESTRUCTOR);
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseAContinueStopStatement(AContinueStopStatement node) {
		node.getContinue().apply(this);
		routine().addIterativeSectionContinueJumpAction(node);
		node.getSeparator().apply(this);
	}
	
	@Override
	public void caseABreakStopStatement(ABreakStopStatement node) {
		node.getBreak().apply(this);
		routine().addIterativeSectionBreakJumpAction(node);
		node.getSeparator().apply(this);
	}
	
	@Override
	public void inAExitExpressionStopStatement(AExitExpressionStopStatement node) {}
	
	@Override
	public void outAExitExpressionStopStatement(AExitExpressionStopStatement node) {
		routine().addExitValueAction(node);
	}
	
	@Override
	public void caseAReturnExpressionStopStatement(AReturnExpressionStopStatement node) {
		if (routine().isRootRoutine()) {
			throw new IllegalArgumentException(String.format("Root routine can not return a value! Use an exit value statement!"));
		}
		else {
			node.getReturn().apply(this);
			node.getExpressionRvalue().apply(this);
			checkLastExpressionCast(node, routine().getReturnTypeInfo(), "Attempted to use expression of type \"%s\" as return value for function of incompatible type \"%s\"! %s");
			routine().addFunctionReturnValueAction(node, scope);
			node.getSeparator().apply(this);
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
	public void caseASemicolonSeparator(ASemicolonSeparator node) {}
	
	@Override
	public void caseAEolSeparator(AEolSeparator node) {}
	
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
	
	public TypeInfo createReturnTypeInfo(Node node, Scope scope, PReturnType pReturnType, int baseReferenceLevel) {
		return createTypeInfo(node, scope, pReturnType == null ? null : ((AReturnType) pReturnType).getType(), baseReferenceLevel);
	}
	
	public TypeInfo createTypeInfo(Node node, Scope scope, PType pType, int baseReferenceLevel) {
		if (pType == null) {
			return new BasicTypeInfo(node, scope.getType(node, Global.VOID), baseReferenceLevel);
		}
		
		PRawType pRawType = ((AType) pType).getRawType();
		baseReferenceLevel += ((AType) pType).getAnd().size();
		
		if (pRawType instanceof ABasicRawType) {
			ABasicRawType aBasicType = (ABasicRawType) pRawType;
			return new BasicTypeInfo(node, scope.getType(node, aBasicType.getName().getText()), baseReferenceLevel);
		}
		else if (pRawType instanceof AArrayRawType) {
			// TODO
			return null;
		}
		else {
			AFunctionRawType aFunctionType = (AFunctionRawType) pRawType;
			aFunctionType.getParParameterList().apply(this);
			TypeInfo[] paramTypeInfos = Helpers.paramTypeInfoArray(program.getParamArray(node, getParameterListLength(aFunctionType.getParParameterList()), true));
			return new FunctionTypeInfo(node, baseReferenceLevel, createReturnTypeInfo(node, scope, aFunctionType.getReturnType(), 0), paramTypeInfos);
		}
	}
	
	@Override
	public void inAParParameterList(AParParameterList node) {
		program.pushParamList(node);
	}
	
	@Override
	public void outAParParameterList(AParParameterList node) {}
	
	@Override
	public void inAParameterList(AParameterList node) {
		VariableModifierInfo modifierInfo = getVariableModifierInfo(node.getModifier());
		modifierInfo.init_ = true;
		program.addParam(node, createDeclaratorInfo(node, node.getDeclarator(), modifierInfo, node.getType(), null));
	}
	
	@Override
	public void outAParameterList(AParameterList node) {}
	
	@Override
	public void inAParameterListTail(AParameterListTail node) {
		VariableModifierInfo modifierInfo = getVariableModifierInfo(node.getModifier());
		modifierInfo.init_ = true;
		program.addParam(node, createDeclaratorInfo(node, node.getDeclarator(), modifierInfo, node.getType(), null));
	}
	
	@Override
	public void outAParameterListTail(AParameterListTail node) {}
	
	public int getParameterListLength(PParParameterList parList) {
		AParameterList list = (AParameterList) ((AParParameterList) parList).getParameterList();
		return list == null ? 0 : 1 + list.getParameterListTail().size();
	}
	
	@Override
	public void caseADeclarator(ADeclarator node) {}
	
	public DeclaratorInfo createDeclaratorInfo(Node node, PDeclarator pDeclarator, VariableModifierInfo modifierInfo, PType ptype, Integer expectSize) {
		ADeclarator aDeclarator = (ADeclarator) pDeclarator;
		String variableName = aDeclarator == null ? Global.DISCARD_PARAM_PREFIX.concat(Integer.toString(program.currentParamListSize(node))) : aDeclarator.getName().getText();
		int baseReferenceLevel = aDeclarator == null ? 0 : aDeclarator.getMultiply().size();
		Variable variable = new Variable(variableName, modifierInfo, createTypeInfo(node, scope, ptype, baseReferenceLevel));
		return createDeclaratorInfoInternal(node, variable, baseReferenceLevel, true, expectSize);
	}
	
	public DeclaratorInfo createDeclaratorInfoInternal(Node node, Variable variable, int dereferenceLevel, boolean declaration, Integer expectSize) {
		// TODO: Handle array/struct declaration
		TypeInfo typeInfo = variable.baseTypeInfo;
		if (declaration) {
			for (int i = 0; i <= dereferenceLevel; ++i) {
				int j = typeInfo.referenceLevel - i;
				TypeInfo info = typeInfo.copy(node, j);
				if (info.isVoid(node, generator)) {
					throw new IllegalArgumentException(String.format("Implicit declarator \"%s\" dereferenced %d time(s) can not be void! %s", variable.name, j, node));
				}
			}
		}
		else {
			if (typeInfo.isVoid(node, generator)) {
				throw new IllegalArgumentException(String.format("Declarator \"%s\" dereferenced %d time(s) can not be void! %s", variable.name, dereferenceLevel, node));
			}
		}
		
		if (expectSize != null) {
			int size = typeInfo.getSize(node, generator);
			if (expectSize != size) {
				throw new IllegalArgumentException(String.format("Declarator \"%s\" requires having a size %s, but has a size %s! %s", variable.name, expectSize, size, node));
			}
		}
		return new DeclaratorInfo(node, variable, dereferenceLevel);
	}
	
	@Override
	public void inAExpressionLvalue(AExpressionLvalue node) {
		routine().setCurrentLvalueParseInfo(node, new LvalueParseInfo(node.getExpression6()));
		routine().expressionInfoStack.push(new LvalueExpressionInfo(generator));
	}
	
	@Override
	public void outAExpressionLvalue(AExpressionLvalue node) {
		routine().setLastExpressionInfo(node, routine().expressionInfoStack.pop());
	}
	
	@Override
	public void inAExpressionRvalue(AExpressionRvalue node) {
		routine().expressionInfoStack.push(new RvalueExpressionInfo(generator));
	}
	
	@Override
	public void outAExpressionRvalue(AExpressionRvalue node) {
		routine().setLastExpressionInfo(node, routine().expressionInfoStack.pop());
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
	
	public void binaryExpression(Node node, Node left, String op, Node right) {
		if (!tryIntegerRegisterAssignment(node)) {
			left.apply(this);
			routine().pushCurrentRegIdToStack(node);
			ExpressionInfo<?> prevInfo = routine().currentExpressionInfo(node).copy(node);
			right.apply(this);
			routine().pushCurrentRegIdToStack(node);
			routine().incrementRegId();
			routine().addBinaryOpAction(node, scope, op.trim(), prevInfo);
		}
	}
	
	@Override
	public void inAPrioritizedExpression6(APrioritizedExpression6 node) {}
	
	@Override
	public void outAPrioritizedExpression6(APrioritizedExpression6 node) {}
	
	@Override
	public void caseAUnaryExpression6(AUnaryExpression6 node) {
		if (!tryIntegerRegisterAssignment(node)) {
			node.getUnaryOp().apply(this);
			node.getExpression6().apply(this);
			routine().pushCurrentRegIdToStack(node);
			routine().incrementRegId();
			routine().addUnaryOpAction(node, scope, node.getUnaryOp().toString().trim());
		}
	}
	
	@Override
	public void caseADereferenceExpression6(ADereferenceExpression6 node) {
		if (!tryIntegerRegisterAssignment(node)) {
			node.getMultiply().apply(this);
			node.getExpression6().apply(this);
			if (routine().getCurrentLvalueParseInfo(node, false) == null) {
				routine().currentExpressionInfo(node).decrementReferenceLevel(node);
				routine().pushCurrentRegIdToStack(node);
				routine().incrementRegId();
				routine().addDereferenceAction(node, scope);
			}
			else {
				++routine().getCurrentLvalueParseInfo(node, false).dereferenceLevel;
			}
		}
	}
	
	@Override
	public void caseAAddressOfExpression6(AAddressOfExpression6 node) {
		node.getAnd().apply(this);
		String variableName = node.getName().getText();
		Variable variable = scope.getVariable(node, variableName);
		TypeInfo typeInfo = variable.baseTypeInfo;
		if (typeInfo.isNonAddressable()) {
			throw new IllegalArgumentException(String.format("Can not get address of non-addressable variable \"%s\"! %s", variableName, node));
		}
		routine().currentExpressionInfo(node).setTypeInfo(node, typeInfo);
		routine().currentExpressionInfo(node).incrementReferenceLevel(node);
		routine().incrementRegId();
		routine().addAddressOfRegisterAssignmentAction(node, scope, variable);
	}
	
	@Override
	public void inAPrioritizedExpression7(APrioritizedExpression7 node) {}
	
	@Override
	public void outAPrioritizedExpression7(APrioritizedExpression7 node) {}
	
	@Override
	public void caseAFunctionExpression7(AFunctionExpression7 node) {
		routine().pushFunctionCallToStack(node);
		
		TypeInfo typeInfo = tryGetDirectFunctionTypeInfo(node.getExpression7());
		if (typeInfo == null) {
			node.getExpression7().apply(this);
			typeInfo = routine().currentExpressionInfo(node).getTypeInfo();
			if (!typeInfo.isFunction()) {
				throw new IllegalArgumentException(String.format("Attempted to use expression of incompatible type \"%s\" as a function type! %s", typeInfo, node));
			}
		}
		routine().currentFunctionCallInfo(node).typeInfo = (FunctionTypeInfo) typeInfo;
		routine().pushCurrentRegIdToStack(node);
		
		node.getParExpressionList().apply(this);
		
		routine().incrementRegId();
		routine().addFunctionAction(node, scope);
	}
	
	public TypeInfo tryGetDirectFunctionTypeInfo(PExpression7 expression) {
		String name = Helpers.removeParentheses(expression.toString());
		if (scope.functionExists(name)) {
			Variable variable = scope.getVariable(expression, name);
			TypeInfo typeInfo = variable.baseTypeInfo;
			if (typeInfo.isFunction()) {
				routine().currentExpressionInfo(expression).setTypeInfo(expression, typeInfo);
				routine().incrementRegId();
				routine().addDirectFunctionRegisterAssignmentAction(expression, scope, variable);
				routine().currentExpressionInfo(expression).isDirectFunction = true;
				return typeInfo;
			}
		}
		return null;
	}
	
	@Override
	public void caseAArgumentList(AArgumentList node) {
		FunctionCallInfo info = routine().currentFunctionCallInfo(node);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionCast(node, info.getNextParamType(node), "Attempted to use expression of type \"%s\" as function argument of incompatible type \"%s\"! %s");
		routine().pushCurrentRegIdToStack(node);
		for (PArgumentListTail tail : node.getArgumentListTail()) {
			tail.apply(this);
		}
	}
	
	@Override
	public void caseAArgumentListTail(AArgumentListTail node) {
		FunctionCallInfo info = routine().currentFunctionCallInfo(node);
		node.getComma().apply(this);
		node.getExpressionRvalue().apply(this);
		checkLastExpressionCast(node, info.getNextParamType(node), "Attempted to use expression of type \"%s\" as function argument of incompatible type \"%s\"! %s");
		routine().pushCurrentRegIdToStack(node);
	}
	
	public List<EvaluationInfo> getArgumentEvaluationInfoList(PExpressionList pExpressionList) {
		List<EvaluationInfo> infoList = new ArrayList<>();
		if (pExpressionList != null) {
			AExpressionList expressionList = (AExpressionList) pExpressionList;
			PExpressionRvalue pExpressionRvalue = expressionList.getExpressionRvalue();
			infoList.add(Evaluator.evaluate(pExpressionRvalue, generator, scope, pExpressionRvalue.toString()));
			for (PExpressionListTail tail : expressionList.getExpressionListTail()) {
				pExpressionRvalue = ((AExpressionListTail) tail).getExpressionRvalue();
				infoList.add(Evaluator.evaluate(pExpressionRvalue, generator, scope, pExpressionRvalue.toString()));
			}
		}
		return infoList;
	}
	
	@Override
	public void caseAValueExpression8(AValueExpression8 node) {
		if (routine().getCurrentLvalueParseInfo(node, false) != null) {
			routine().getCurrentLvalueParseInfo(node, false).error();
		}
		node.getValue().apply(this);
		EvaluationInfo evalInfo = Evaluator.evaluate(node, generator, scope, node.toString());
		routine().setLastExpressionInfo(node, evalInfo.expressionInfo);
		routine().currentExpressionInfo(node).setTypeInfo(node, evalInfo.expressionInfo.getTypeInfo());
		routine().incrementRegId();
		routine().addImmediateRegisterAssignmentAction(node, scope, evalInfo.value);
	}
	
	public boolean tryIntegerRegisterAssignment(Node node) {
		EvaluationInfo evalInfo = Evaluator.tryEvaluate(node, generator, scope, node.toString());
		if (evalInfo != null) {
			if (routine().getCurrentLvalueParseInfo(node, false) != null) {
				routine().getCurrentLvalueParseInfo(node, false).error();
			}
			routine().setLastExpressionInfo(node, evalInfo.expressionInfo);
			routine().currentExpressionInfo(node).setTypeInfo(node, evalInfo.expressionInfo.getTypeInfo());
			routine().incrementRegId();
			routine().addImmediateRegisterAssignmentAction(node, scope, evalInfo.value);
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public void caseAVariableExpression8(AVariableExpression8 node) {
		if (!tryIntegerRegisterAssignment(node)) {
			node.getName().apply(this);
			String variableName = node.getName().getText();
			Variable variable = scope.getVariable(node, variableName);
			routine().currentExpressionInfo(node).setTypeInfo(node, variable.baseTypeInfo);
			if (routine().getCurrentLvalueParseInfo(node, false) == null) {
				routine().incrementRegId();
				routine().addRegisterAssignmentAction(node, scope, variable);
			}
			else {
				routine().getCurrentLvalueParseInfo(node, false).variable = variable;
			}
		}
	}
	
	@Override
	public void inAParenthesesExpression8(AParenthesesExpression8 node) {}
	
	@Override
	public void outAParenthesesExpression8(AParenthesesExpression8 node) {}
	
	@Override
	public void caseAIntegerValue(AIntegerValue node) {}
	
	@Override
	public void caseACharacterValue(ACharacterValue node) {}
	
	@Override
	public void caseASizeofBasicTypeValue(ASizeofBasicTypeValue node) {}
	
	@Override
	public void caseASizeofFunctionTypeValue(ASizeofFunctionTypeValue node) {}
	
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
	public void inALogicalXorAssignmentOp(ALogicalXorAssignmentOp node) {}
	
	@Override
	public void outALogicalXorAssignmentOp(ALogicalXorAssignmentOp node) {}
	
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
	public void inAArithmeticLeftShiftAssignmentOp(AArithmeticLeftShiftAssignmentOp node) {}
	
	@Override
	public void outAArithmeticLeftShiftAssignmentOp(AArithmeticLeftShiftAssignmentOp node) {}
	
	@Override
	public void inAArithmeticRightShiftAssignmentOp(AArithmeticRightShiftAssignmentOp node) {}
	
	@Override
	public void outAArithmeticRightShiftAssignmentOp(AArithmeticRightShiftAssignmentOp node) {}
	
	@Override
	public void inALogicalRightShiftAssignmentOp(ALogicalRightShiftAssignmentOp node) {}
	
	@Override
	public void outALogicalRightShiftAssignmentOp(ALogicalRightShiftAssignmentOp node) {}
	
	@Override
	public void inACircularLeftShiftAssignmentOp(ACircularLeftShiftAssignmentOp node) {}
	
	@Override
	public void outACircularLeftShiftAssignmentOp(ACircularLeftShiftAssignmentOp node) {}
	
	@Override
	public void inACircularRightShiftAssignmentOp(ACircularRightShiftAssignmentOp node) {}
	
	@Override
	public void outACircularRightShiftAssignmentOp(ACircularRightShiftAssignmentOp node) {}
	
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
	public void inALogicalXorLogicalBinaryOp(ALogicalXorLogicalBinaryOp node) {}
	
	@Override
	public void outALogicalXorLogicalBinaryOp(ALogicalXorLogicalBinaryOp node) {}
	
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
	public void inAArithmeticLeftShiftShiftBinaryOp(AArithmeticLeftShiftShiftBinaryOp node) {}
	
	@Override
	public void outAArithmeticLeftShiftShiftBinaryOp(AArithmeticLeftShiftShiftBinaryOp node) {}
	
	@Override
	public void inAArithmeticRightShiftShiftBinaryOp(AArithmeticRightShiftShiftBinaryOp node) {}
	
	@Override
	public void outAArithmeticRightShiftShiftBinaryOp(AArithmeticRightShiftShiftBinaryOp node) {}
	
	@Override
	public void inALogicalRightShiftShiftBinaryOp(ALogicalRightShiftShiftBinaryOp node) {}
	
	@Override
	public void outALogicalRightShiftShiftBinaryOp(ALogicalRightShiftShiftBinaryOp node) {}
	
	@Override
	public void inACircularLeftShiftShiftBinaryOp(ACircularLeftShiftShiftBinaryOp node) {}
	
	@Override
	public void outACircularLeftShiftShiftBinaryOp(ACircularLeftShiftShiftBinaryOp node) {}
	
	@Override
	public void inACircularRightShiftShiftBinaryOp(ACircularRightShiftShiftBinaryOp node) {}
	
	@Override
	public void outACircularRightShiftShiftBinaryOp(ACircularRightShiftShiftBinaryOp node) {}
	
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
	
	// Tokens
	
	@Override
	public void caseTLBrace(TLBrace node) {
		scope = new Scope(node, program, scope);
	}
	
	@Override
	public void caseTRBrace(TRBrace node) {
		scope = scope.previous;
	}
}
