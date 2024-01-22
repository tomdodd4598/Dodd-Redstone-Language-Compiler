package drlc.intermediate.routine;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class Routine {
	
	public final @NonNull Function function;
	
	protected RoutineCallType type = RoutineCallType.LEAF;
	
	public final Map<String, TypeInfo> typeDefMap = new LinkedHashMap<>();
	public final List<DeclaratorInfo> declaratorList = new ArrayList<>();
	
	private final List<List<Action>> body = new ArrayList<>();
	private final List<Action> destruction = new ArrayList<>();
	
	public int sectionId = 0;
	
	private long regId = 0;
	
	public Routine(@NonNull Function function) {
		this.function = function;
		body.add(new ArrayList<>());
		if (function.returnTypeInfo.equals(Main.generator.unitTypeInfo)) {
			getDestructionActionList().add(new ReturnAction(null, Main.generator.unitValue.dataId()));
		}
	}
	
	public RoutineCallType getType() {
		return type;
	}
	
	public void onRequiresNesting() {
		type = type.onRequiresNesting();
	}
	
	public void onRequiresStack() {
		type = type.onRequiresRecursion();
	}
	
	public boolean isLeafRoutine() {
		return type.equals(RoutineCallType.LEAF);
	}
	
	public boolean isNestingRoutine() {
		return type.equals(RoutineCallType.NESTING);
	}
	
	public boolean isStackRoutine() {
		return type.equals(RoutineCallType.STACK);
	}
	
	public boolean isBuiltInFunctionRoutine() {
		return function.builtIn;
	}
	
	public @NonNull TypeInfo getReturnTypeInfo() {
		return function.returnTypeInfo;
	}
	
	public List<DeclaratorInfo> getParams() {
		return function.params;
	}
	
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
							Function function = ((FunctionItemValue) valueData.value).typeInfo.function;
							if (function.scope.functionExists(function.name, false) && !Main.rootScope.routineExists(function)) {
								throw Helpers.error("Function \"%s\" was not defined! %s", function, aa);
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
	
	public void addValueAssignmentAction(ASTNode<?> node, DataId target, @NonNull Value<?> value) {
		addAssignmentAction(node, target, value.dataId());
	}
	
	public void addAddressVariableAssignmentAction(ASTNode<?> node, DataId target, @NonNull Variable variable) {
		addAssignmentAction(node, target, new VariableDataId(-1, variable));
	}
	
	public void addVariableAssignmentAction(ASTNode<?> node, DataId target, @NonNull Variable variable) {
		addAssignmentAction(node, target, new VariableDataId(0, variable));
	}
	
	public void addDereferenceAssignmentAction(ASTNode<?> node, DataId target, DataId arg) {
		addAssignmentAction(node, target, arg.addDereference(node));
	}
	
	/** Type(&...&T), Data(&...&T) -> Data(T) */
	public @NonNull DataId addSelfDereferenceAssignmentAction(ASTNode<?> node, int dereferenceLevel, @NonNull DataId dataId) {
		for (int i = 0; i < dereferenceLevel; ++i) {
			@NonNull DataId arg = dataId;
			addDereferenceAssignmentAction(node, dataId = nextRegId(dataId.typeInfo.dereference(node, 1)), arg);
		}
		return dataId;
	}
	
	public void addAddressAssignmentAction(ASTNode<?> node, DataId target, DataId arg) {
		addAssignmentAction(node, target, arg.removeDereference(node));
	}
	
	/** Type(&...&T), Data(T) -> Data(&...&T) */
	public @NonNull DataId addSelfAddressAssignmentAction(ASTNode<?> node, Scope scope, int referenceLevel, @NonNull DataId dataId) {
		for (int i = 0; i < referenceLevel; ++i) {
			@NonNull DataId arg = dataId;
			@NonNull TypeInfo nextTypeInfo = dataId.typeInfo.addressOf(node, true);
			addAddressAssignmentAction(node, dataId = i == referenceLevel - 1 ? nextRegId(nextTypeInfo) : scope.nextLocalDataId(this, nextTypeInfo), arg);
		}
		return dataId;
	}
	
	public void addAssignmentAction(ASTNode<?> node, DataId target, DataId arg) {
		addAction(new AssignmentAction(node, target, arg));
	}
	
	public void addCompoundAssignmentAction(ASTNode<?> node, DataId target, List<DataId> args) {
		addAction(new CompoundAssignmentAction(node, target, args));
	}
	
	public void addBinaryOpAction(ASTNode<?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType, DataId target, DataId arg1, DataId arg2) {
		Main.generator.binaryOpAction(node, this, leftType, opType, rightType, target, arg1, arg2);
	}
	
	public void addUnaryOpAction(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo, DataId target, DataId arg) {
		Main.generator.unaryOpAction(node, this, opType, typeInfo, target, arg);
	}
	
	public void addTypeCastAction(ASTNode<?> node, Scope scope, @NonNull TypeInfo castType, @NonNull TypeInfo typeInfo, DataId target, DataId arg) {
		Main.generator.typeCastAction(node, scope, this, castType, typeInfo, target, arg);
	}
	
	public void addExitAction(ASTNode<?> node, DataId arg) {
		addAction(new ExitAction(node, arg));
	}
	
	public void addReturnAction(ASTNode<?> node, DataId arg) {
		addAction(new ReturnAction(node, arg));
	}
	
	public JumpAction addJumpAction(ASTNode<?> node, int target) {
		JumpAction ja = new JumpAction(node, target);
		addAction(ja);
		return ja;
	}
	
	public ConditionalJumpAction addConditionalJumpAction(ASTNode<?> node, int target, boolean jumpCondition) {
		ConditionalJumpAction cja = new ConditionalJumpAction(node, target, jumpCondition);
		addAction(cja);
		return cja;
	}
	
	public void addCallAction(ASTNode<?> node, Scope scope, Function directFunction, DataId target, DataId caller, List<DataId> args) {
		if (directFunction != null) {
			directFunction.setRequired(false);
		}
		
		if (directFunction != null && directFunction.builtIn) {
			addAction(new BuiltInCallAction(node, scope, target, caller, args));
		}
		else {
			addAction(new CallAction(node, scope, target, caller, args));
		}
	}
	
	public void onNonLocalFunctionItemExpression(ASTNode<?> node, Function function) {
		onRequiresStack();
		function.setRequired(true);
		if (Main.rootScope.routineExists(function)) {
			Main.rootScope.getRoutine(node, function).onRequiresStack();
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
	
	public @NonNull RegDataId nextRegId(@NonNull TypeInfo typeInfo) {
		return new RegDataId(0, typeInfo, regId++);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(function);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Routine) {
			Routine other = (Routine) obj;
			return function.equals(other.function);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return function.routineString();
	}
}
