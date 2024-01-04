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

public abstract class Routine {
	
	public final String name;
	
	protected RoutineCallType type = RoutineCallType.LEAF;
	
	public final Map<String, TypeInfo> typedefMap = new LinkedHashMap<>();
	public final List<DeclaratorInfo> declaratorList = new ArrayList<>();
	
	private final List<List<Action>> body = new ArrayList<>();
	private final List<Action> destruction = new ArrayList<>();
	
	public int sectionId = 0;
	
	private long regId = 0;
	
	public Scope scope;
	
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
							if (Main.rootScope.functionExists(functionName, false) && !Main.rootScope.routineExists(functionName, false)) {
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
	
	public void addValueAssignmentAction(ASTNode<?, ?> node, DataId target, @NonNull Value value) {
		addAction(new AssignmentAction(node, target, new ValueDataId(value)));
	}
	
	public void addAddressVariableAssignmentAction(ASTNode<?, ?> node, DataId target, @NonNull Variable variable) {
		addAction(new AssignmentAction(node, target, new VariableDataId(-1, variable)));
	}
	
	public void addVariableAssignmentAction(ASTNode<?, ?> node, DataId target, @NonNull Variable variable) {
		addAction(new AssignmentAction(node, target, new VariableDataId(0, variable)));
	}
	
	public void addAddressAssignmentAction(ASTNode<?, ?> node, DataId target, DataId arg) {
		addAction(new AssignmentAction(node, target, arg.removeDereference(node)));
	}
	
	public void addAssignmentAction(ASTNode<?, ?> node, DataId target, DataId arg) {
		addAction(new AssignmentAction(node, target, arg));
	}
	
	public void addCompoundAssignmentAction(ASTNode<?, ?> node, DataId target, List<DataId> args) {
		addAction(new CompoundAssignmentAction(node, target, args));
	}
	
	public void addLvalueAssignmentAction(ASTNode<?, ?> node, DataId target, DataId arg) {
		addAction(new AssignmentAction(node, target.addDereference(node), arg));
	}
	
	public void addLvalueAssignmentOpAction(ASTNode<?, ?> node, @NonNull TypeInfo lvalueType, @NonNull BinaryOpType opType, @NonNull TypeInfo rvalueType, DataId target, DataId arg) {
		target = target.addDereference(node);
		
		DataId original = nextRegId(lvalueType);
		addAction(new AssignmentAction(node, original, target));
		
		Main.generator.binaryOp(node, this, lvalueType, opType, rvalueType, target, original, arg);
	}
	
	public void addBinaryOpAction(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType, DataId target, DataId arg1, DataId arg2) {
		Main.generator.binaryOp(node, this, leftType, opType, rightType, target, arg1, arg2);
	}
	
	public void addUnaryOpAction(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo, DataId target, DataId arg) {
		Main.generator.unaryOp(node, this, opType, typeInfo, target, arg);
	}
	
	public void addDereferenceAssignmentAction(ASTNode<?, ?> node, DataId target, DataId arg) {
		addAction(new AssignmentAction(node, target, arg.addDereference(node)));
	}
	
	public void addExitAction(ASTNode<?, ?> node, DataId arg) {
		addAction(new ExitAction(node, arg));
	}
	
	public void addReturnAction(ASTNode<?, ?> node, DataId arg) {
		addAction(new ReturnAction(node, arg));
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
	
	public void addFunctionAction(ASTNode<?, ?> node, Function directFunction, DataId target, DataId function, List<DataId> args, Scope scope) {
		if (directFunction != null) {
			directFunction.setRequired(false);
		}
		
		if (directFunction != null && directFunction.builtIn) {
			addAction(new BuiltInFunctionCallAction(node, target, function, args, scope));
		}
		else {
			addAction(new FunctionCallAction(node, target, function, args, scope));
		}
	}
	
	public void onNonLocalFunctionItemExpression(ASTNode<?, ?> node, Function function) {
		function.setRequired(true);
		if (Main.rootScope.routineExists(function.name, false)) {
			Main.rootScope.getRoutine(node, function.name).onRequiresStack();
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
		return Objects.hash(name, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Routine) {
			Routine other = (Routine) obj;
			return name.equals(other.name) && Objects.equals(scope, other.scope);
		}
		return false;
	}
	
	@Override
	public abstract String toString();
}
