package drlc.interpret.routine;

import java.util.*;

import drlc.*;
import drlc.interpret.*;
import drlc.interpret.action.*;
import drlc.interpret.component.*;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.expression.ExpressionInfo;
import drlc.interpret.component.info.type.*;
import drlc.node.Node;

public abstract class Routine {
	
	public final Program program;
	public final String name;
	
	protected RoutineType baseType = RoutineType.LEAF;
	
	private final List<List<Action>> body = new ArrayList<>();
	private final List<Action> destruction = new ArrayList<>();
	
	public int sectionId = 0;
	private String sectionIdString = Helpers.sectionIdString(0);
	
	private Stack<FunctionCallInfo> functionCallInfoStack = new Stack<>();
	
	public Stack<ExpressionInfo<?>> expressionInfoStack = new Stack<>();
	protected ExpressionInfo<?> lastExpressionInfo = null;
	
	protected LvalueParseInfo lvalueParseInfo = null;
	
	public Stack<ConditionalSectionInfo> conditionalSectionInfoStack = new Stack<>();
	public Stack<IterativeSectionInfo> iterativeSectionInfoStack = new Stack<>();
	
	private Map<String, String> statementLabelSectionIdMap = new HashMap<>();
	
	private int regId = -1;
	private DataId regDataId = null;
	public Stack<DataId> regDataIdStack = new Stack<>();
	
	protected Routine(Program program, String name) {
		this.program = program;
		this.name = name;
		body.add(new ArrayList<>());
	}
	
	public RoutineType getType() {
		return baseType;
	}
	
	public abstract void onRequiresNesting(boolean force);
	
	public abstract void onRequiresStack(boolean force);
	
	public boolean isLeafRoutine() {
		return getType().equals(RoutineType.LEAF);
	}
	
	public boolean isNestingRoutine() {
		return getType().equals(RoutineType.NESTING);
	}
	
	public boolean isStackRoutine() {
		return getType().equals(RoutineType.STACK);
	}
	
	public boolean isRootRoutine() {
		return false;
	}
	
	public boolean isFunctionRoutine() {
		return false;
	}
	
	public boolean isBuiltInFunctionRoutine() {
		return false;
	}
	
	public Function getFunction() {
		return null;
	}
	
	public abstract TypeInfo getReturnTypeInfo();
	
	public abstract DeclaratorInfo[] getParams();
	
	public List<List<Action>> getBodyActionLists() {
		return body;
	}
	
	public List<Action> getDestructionActionList() {
		return destruction;
	}
	
	// Preliminary optimization
	
	public void flattenSections() {
		String _d = Helpers.sectionIdString(body.size());
		body.add(getDestructionActionList());
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) list.get(j);
					if (jump.getTarget().equals(Global.DESTRUCTOR)) {
						list.set(j, jump.copy(_d));
					}
				}
			}
		}
	}
	
	public void fixUpLabelJumps() {
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j) instanceof IJumpAction) {
					IJumpAction<?> jump = (IJumpAction<?>) list.get(j);
					String target = jump.getTarget();
					if (Helpers.isStatementLabel(target)) {
						list.set(j, jump.copy(getStatementLabelSectionId(target)));
					}
				}
			}
		}
	}
	
	// Finalization
	
	public void setTransientRegisters() {
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); j++) {
				Action action = list.get(j), previous = list.get(j - 1);
				if (action instanceof ConditionalJumpAction) {
					ConditionalJumpAction cja = (ConditionalJumpAction) action;
					if (!(previous instanceof IValueAction)) {
						throw new IllegalArgumentException(String.format("Found unexpected action \"%s\" before conditional jump action \"%s\"!", previous, cja));
					}
					IValueAction iva = (IValueAction) previous;
					if (!iva.canReplaceLvalue() || iva.lvalues().length != 1) {
						throw new IllegalArgumentException(String.format("Found unexpected value action \"%s\" before conditional jump action \"%s\"!", iva, cja));
					}
					list.set(j - 1, iva.replaceLvalue(iva.lvalues()[0], Global.TRANSIENT_DATA_ID));
				}
			}
		}
	}
	
	public void checkInvalidDataIds() {
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					for (DataId rvalue : iva.rvalues()) {
						if (rvalue.dereferenceLevel > 0) {
							throw new IllegalArgumentException(String.format("Found invalid rvalue \"%s\" in value action \"%s\"!", rvalue, iva));
						}
					}
				}
			}
		}
	}
	
	public void checkFunctionVariableInitialization() {
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 0; j < list.size(); j++) {
				Action action = list.get(j);
				if (action instanceof AssignmentAction) {
					AssignmentAction aa = (AssignmentAction) action;
					if (program.rootScope.functionExists(aa.arg.raw) && !program.routineExists(aa.arg.raw)) {
						throw new IllegalArgumentException(String.format("Function \"%s\" was not defined! %s", aa.arg, aa));
					}
				}
			}
		}
	}
	
	// General actions
	
	protected void addAction(Action action) {
		currentSection().add(action);
	}
	
	public void addImmediateRegisterAssignmentAction(Node node, Scope scope, long value) {
		addAction(new AssignmentAction(node, currentRegId(node), Helpers.immediateDataId(value)));
	}
	
	public void addDirectFunctionRegisterAssignmentAction(Node node, Scope scope, Variable variable) {
		addAction(new AssignmentAction(node, currentRegId(node), new DataId(variable.name, variable.scopeId)));
	}
	
	public void addAddressOfRegisterAssignmentAction(Node node, Scope scope, Variable variable) {
		if (!variable.modifierInfo.init_) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not initialized! %s", variable.name, node));
		}
		else {
			addAction(new AssignmentAction(node, currentRegId(node), new DataId(Helpers.addAddressPrefix(variable.name), variable.scopeId)));
		}
	}
	
	public void addRegisterAssignmentAction(Node node, Scope scope, Variable variable) {
		if (!variable.modifierInfo.init_) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not initialized! %s", variable.name, node));
		}
		else {
			addAction(new AssignmentAction(node, currentRegId(node), new DataId(variable.name, variable.scopeId)));
		}
	}
	
	public void addStackAssignmentAction(Node node, Scope scope) {
		addAction(new AssignmentAction(node, currentRegId(node), popRegIdStack(1)[0]));
	}
	
	public void addStackDeclarationAction(Node node, Scope scope, DeclaratorInfo declaratorInfo) {
		addAction(new DeclarationAction(node, declaratorInfo.dataId(), declaratorInfo.typeInfo));
	}
	
	public void addStackInitializationAction(Node node, Scope scope, DeclaratorInfo declaratorInfo) {
		addAction(new InitializationAction(node, declaratorInfo.dataId(), declaratorInfo.typeInfo, popRegIdStack(1)[0]));
	}
	
	public void addStackLvalueAssignmentAction(Node node, Scope scope, LvalueParseInfo lvalueParseInfo) {
		Variable variable = lvalueParseInfo.variable;
		int dereferenceLevel = lvalueParseInfo.dereferenceLevel;
		
		if (variable != null) {
			if (Helpers.isConstantName(variable.name) && scope.constantExists(variable.name)) {
				throw new IllegalArgumentException(String.format("Constant \"%s\" can not be modified! %s", variable.name, node));
			}
			else {
				variable.modifierInfo.init_ = true;
				addAction(new AssignmentAction(node, variable.lvalueDataId(dereferenceLevel), popRegIdStack(1)[0]));
			}
		}
		else if (dereferenceLevel > 0) {
			addAction(new AssignmentAction(node, currentRegId(node).addDereferences(dereferenceLevel), popRegIdStack(1)[0]));
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered invalid lvalue parse info! %s", lvalueParseInfo.node));
		}
	}
	
	public void addStackLvalueAssignmentOperationAction(Node node, Scope scope, String operation, LvalueParseInfo lvalueParseInfo) {
		Variable variable = lvalueParseInfo.variable;
		int dereferenceLevel = lvalueParseInfo.dereferenceLevel;
		DataId r0;
		
		if (variable != null) {
			if (Helpers.isConstantName(variable.name) && scope.constantExists(variable.name)) {
				throw new IllegalArgumentException(String.format("Constant \"%s\" can not be modified! %s", variable.name, node));
			}
			else if (!variable.modifierInfo.init_) {
				throw new IllegalArgumentException(String.format("Variable \"%s\" not initialized! %s", variable.name, node));
			}
			else {
				r0 = variable.lvalueDataId(0);
			}
		}
		else if (dereferenceLevel > 0) {
			r0 = currentRegId(node);
		}
		else {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered invalid lvalue parse info! %s", lvalueParseInfo.node));
		}
		
		incrementRegId();
		DataId r1 = currentRegId(node);
		
		if (dereferenceLevel == 0) {
			addAction(new BinaryOpAction(node, r1, r0, operation, popRegIdStack(1)[0]));
			addAction(new AssignmentAction(node, r0, r1));
		}
		else if (dereferenceLevel == 1) {
			addAction(new DereferenceAction(node, r1, r0));
			incrementRegId();
			DataId r2 = currentRegId(node);
			addAction(new BinaryOpAction(node, r2, r1, operation, popRegIdStack(1)[0]));
			addAction(new AssignmentAction(node, r0.addDereferences(1), r2));
		}
		else {
			for (int i = 1; i < dereferenceLevel; ++i) {
				addAction(new DereferenceAction(node, r1, r0));
				r0 = r1;
				incrementRegId();
				r1 = currentRegId(node);
			}
			addAction(new DereferenceAction(node, r1, r0));
			incrementRegId();
			DataId r2 = currentRegId(node);
			addAction(new BinaryOpAction(node, r2, r1, operation, popRegIdStack(1)[0]));
			addAction(new AssignmentAction(node, r0.addDereferences(1), r2));
		}
	}
	
	public void addBinaryOpAction(Node node, Scope scope, String operation, ExpressionInfo<?> prevExpressionInfo) {
		DataId[] popRegIdStack = popRegIdStack(2);
		BinaryOpAction boa = new BinaryOpAction(node, currentRegId(node), popRegIdStack[0], operation, popRegIdStack[1]);
		addAction(boa);
		currentExpressionInfo(node).binaryOp(node, boa.opType, prevExpressionInfo);
	}
	
	public void addUnaryOpAction(Node node, Scope scope, String operation) {
		UnaryOpAction uoa = new UnaryOpAction(node, currentRegId(node), operation, popRegIdStack(1)[0]);
		addAction(uoa);
		currentExpressionInfo(node).unaryOp(node, uoa.opType);
	}
	
	public void addDereferenceAction(Node node, Scope scope) {
		addAction(new DereferenceAction(node, currentRegId(node), popRegIdStack(1)[0]));
	}
	
	public void addExitAction(Node node) {
		addAction(Global.EXIT_PROGRAM);
	}
	
	public void addExitValueAction(Node node) {
		addAction(new ExitValueAction(node, currentRegId(node)));
	}
	
	public void addReturnAction(Node node, String target) {
		if (isRootRoutine()) {
			throw new IllegalArgumentException(String.format("Root routine can not return! Use an exit statement!"));
		}
		else {
			addAction(new JumpAction(node, target));
		}
	}
	
	public void addConditionalJumpAction(Node node, String target, boolean jumpCondition) {
		addAction(new ConditionalJumpAction(node, target, jumpCondition));
	}
	
	public void addGotoAction(Node node, String labelName) {
		addAction(new JumpAction(node, Helpers.statementLabelString(labelName)));
	}
	
	public void addFunctionAction(Node node, Scope scope) {
		FunctionCallInfo functionCallInfo = functionCallInfoStack.pop();
		FunctionTypeInfo functionTypeInfo = functionCallInfo.typeInfo;
		Function function = functionTypeInfo.function;
		if (function == null) {
			addIndirectFunctionActionInternal(node, scope, functionCallInfo, functionTypeInfo.paramTypeInfos.length);
		}
		else {
			addDirectFunctionActionInternal(node, scope, functionCallInfo, function, function.getArgumentCount());
		}
	}
	
	private void addIndirectFunctionActionInternal(Node node, Scope scope, FunctionCallInfo functionCallInfo, int paramCount) {
		FunctionTypeInfo functionTypeInfo = functionCallInfo.typeInfo;
		TypeInfo returnTypeInfo = functionTypeInfo.returnTypeInfo;
		int argc = functionCallInfo.getArgumentCount();
		
		if (paramCount != argc) {
			throw new IllegalArgumentException(String.format("Function requires %d arguments but received %d! %s", paramCount, argc, node));
		}
		else {
			DataId[] args = popRegIdStack(argc);
			DataId r0 = popRegIdStack(1)[0], r1 = currentRegId(node);
			for (int i = 0; i < functionTypeInfo.referenceLevel; ++i) {
				addAction(new DereferenceAction(node, r1, r0));
				r0 = r1;
				incrementRegId();
				r1 = currentRegId(node);
			}
			
			addAction(new FunctionCallAction(node, currentRegId(node), r0, args));
			currentExpressionInfo(node).setTypeInfo(node, returnTypeInfo);
		}
	}
	
	private void addDirectFunctionActionInternal(Node node, Scope scope, FunctionCallInfo functionCallInfo, Function function, int paramCount) {
		FunctionTypeInfo functionTypeInfo = functionCallInfo.typeInfo;
		int argc = functionCallInfo.getArgumentCount();
		
		if (paramCount != argc) {
			throw new IllegalArgumentException(String.format("Function \"%s\" requires %d arguments but received %d! %s", function.name, paramCount, argc, node));
		}
		else {
			function.required = true;
			DataId[] args = popRegIdStack(argc);
			DataId r0 = popRegIdStack(1)[0], r1 = currentRegId(node);
			for (int i = 0; i < functionTypeInfo.referenceLevel; ++i) {
				addAction(new DereferenceAction(node, r1, r0));
				r0 = r1;
				incrementRegId();
				r1 = currentRegId(node);
			}
			
			if (function.builtIn) {
				addAction(new BuiltInFunctionCallAction(node, currentRegId(node), r0, args));
			}
			else {
				addAction(new FunctionCallAction(node, currentRegId(node), r0, args));
			}
			currentExpressionInfo(node).setTypeInfo(node, function.returnTypeInfo);
		}
	}
	
	public void addFunctionReturnValueAction(Node node, Scope scope) {
		scope.checkExpectingFunctionReturn(node, true);
		addAction(new ReturnValueAction(node, currentRegId(node)));
	}
	
	// Function stack
	
	public void pushFunctionCallToStack(Node node) {
		functionCallInfoStack.push(new FunctionCallInfo());
	}
	
	public FunctionCallInfo currentFunctionCallInfo(Node node) {
		if (functionCallInfoStack.empty()) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to get function argument info! %s", node));
		}
		else {
			return functionCallInfoStack.peek();
		}
	}
	
	// Expression info
	
	public ExpressionInfo<?> currentExpressionInfo(Node node) {
		if (expressionInfoStack.empty()) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to get expression info! %s", node));
		}
		else {
			return expressionInfoStack.peek();
		}
	}
	
	public ExpressionInfo<?> getLastExpressionInfo(Node node) {
		return lastExpressionInfo;
	}
	
	public void setLastExpressionInfo(Node node, ExpressionInfo<?> expressionInfo) {
		TypeInfo expressionTypeInfo = expressionInfo.getTypeInfo();
		if (expressionTypeInfo.isFunction()) {
			Function function = ((FunctionTypeInfo) expressionTypeInfo).function;
			if (function != null && !expressionInfo.isDirectFunction) {
				function.modifierInfo.stack_ = true;
				function.required = true;
				
				Routine functionRoutine = program.getRoutine(function.name);
				if (functionRoutine != null) {
					functionRoutine.onRequiresStack(true);
				}
			}
		}
		lastExpressionInfo = expressionInfo;
	}
	
	// Lvalue info
	
	public LvalueParseInfo getCurrentLvalueParseInfo(Node node, boolean finishedParsing) {
		if (finishedParsing || currentExpressionInfo(node).isLvalue()) {
			LvalueParseInfo lvalueParseInfo = this.lvalueParseInfo;
			if (finishedParsing) {
				this.lvalueParseInfo = null;
			}
			return lvalueParseInfo;
		}
		else {
			return null;
		}
	}
	
	public void setCurrentLvalueParseInfo(Node node, LvalueParseInfo lvalueParseInfo) {
		if (lvalueParseInfo == null && this.lvalueParseInfo == null) {
			throw new IllegalArgumentException(String.format("Lvalue parse info was unexpectedly null! %s", node));
		}
		else if (lvalueParseInfo != null && this.lvalueParseInfo != null) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to overwrite lvalue parse info! %s", node));
		}
		else {
			this.lvalueParseInfo = lvalueParseInfo;
		}
	}
	
	// Conditional and iterative sections
	
	public ConditionalSectionInfo currentConditionalSectionInfo(Node node) {
		if (conditionalSectionInfoStack.empty()) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to get conditional section info! %s", node));
		}
		else {
			return conditionalSectionInfoStack.peek();
		}
	}
	
	public void addConditionalSectionElseJumpAction(Node node) {
		ConditionalSectionInfo info = currentConditionalSectionInfo(node);
		body.get(info.getElseJumpSectionId(node)).add(new ConditionalJumpAction(node, sectionIdString, !info.getExecuteIfCondition(node)));
	}
	
	public void addConditionalSectionExitJumpActions(Node node) {
		for (int sectionId : currentConditionalSectionInfo(node).getExitJumpSectionIds(node)) {
			body.get(sectionId).add(new JumpAction(node, sectionIdString));
		}
	}
	
	public IterativeSectionInfo currentIterativeSectionInfo(Node node) {
		if (iterativeSectionInfoStack.empty()) {
			throw new IllegalArgumentException(String.format("Unexpectedly attempted to get iterative section info! %s", node));
		}
		else {
			return iterativeSectionInfoStack.peek();
		}
	}
	
	public void addIterativeSectionContinueJumpAction(Node node) {
		currentIterativeSectionInfo(node).continueJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATION_CONTINUE_PLACEHOLDER);
	}
	
	public void addIterativeSectionConditionalContinueJumpAction(Node node, boolean continueIfCondition) {
		currentIterativeSectionInfo(node).continueJumpIterativeSectionIds.add(sectionId);
		addAction(continueIfCondition ? Global.ITERATION_CONDITIONAL_CONTINUE_PLACEHOLDER : Global.ITERATION_CONDITIONAL_NOT_CONTINUE_PLACEHOLDER);
	}
	
	public void addIterativeSectionBodyJumpAction(Node node) {
		currentIterativeSectionInfo(node).bodyJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATION_BODY_JUMP_PLACEHOLDER);
	}
	
	public void addIterativeSectionConditionalBodyJumpAction(Node node, boolean jumpIfCondition) {
		currentIterativeSectionInfo(node).bodyJumpIterativeSectionIds.add(sectionId);
		addAction(jumpIfCondition ? Global.ITERATION_CONDITIONAL_BODY_JUMP_PLACEHOLDER : Global.ITERATION_CONDITIONAL_NOT_BODY_JUMP_PLACEHOLDER);
	}
	
	public void addIterativeSectionBreakJumpAction(Node node) {
		currentIterativeSectionInfo(node).breakJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATION_BREAK_PLACEHOLDER);
	}
	
	public void addIterativeSectionConditionalBreakJumpAction(Node node, boolean breakIfCondition) {
		currentIterativeSectionInfo(node).breakJumpIterativeSectionIds.add(sectionId);
		addAction(breakIfCondition ? Global.ITERATION_CONDITIONAL_BREAK_PLACEHOLDER : Global.ITERATION_CONDITIONAL_NOT_BREAK_PLACEHOLDER);
	}
	
	public void finalizeIterativeSectionJumpActions(Node node) {
		IterativeSectionInfo info = currentIterativeSectionInfo(node);
		
		for (int sectionId : info.getContinueJumpIterativeSectionIds(node)) {
			List<Action> list = body.get(sectionId);
			for (int i = 0; i < list.size(); i++) {
				Action action = list.get(i);
				if (Global.ITERATION_CONTINUE_PLACEHOLDER.equals(action)) {
					list.set(i, new JumpAction(node, info.getContinueJumpTargetSectionId(node)));
				}
				else if (Global.ITERATION_CONDITIONAL_CONTINUE_PLACEHOLDER.equals(action)) {
					list.set(i, new ConditionalJumpAction(node, info.getContinueJumpTargetSectionId(node), true));
				}
				else if (Global.ITERATION_CONDITIONAL_NOT_CONTINUE_PLACEHOLDER.equals(action)) {
					list.set(i, new ConditionalJumpAction(node, info.getContinueJumpTargetSectionId(node), false));
				}
			}
		}
		
		for (int sectionId : info.getBodyJumpIterativeSectionIds(node)) {
			List<Action> list = body.get(sectionId);
			for (int i = 0; i < list.size(); i++) {
				Action action = list.get(i);
				if (Global.ITERATION_BODY_JUMP_PLACEHOLDER.equals(action)) {
					list.set(i, new JumpAction(node, info.getBodyJumpTargetSectionId(node)));
				}
				else if (Global.ITERATION_CONDITIONAL_BODY_JUMP_PLACEHOLDER.equals(action)) {
					list.set(i, new ConditionalJumpAction(node, info.getBodyJumpTargetSectionId(node), true));
				}
				else if (Global.ITERATION_CONDITIONAL_NOT_BODY_JUMP_PLACEHOLDER.equals(action)) {
					list.set(i, new ConditionalJumpAction(node, info.getBodyJumpTargetSectionId(node), false));
				}
			}
		}
		
		for (int sectionId : info.getBreakJumpIterativeSectionIds(node)) {
			List<Action> list = body.get(sectionId);
			for (int i = 0; i < list.size(); i++) {
				Action action = list.get(i);
				if (Global.ITERATION_BREAK_PLACEHOLDER.equals(action)) {
					list.set(i, new JumpAction(node, info.getBreakJumpTargetSectionId(node)));
				}
				else if (Global.ITERATION_CONDITIONAL_BREAK_PLACEHOLDER.equals(action)) {
					list.set(i, new ConditionalJumpAction(node, info.getBreakJumpTargetSectionId(node), true));
				}
				else if (Global.ITERATION_CONDITIONAL_NOT_BREAK_PLACEHOLDER.equals(action)) {
					list.set(i, new ConditionalJumpAction(node, info.getBreakJumpTargetSectionId(node), false));
				}
			}
		}
	}
	
	// Statement labels
	
	public void mapStatementLabel(Node node, String labelName) {
		String statementLabelString = Helpers.statementLabelString(labelName);
		if (statementLabelSectionIdMap.containsKey(statementLabelString)) {
			if (isRootRoutine()) {
				throw new IllegalArgumentException(String.format("Label name \"%s\" already used in root routine! %s", labelName, node));
			}
			else {
				throw new IllegalArgumentException(String.format("Label name \"%s\" already used in function \"%s\"! %s", labelName, name, node));
			}
		}
		else {
			statementLabelSectionIdMap.put(statementLabelString, sectionIdString);
		}
	}
	
	public String getStatementLabelSectionId(String statementLabelString) {
		if (statementLabelSectionIdMap.containsKey(statementLabelString)) {
			return statementLabelSectionIdMap.get(statementLabelString);
		}
		else {
			if (isRootRoutine()) {
				throw new IllegalArgumentException(String.format("Label name \"%s\" not found in root routine!", Helpers.parseLabelName(statementLabelString)));
			}
			else {
				throw new IllegalArgumentException(String.format("Label name \"%s\" not found in function \"%s\"!", Helpers.parseLabelName(statementLabelString), name));
			}
		}
	}
	
	// Sections and registers
	
	public List<Action> currentSection() {
		return body.get(sectionId);
	}
	
	public void incrementSectionId() {
		++sectionId;
		sectionIdString = Helpers.sectionIdString(sectionId);
		body.add(new ArrayList<>());
	}
	
	public String currentSectionId() {
		return sectionIdString;
	}
	
	public void incrementRegId() {
		++regId;
		regDataId = new DataId(Helpers.regIdString(regId), null);
	}
	
	protected DataId currentRegId(Node node) {
		if (regDataId == null) {
			throw new IllegalArgumentException(String.format("Current register is null! %s", node));
		}
		else {
			return regDataId;
		}
	}
	
	public void pushCurrentRegIdToStack(Node node) {
		regDataIdStack.push(currentRegId(node));
	}
	
	protected DataId[] popRegIdStack(int count) {
		DataId[] out = new DataId[count];
		for (int i = 0; i < count; i++) {
			out[count - i - 1] = regDataIdStack.pop();
		}
		return out;
	}
	
	@Override
	public abstract String toString();
}
