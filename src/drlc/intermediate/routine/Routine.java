package drlc.intermediate.routine;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;

public abstract class Routine {
	
	public final String name;
	
	protected RoutineCallType type = RoutineCallType.LEAF;
	
	private final List<List<Action>> body = new ArrayList<>();
	private final List<Action> destruction = new ArrayList<>();
	
	public int sectionId = 0;
	
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
		int destructorSectionId = body.size();
		body.add(getDestructionActionList());
		for (List<Action> list : body) {
			for (int j = 0; j < list.size(); ++j) {
				Action action = list.get(j);
				if (action instanceof IJumpAction) {
					IJumpAction jump = (IJumpAction) action;
					if (jump.getTarget() == Integer.MAX_VALUE) {
						list.set(j, jump.copy(destructorSectionId));
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
						throw Helpers.error("Found unexpected action \"%s\" before conditional jump action \"%s\"!", previous, cja);
					}
					IValueAction iva = (IValueAction) previous;
					DataId[] lvalues = iva.lvalues();
					if (!iva.canReplaceLvalue() || lvalues.length != 1) {
						throw Helpers.error("Found unexpected value action \"%s\" before conditional jump action \"%s\"!", iva, cja);
					}
					list.set(j - 1, iva.setTransientLvalue());
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
							if (Main.rootScope.functionExists(functionName) && !Main.program.routineExists(functionName)) {
								throw Helpers.error("Function \"%s\" was not defined! %s", functionName, aa);
							}
						}
					}
				}
			}
		}
	}
	
	// General actions
	
	public void addAction(Action action) {
		currentSection().add(action);
	}
	
	public void addImmediateRegisterAssignmentAction(ASTNode<?, ?> node, @NonNull Value value) {
		addAction(new AssignmentAction(node, currentRegId(node), new ValueDataId(value)));
	}
	
	public void addAddressOfRegisterAssignmentAction(ASTNode<?, ?> node, @NonNull Variable variable) {
		addAction(new AssignmentAction(node, currentRegId(node), new VariableDataId(-1, variable)));
	}
	
	public void addRegisterAssignmentAction(ASTNode<?, ?> node, @NonNull Variable variable) {
		addAction(new AssignmentAction(node, currentRegId(node), new VariableDataId(0, variable)));
	}
	
	public void addAddressOfStackAssignmentAction(ASTNode<?, ?> node) {
		addAction(new AssignmentAction(node, currentRegId(node), regDataIdStack.pop().addAddressPrefix(node)));
	}
	
	public void addStackAssignmentAction(ASTNode<?, ?> node) {
		addAction(new AssignmentAction(node, currentRegId(node), regDataIdStack.pop()));
	}
	
	public void addStackCompoundAssignmentAction(ASTNode<?, ?> node, int length) {
		if (length == 0) {
			return;
		}
		else if (length == 1) {
			addStackAssignmentAction(node);
		}
		else {
			addAction(new CompoundAssignmentAction(node, currentRegId(node), Arrays.asList(popRegIds(length))));
		}
	}
	
	public void addStackArrayRepeatAssignmentAction(ASTNode<?, ?> node, int length, DataId repeat, @NonNull ArrayTypeInfo typeInfo) {
		if (length == 0) {
			return;
		}
		else if (length == 1) {
			incrementRegId(typeInfo);
			addAction(new AssignmentAction(node, currentRegId(node), repeat));
		}
		else {
			// TODO
			@NonNull TypeInfo offsetTypeInfo = typeInfo.elementTypeInfo.modifiedReferenceLevel(node, 1);
			
			incrementRegId(offsetTypeInfo);
			DataId arrayStart = currentRegId(node);
			
			incrementRegId(offsetTypeInfo);
			addAction(new AssignmentAction(node, currentRegId(node), arrayStart));
			incrementRegId(offsetTypeInfo);
			DataId arrayEnd = currentRegId(node);
			Main.generator.binaryOp(node, this, offsetTypeInfo, BinaryOpType.PLUS, Main.generator.intTypeInfo, arrayEnd, arrayStart, new ValueDataId(Main.generator.sizeValue(length)));
			JumpAction ja = addJumpAction(node, -1);
			
			incrementSectionId();
			int cjTarget = currentSectionId();
			addAction(new AssignmentAction(node, currentRegId(node), repeat));
			
			incrementSectionId();
			ja.setTarget(currentSectionId());
			
			addConditionalJumpAction(node, cjTarget, true);
		}
	}
	
	public void addStackDeclarationAction(ASTNode<?, ?> node, DeclaratorInfo declaratorInfo) {
		addAction(new DeclarationAction(node, declaratorInfo.dataId(), declaratorInfo.getTypeInfo()));
	}
	
	public void addStackInitializationAction(ASTNode<?, ?> node, DeclaratorInfo declaratorInfo) {
		addAction(new InitializationAction(node, declaratorInfo.dataId(), declaratorInfo.getTypeInfo(), regDataIdStack.pop()));
	}
	
	public void addStackLvalueAssignmentAction(ASTNode<?, ?> node) {
		addAction(new AssignmentAction(node, currentRegId(node).addDereference(node), regDataIdStack.pop()));
	}
	
	public void addStackLvalueAssignmentOpAction(ASTNode<?, ?> node, @NonNull TypeInfo lvalueType, @NonNull BinaryOpType opType, @NonNull TypeInfo rvalueType) {
		DataId address = currentRegId(node), deref = address.addDereference(node);
		
		incrementRegId(lvalueType);
		DataId value = currentRegId(node);
		
		addAction(new AssignmentAction(node, value, deref));
		
		incrementRegId(rvalueType);
		DataId result = currentRegId(node);
		
		Main.generator.binaryOp(node, this, lvalueType, opType, rvalueType, result, value, regDataIdStack.pop());
		addAction(new AssignmentAction(node, deref, result));
	}
	
	public void addBinaryOpAction(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		DataId[] values = popRegIds(2);
		Main.generator.binaryOp(node, this, leftType, opType, rightType, currentRegId(node), values[0], values[1]);
	}
	
	public void addUnaryOpAction(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		Main.generator.unaryOp(node, this, opType, typeInfo, currentRegId(node), regDataIdStack.pop());
	}
	
	public void addDereferenceAction(ASTNode<?, ?> node) {
		addAction(new AssignmentAction(node, currentRegId(node), regDataIdStack.pop().addDereference(node)));
	}
	
	public void addExitAction(ASTNode<?, ?> node) {
		addAction(Global.EXIT_PROGRAM);
	}
	
	public void addExitValueAction(ASTNode<?, ?> node) {
		addAction(new ExitValueAction(node, currentRegId(node)));
	}
	
	public void addReturnAction(ASTNode<?, ?> node) {
		if (isRootRoutine()) {
			throw Helpers.nodeError(node, "Root routine can not return! Use an exit statement!");
		}
		else {
			addAction(new JumpAction(node, Integer.MAX_VALUE));
		}
	}
	
	public void addReturnValueAction(ASTNode<?, ?> node) {
		addAction(new ReturnValueAction(node, currentRegId(node)));
	}
	
	public JumpAction addJumpAction(ASTNode<?, ?> node, int target) {
		JumpAction ja = new JumpAction(node, target);
		addAction(ja);
		return ja;
	}
	
	public ConditionalJumpAction addConditionalJumpAction(ASTNode<?, ?> node, int target, boolean jumpCondition) {
		ConditionalJumpAction cja = new ConditionalJumpAction(node, target, jumpCondition);
		addAction(cja);
		return cja;
	}
	
	public void addFunctionAction(ASTNode<?, ?> node, Function directFunction, int argc) {
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
	
	public void onNonLocalFunctionItemExpression(ASTNode<?, ?> node, Function function) {
		function.required = true;
		Routine functionRoutine = Main.program.getRoutine(function.name);
		if (functionRoutine != null) {
			functionRoutine.onRequiresStack();
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
	
	public RegDataId currentRegId(ASTNode<?, ?> node) {
		if (regDataId == null) {
			throw Helpers.nodeError(node, "Current register is null!");
		}
		else {
			return regDataId;
		}
	}
	
	public void pushCurrentRegId(ASTNode<?, ?> node) {
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
