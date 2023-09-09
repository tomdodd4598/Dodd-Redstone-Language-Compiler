package drlc.intermediate.routine;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;

public abstract class Routine {
	
	public final String name;
	
	protected RoutineCallType type = RoutineCallType.LEAF;
	
	private final List<List<Action>> body = new ArrayList<>();
	private final List<Action> destruction = new ArrayList<>();
	
	public int sectionId = 0;
	
	public final Deque<ConditionalSectionInfo> conditionalSectionInfoStack = new ArrayDeque<>();
	public final Deque<IterativeSectionInfo> iterativeSectionInfoStack = new ArrayDeque<>();
	
	private Map<String, Integer> sectionLabelSectionIdMap = new HashMap<>();
	
	private long regId = 0;
	private RegDataId regDataId = null;
	public final Deque<RegDataId> regDataIdStack = new ArrayDeque<>();
	
	protected Routine(String name) {
		this.name = name;
		body.add(new ArrayList<>());
	}
	
	public RoutineCallType getType() {
		return type;
	}
	
	public abstract void onRequiresNesting();
	
	public abstract void onRequiresStack();
	
	public boolean isLeafRoutine() {
		return getType().equals(RoutineCallType.LEAF);
	}
	
	public boolean isNestingRoutine() {
		return getType().equals(RoutineCallType.NESTING);
	}
	
	public boolean isStackRoutine() {
		return getType().equals(RoutineCallType.STACK);
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
	
	public boolean isDefined() {
		return true;
	}
	
	public Function getFunction() {
		return null;
	}
	
	public abstract @NonNull TypeInfo getReturnTypeInfo();
	
	public abstract List<DeclaratorInfo> getParams();
	
	public List<List<Action>> getBodyActionLists() {
		return body;
	}
	
	public List<Action> getDestructionActionList() {
		return destruction;
	}
	
	// Preliminary optimization
	
	public void flattenSections() {
		String destructorSectionId = Helpers.sectionIdString(body.size());
		body.add(getDestructionActionList());
		for (List<Action> list : body) {
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					if (jump.getTarget().equals(Global.DESTRUCTOR)) {
						list.set(j, jump.copy(destructorSectionId));
					}
				}
			}
		}
	}
	
	public void fixUpLabelJumps() {
		for (List<Action> list : body) {
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					String target = jump.getTarget();
					if (Helpers.isSectionLabel(target)) {
						list.set(j, jump.copy(getSectionLabelSectionId(target)));
					}
				}
			}
		}
	}
	
	// Finalization
	
	public void setTransientRegisters() {
		for (List<Action> list : body) {
			for (int j = 1; j < list.size(); ++j) {
				Action action = list.get(j), previous = list.get(j - 1);
				if (action instanceof ConditionalJumpAction) {
					ConditionalJumpAction cja = (ConditionalJumpAction) action;
					if (!(previous instanceof IValueAction)) {
						throw Helpers.nodeError(null, "Found unexpected action \"%s\" before conditional jump action \"%s\"!", previous, cja);
					}
					IValueAction iva = (IValueAction) previous;
					DataId[] lvalues = iva.lvalues();
					if (!iva.canReplaceLvalue() || lvalues.length != 1) {
						throw Helpers.nodeError(null, "Found unexpected value action \"%s\" before conditional jump action \"%s\"!", iva, cja);
					}
					list.set(j - 1, iva.setTransientLvalue());
				}
			}
		}
	}
	
	public void checkInvalidDataIds() {
		for (List<Action> list : body) {
			for (Action action : list) {
				if (action instanceof IValueAction) {
					IValueAction iva = (IValueAction) action;
					for (DataId rvalue : iva.rvalues()) {
						if (rvalue.dereferenceLevel > 0) {
							throw Helpers.nodeError(null, "Found invalid rvalue \"%s\" in value action \"%s\"!", rvalue, iva);
						}
					}
				}
			}
		}
	}
	
	public void checkFunctionVariableInitialization() {
		for (List<Action> list : body) {
			for (Action action : list) {
				if (action instanceof AssignmentAction) {
					AssignmentAction aa = (AssignmentAction) action;
					if (aa.arg instanceof ValueDataId) {
						ValueDataId valueData = (ValueDataId) aa.arg;
						if (valueData.value instanceof FunctionItemValue) {
							String functionName = ((FunctionItemValue) valueData.value).name;
							if (program.rootScope.functionExists(functionName) && !program.routineExists(functionName)) {
								throw Helpers.nodeError(null, "Function \"%s\" was not defined! %s", functionName, aa);
							}
						}
					}
				}
			}
		}
	}
	
	// Empty stack checks
	
	public void checkEmptyStacks() {
		checkEmptyStack("conditional section info", conditionalSectionInfoStack);
		checkEmptyStack("iterative section info", iterativeSectionInfoStack);
		checkEmptyStack("register data ID", regDataIdStack);
	}
	
	// General actions
	
	public void addAction(Action action) {
		currentSection().add(action);
	}
	
	public void addImmediateRegisterAssignmentAction(ASTNode node, @NonNull Value value) {
		addAction(new AssignmentAction(node, currentRegId(node), new ValueDataId(value)));
	}
	
	public void addAddressOfRegisterAssignmentAction(ASTNode node, @NonNull Variable variable) {
		addAction(new AssignmentAction(node, currentRegId(node), new VariableDataId(-1, variable)));
	}
	
	public void addRegisterAssignmentAction(ASTNode node, @NonNull Variable variable) {
		addAction(new AssignmentAction(node, currentRegId(node), new VariableDataId(0, variable)));
	}
	
	public void addAddressOfStackAssignmentAction(ASTNode node) {
		addAction(new AssignmentAction(node, currentRegId(node), regDataIdStack.pop().addAddressPrefix()));
	}
	
	public void addStackAssignmentAction(ASTNode node) {
		addAction(new AssignmentAction(node, currentRegId(node), regDataIdStack.pop()));
	}
	
	public void addStackCompoundAssignmentAction(ASTNode node, int length) {
		addAction(new CompoundAssignmentAction(node, currentRegId(node), Arrays.asList(popRegIds(length))));
	}
	
	public void addStackArrayRepeatAssignmentAction(ASTNode node, int length, @NonNull ArrayTypeInfo typeInfo) {
		DataId array = currentRegId(node), repeat = regDataIdStack.pop();
		addAction(new DeclarationAction(node, array, typeInfo));
		
		if (length == 0) {
			return;
		}
		else if (length == 1) {
			addAction(new AssignmentAction(node, array, repeat));
		}
		else {
			@NonNull TypeInfo offsetTypeInfo = typeInfo.elementTypeInfo.modifiedReferenceLevel(node, 1);
			FOR LOOP
		}
	}
	
	public void addStackDeclarationAction(ASTNode node, DeclaratorInfo declaratorInfo) {
		addAction(new DeclarationAction(node, declaratorInfo.dataId(), declaratorInfo.getTypeInfo()));
	}
	
	public void addStackInitializationAction(ASTNode node, DeclaratorInfo declaratorInfo) {
		addAction(new InitializationAction(node, declaratorInfo.dataId(), declaratorInfo.getTypeInfo(), regDataIdStack.pop()));
	}
	
	public void addStackLvalueAssignmentAction(ASTNode node) {
		addAction(new AssignmentAction(node, currentRegId(node).addDereference(), regDataIdStack.pop()));
	}
	
	public void addStackLvalueAssignmentOpAction(ASTNode node, @NonNull TypeInfo lvalueType, @NonNull BinaryOpType opType, @NonNull TypeInfo rvalueType) {
		DataId address = currentRegId(node);
		
		incrementRegId(lvalueType);
		DataId value = currentRegId(node);
		
		addAction(new AssignmentAction(node, value, address.addDereference()));
		
		incrementRegId(rvalueType);
		DataId result = currentRegId(node);
		
		Main.generator.binaryOp(node, this, lvalueType, opType, rvalueType, result, value, regDataIdStack.pop());
		addAction(new AssignmentAction(node, address.addDereference(), result));
	}
	
	public void addBinaryOpAction(ASTNode node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		DataId[] values = popRegIds(2);
		Main.generator.binaryOp(node, this, leftType, opType, rightType, currentRegId(node), values[0], values[1]);
	}
	
	public void addUnaryOpAction(ASTNode node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		Main.generator.unaryOp(node, this, opType, typeInfo, currentRegId(node), regDataIdStack.pop());
	}
	
	public void addDereferenceAction(ASTNode node) {
		addAction(new AssignmentAction(node, currentRegId(node), regDataIdStack.pop().addDereference()));
	}
	
	public void addExitAction(ASTNode node) {
		addAction(Global.EXIT_PROGRAM);
	}
	
	public void addExitValueAction(ASTNode node) {
		addAction(new ExitValueAction(node, currentRegId(node)));
	}
	
	public void addReturnAction(ASTNode node) {
		if (isRootRoutine()) {
			throw node.error("Root routine can not return! Use an exit statement!");
		}
		else {
			addAction(new JumpAction(node, Global.DESTRUCTOR));
		}
	}
	
	public void addReturnValueAction(ASTNode node) {
		addAction(new ReturnValueAction(node, currentRegId(node)));
	}
	
	public void addConditionalJumpAction(ASTNode node, String target, boolean jumpCondition) {
		addAction(new ConditionalJumpAction(node, target, jumpCondition));
	}
	
	public void addGotoAction(ASTNode node, String labelName) {
		addAction(new JumpAction(node, Helpers.sectionLabelString(labelName)));
	}
	
	public void addFunctionAction(ASTNode node, Function directFunction, int argc) {
		if (directFunction != null) {
			directFunction.required = true;
		}
		
		List<DataId> args = Arrays.asList(popRegIds(argc));
		DataId function = regDataIdStack.pop(), target = currentRegId(node);
		
		if (directFunction != null && directFunction.builtIn) {
			addAction(new BuiltInFunctionCallAction(node, target, function, args));
		}
		else {
			addAction(new FunctionCallAction(node, target, function, args));
		}
	}
	
	// Expression info
	
	protected void onPopExpressionInfo(ASTNode node, ExpressionInfo expressionInfo, boolean reversibleValue) {
		expressionInfo.checkExpectedType(node, generator);
		
		if (reversibleValue && expressionInfo.currentTypeInfo instanceof FunctionItemTypeInfo) {
			onReversibleFunctionItemExpressionInfo(node, ((FunctionItemTypeInfo) expressionInfo.currentTypeInfo).function);
		}
	}
	
	protected void onReversibleFunctionItemExpressionInfo(ASTNode node, Function function) {
		function.required = true;
		Routine functionRoutine = program.getRoutine(function.name);
		if (functionRoutine != null) {
			functionRoutine.onRequiresStack();
		}
	}
	
	// Conditional and iterative sections
	
	public ConditionalSectionInfo currentConditionalSectionInfo(ASTNode node) {
		if (conditionalSectionInfoStack.isEmpty()) {
			throw node.error("Unexpectedly attempted to get conditional section info!");
		}
		else {
			return conditionalSectionInfoStack.peek();
		}
	}
	
	public void addConditionalSectionElseJumpAction(ASTNode node) {
		ConditionalSectionInfo info = currentConditionalSectionInfo(node);
		if (!info.sectionStart) {
			body.get(info.getElseJumpSectionId(node)).add(new ConditionalJumpAction(node, sectionId, !info.getExecuteIfCondition(node)));
		}
	}
	
	public void addConditionalSectionExitJumpActions(ASTNode node) {
		for (int sectionId : currentConditionalSectionInfo(node).getExitJumpSectionIds(node)) {
			body.get(sectionId).add(new JumpAction(node, sectionId));
		}
	}
	
	public IterativeSectionInfo currentIterativeSectionInfo(ASTNode node) {
		if (iterativeSectionInfoStack.isEmpty()) {
			throw node.error("Unexpectedly attempted to get iterative section info!");
		}
		else {
			return iterativeSectionInfoStack.peek();
		}
	}
	
	public void addIterativeSectionContinueJumpAction(ASTNode node) {
		currentIterativeSectionInfo(node).continueJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATION_CONTINUE_PLACEHOLDER);
	}
	
	public void addIterativeSectionConditionalContinueJumpAction(ASTNode node, boolean continueIfCondition) {
		currentIterativeSectionInfo(node).continueJumpIterativeSectionIds.add(sectionId);
		addAction(continueIfCondition ? Global.ITERATION_CONDITIONAL_CONTINUE_PLACEHOLDER : Global.ITERATION_CONDITIONAL_NOT_CONTINUE_PLACEHOLDER);
	}
	
	public void addIterativeSectionBodyJumpAction(ASTNode node) {
		currentIterativeSectionInfo(node).bodyJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATION_BODY_JUMP_PLACEHOLDER);
	}
	
	public void addIterativeSectionConditionalBodyJumpAction(ASTNode node, boolean jumpIfCondition) {
		currentIterativeSectionInfo(node).bodyJumpIterativeSectionIds.add(sectionId);
		addAction(jumpIfCondition ? Global.ITERATION_CONDITIONAL_BODY_JUMP_PLACEHOLDER : Global.ITERATION_CONDITIONAL_NOT_BODY_JUMP_PLACEHOLDER);
	}
	
	public void addIterativeSectionBreakJumpAction(ASTNode node) {
		currentIterativeSectionInfo(node).breakJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATION_BREAK_PLACEHOLDER);
	}
	
	public void addIterativeSectionConditionalBreakJumpAction(ASTNode node, boolean breakIfCondition) {
		currentIterativeSectionInfo(node).breakJumpIterativeSectionIds.add(sectionId);
		addAction(breakIfCondition ? Global.ITERATION_CONDITIONAL_BREAK_PLACEHOLDER : Global.ITERATION_CONDITIONAL_NOT_BREAK_PLACEHOLDER);
	}
	
	public void finalizeIterativeSectionJumpActions(ASTNode node) {
		IterativeSectionInfo info = currentIterativeSectionInfo(node);
		
		for (int sectionId : info.getContinueJumpIterativeSectionIds(node)) {
			List<Action> list = body.get(sectionId);
			for (int i = 0; i < list.size(); ++i) {
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
			for (int i = 0; i < list.size(); ++i) {
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
			for (int i = 0; i < list.size(); ++i) {
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
	
	public void mapSectionLabel(ASTNode node, String labelName) {
		String sectionLabelString = Helpers.sectionLabelString(labelName);
		if (sectionLabelSectionIdMap.containsKey(sectionLabelString)) {
			if (isRootRoutine()) {
				throw node.error("Label name \"%s\" already used in root routine!", labelName);
			}
			else {
				throw node.error("Label name \"%s\" already used in function \"%s\"!", labelName, name);
			}
		}
		else {
			sectionLabelSectionIdMap.put(sectionLabelString, sectionId);
		}
	}
	
	public String getSectionLabelSectionId(String sectionLabelString) {
		if (sectionLabelSectionIdMap.containsKey(sectionLabelString)) {
			return Helpers.sectionIdString(sectionLabelSectionIdMap.get(sectionLabelString));
		}
		else {
			if (isRootRoutine()) {
				throw Helpers.nodeError(null, "Label name \"%s\" not found in root routine!", Helpers.parseSectionLabel(sectionLabelString));
			}
			else {
				throw Helpers.nodeError(null, "Label name \"%s\" not found in function \"%s\"!", Helpers.parseSectionLabel(sectionLabelString), name);
			}
		}
	}
	
	// Sections and registers
	
	public List<Action> getSection(int sectionId) {
		return body.get(sectionId);
	}
	
	public List<Action> currentSection() {
		return getSection(sectionId);
	}
	
	public void incrementSectionId() {
		++sectionId;
		body.add(new ArrayList<>());
	}
	
	public int currentSectionId() {
		return sectionId;
	}
	
	public void incrementRegId(@NonNull TypeInfo typeInfo) {
		regDataId = new RegDataId(typeInfo, regId++);
	}
	
	public RegDataId currentRegId(ASTNode node) {
		if (regDataId == null) {
			throw node.error("Current register is null!");
		}
		else {
			return regDataId;
		}
	}
	
	public void pushCurrentRegId(ASTNode node) {
		regDataIdStack.push(currentRegId(node));
	}
	
	public RegDataId[] popRegIds(int count) {
		RegDataId[] out = new RegDataId[count];
		for (int i = 0; i < count; ++i) {
			out[count - i - 1] = regDataIdStack.pop();
		}
		return out;
	}
	
	@Override
	public abstract String toString();
}
