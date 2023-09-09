package drlc.intermediate.interpreter;

import drlc.*;
import drlc.analysis.DepthFirstAdapter;
import drlc.intermediate.Program;
import drlc.intermediate.component.DeclaratorInfo;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.*;

public class SecondPassInterpreter extends DepthFirstAdapter {
	
	protected Generator generator;
	protected Program program;
	protected Routine currRoutine;
	protected Scope scope;
	
	public SecondPassInterpreter() {
		super();
	}
	
	// Productions
	
	@Override
	public void caseAFunctionDefinition(AFunctionDefinition node) {
		PParParameterList parParameterList = node.getParParameterList();
		int argc = getParameterListLength(parParameterList);
		parParameterList.apply(this);
		
		TypeInfo returnTypeInfo = createReturnTypeInfo(node, scope, node.getReturnType());
		
		node.getLBrace().apply(this);
		
		Routine routine = currRoutine;
		for (DeclaratorInfo paramInfo : routine.getParamArray(node, argc, true)) {
			scope.addVariable(node, paramInfo.variable, false);
		}
		routine.defineFunctionAndSetRoutine(node, scope.parent, node.getName().getText(), argc, returnTypeInfo);
		
		node.getScopeContents().apply(this);
		
		node.getRBrace().apply(this);
		
		program.returnToRootRoutine();
	}
	
	@Override
	public void caseAConditionalSection(AConditionalSection node) {
		Routine routine = currRoutine;
		currRoutine.conditionalSectionInfoStack.push(new ConditionalSectionInfo());
		
		routine.incrementSectionId();
		routine.addConditionalSectionElseJumpAction(null);
		
		ConditionalSectionInfo conditionalSectionInfo = routine.currentConditionalSectionInfo(null);
		conditionalSectionInfo.sectionStart = false;
		
		conditionalSectionInfo.setExecuteIfCondition(null, node.getConditionalBranchKeyword().toString().trim().equals(Global.IF));
		
		routine.applyExpressionInfo(this, node.getExpression(), generator.boolTypeInfo, false, "conditional expression");
		
		ExpressionInfo expressionInfo = routine.popExpressionInfo(node, scope, false);
		
		conditionalSectionInfo.setElseJumpSectionId(null, routine);
		routine.incrementSectionId();
		
		node.getLBrace().apply(this);
		
		node.getScopeContents().apply(this);
		
		conditionalSectionInfo.addExitJumpSection(null, routine);
		node.getRBrace().apply(this);
		
		PElseSection elseSection = node.getElseSection();
		if (elseSection != null) {
			elseSection.apply(this);
		}
		
		routine.incrementSectionId();
		if (!routine.currentConditionalSectionInfo(null).getHasElseSection(null)) {
			routine.addConditionalSectionElseJumpAction(null);
		}
		routine.addConditionalSectionExitJumpActions(null);
		routine.conditionalSectionInfoStack.pop();
	}
	
	@Override
	public void caseAExcludingBranchElseSection(AExcludingBranchElseSection node) {
		Routine routine = currRoutine;
		routine.incrementSectionId();
		routine.addConditionalSectionElseJumpAction(null);
		
		ConditionalSectionInfo conditionalSectionInfo = routine.currentConditionalSectionInfo(null);
		
		node.getLBrace().apply(this);
		
		node.getScopeContents().apply(this);
		
		conditionalSectionInfo.addExitJumpSection(null, routine);
		node.getRBrace().apply(this);
		
		conditionalSectionInfo.setHasElseSection(null, true);
	}
	
	@Override
	public void caseALoopIterativeSection(ALoopIterativeSection node) {
		Routine routine = currRoutine;
		routine.incrementSectionId();
		IterativeSectionInfo info = new IterativeSectionInfo();
		info.setContinueJumpTargetSectionId(null, routine.currentSectionId());
		routine.iterativeSectionInfoStack.push(info);
		
		node.getLBrace().apply(this);
		
		node.getScopeContents().apply(this);
		
		node.getRBrace().apply(this);
		
		routine.addIterativeSectionContinueJumpAction(null);
		routine.incrementSectionId();
		info.setBreakJumpTargetSectionId(null, routine.currentSectionId());
		routine.finalizeIterativeSectionJumpActions(null);
		routine.iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseAConditionalIterativeSection(AConditionalIterativeSection node) {
		Routine routine = currRoutine;
		IterativeSectionInfo info = new IterativeSectionInfo();
		routine.iterativeSectionInfoStack.push(info);
		
		routine.addIterativeSectionContinueJumpAction(null);
		routine.incrementSectionId();
		info.setBodyJumpTargetSectionId(null, routine.currentSectionId());
		
		node.getLBrace().apply(this);
		
		node.getScopeContents().apply(this);
		
		node.getRBrace().apply(this);
		
		routine.incrementSectionId();
		info.setContinueJumpTargetSectionId(null, routine.currentSectionId());
		
		routine.applyExpressionInfo(this, node.getExpression(), generator.boolTypeInfo, false, "conditional expression");
		
		ExpressionInfo expressionInfo = routine.popExpressionInfo(node, scope, false);
		
		routine.addIterativeSectionConditionalBodyJumpAction(null, node.getConditionalIterativeKeyword().toString().trim().equals(Global.WHILE));
		routine.incrementSectionId();
		info.setBreakJumpTargetSectionId(null, routine.currentSectionId());
		routine.finalizeIterativeSectionJumpActions(null);
		routine.iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseADoConditionalIterativeSection(ADoConditionalIterativeSection node) {
		Routine routine = currRoutine;
		IterativeSectionInfo info = new IterativeSectionInfo();
		routine.iterativeSectionInfoStack.push(info);
		
		routine.incrementSectionId();
		info.setBodyJumpTargetSectionId(null, routine.currentSectionId());
		
		node.getLBrace().apply(this);
		
		node.getScopeContents().apply(this);
		
		node.getRBrace().apply(this);
		
		routine.incrementSectionId();
		info.setContinueJumpTargetSectionId(null, routine.currentSectionId());
		
		routine.applyExpressionInfo(this, node.getExpression(), generator.boolTypeInfo, false, "conditional expression");
		
		ExpressionInfo expressionInfo = routine.popExpressionInfo(node, scope, false);
		
		routine.addIterativeSectionConditionalBodyJumpAction(null, node.getConditionalIterativeKeyword().toString().trim().equals(Global.WHILE));
		routine.incrementSectionId();
		info.setBreakJumpTargetSectionId(null, routine.currentSectionId());
		routine.finalizeIterativeSectionJumpActions(null);
		routine.iterativeSectionInfoStack.pop();
	}
	
	@Override
	public void caseAGotoStatement(AGotoStatement node) {
		currRoutine.addGotoAction(node, node.getName().getText());
	}
	
	@Override
	public void caseASectionLabel(ASectionLabel node) {
		Routine routine = currRoutine;
		routine.incrementSectionId();
		routine.mapSectionLabel(node, node.getName().getText());
	}
	
	@Override
	public void caseAContinueStopStatement(AContinueStopStatement node) {
		currRoutine.addIterativeSectionContinueJumpAction(node);
	}
	
	@Override
	public void caseABreakStopStatement(ABreakStopStatement node) {
		currRoutine.addIterativeSectionBreakJumpAction(node);
	}
}
