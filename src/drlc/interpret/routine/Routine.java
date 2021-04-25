package drlc.interpret.routine;

import java.util.*;

import drlc.*;
import drlc.interpret.Program;
import drlc.interpret.action.*;
import drlc.interpret.scope.*;
import drlc.node.Node;

public abstract class Routine {
	
	public final Program program;
	public final String name;
	
	protected RoutineType type = RoutineType.LEAF;
	public boolean called = false;
	
	private final List<List<Action>> body = new ArrayList<>();
	private final List<Action> destruction = new ArrayList<>();
	
	public int sectionId = 0;
	private String sectionIdString = Helper.sectionIdString(0);
	
	public Stack<ConditionalSectionInfo> conditionalSectionInfoStack = new Stack<>();
	public Stack<IterativeSectionInfo> iterativeSectionInfoStack = new Stack<>();
	
	private int regId = -1;
	private String regIdString = null;
	public Stack<String> regIdStack = new Stack<>();
	
	public Routine(Program program, String name) {
		this.program = program;
		this.name = name;
		body.add(new ArrayList<>());
	}
	
	public RoutineType getType() {
		return type;
	}
	
	protected abstract void onNestingRoutine();
	
	protected abstract void onRecursiveRoutine();
	
	public List<List<Action>> getBodyActionLists() {
		return body;
	}
	
	public List<Action> getDestructionActionList() {
		return destruction;
	}
	
	// Routine finalization
	
	public void setTransientRegisters() {
		for (int i = 0; i < body.size(); i++) {
			List<Action> list = body.get(i);
			for (int j = 1; j < list.size(); j++) {
				Action action = list.get(j), previous = list.get(j - 1);
				if (action instanceof ConditionalJumpAction) {
					ConditionalJumpAction cja = (ConditionalJumpAction) action;
					if (!(previous instanceof IValueAction)) {
						throw new IllegalArgumentException(String.format("Found unexpected action %s before conditional jump action %s!", previous, cja));
					}
					IValueAction iva = (IValueAction) previous;
					if (!iva.canReplaceLValue() || iva.lValues().length != 1) {
						throw new IllegalArgumentException(String.format("Found unexpected value action %s before conditional jump action %s!", iva, cja));
					}
					list.set(j - 1, iva.replaceLValue(iva.lValues()[0], Global.TRANSIENT));
				}
			}
		}
	}
	
	public void updateRoutineType() {
		updateRoutineTypeInternal(this, new HashSet<>());
	}
	
	protected void updateRoutineTypeInternal(Routine node, Set<String> checked) {
		for (List<Action> section : node.body) {
			for (Action action : section) {
				if (action instanceof SubroutineCallAction) {
					String name = ((SubroutineCallAction) action).name;
					if (!Global.BUILT_IN_METHODS.containsKey(name) && !Global.BUILT_IN_FUNCTIONS.containsKey(name)) {
						if (name.equals(this.name)) {
							onRecursiveRoutine();
						}
						else {
							onNestingRoutine();
						}
						if (!checked.contains(name)) {
							checked.add(name);
							updateRoutineTypeInternal(program.routineMap.get(name), checked);
						}
					}
				}
			}
		}
	}
	
	// General actions
	
	protected void addAction(Action action) {
		body.get(sectionId).add(action);
	}
	
	protected void checkConstantExists(Node node, Scope scope, String name, boolean expected, String error) {
		if (Character.isLetter(name.charAt(0)) && (expected ^ scope.constantExists(name))) {
			throw new IllegalArgumentException(String.format("Constant \"%s\" ".concat(error).concat(" %s"), name, node));
		}
	}
	
	protected void checkVariableExists(Node node, Scope scope, String name, boolean expected, String error) {
		if (Character.isLetter(name.charAt(0)) && (expected ^ scope.variableExists(name))) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" ".concat(error).concat(" %s"), name, node));
		}
	}
	
	protected void checkVariableInitialised(Node node, Scope scope, String name, String error) {
		if (Character.isLetter(name.charAt(0)) && !scope.getVariable(node, name).initialised) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" ".concat(error).concat(" %s"), name, node));
		}
	}
	
	public void addRegisterAssignmentAction(Node node, Scope scope, String arg) {
		if (scope.variableExists(arg)) {
			checkVariableInitialised(node, scope, arg, "not initialised!");
		}
		addAction(new AssignmentAction(node, currentRegId(node), arg));
	}
	
	public void addStackDeclarationAction(Node node, Scope scope, String target) {
		addAction(new DeclarationAction(node, target));
	}
	
	public void addStackInitialisationAction(Node node, Scope scope, String target) {
		addAction(new InitialisationAction(node, target, popRegIdStack(1)[0]));
	}
	
	public void addStackVariableAssignmentAction(Node node, Scope scope, String target) {
		checkConstantExists(node, scope, target, false, "can not be modified!");
		checkVariableExists(node, scope, target, true, "not defined in this scope!");
		scope.getVariable(node, target).initialised = true;
		addAction(new AssignmentAction(node, target, popRegIdStack(1)[0]));
	}
	
	public void addDereferenceAction(Node node, Scope scope, int dereferenceLevel) {
		addAction(new DereferenceAction(node, currentRegId(node), dereferenceLevel, popRegIdStack(1)[0]));
	}
	
	public void addUnaryOpAction(Node node, Scope scope, String operation) {
		addAction(new UnaryOpAction(node, currentRegId(node), operation, popRegIdStack(1)[0]));
	}
	
	public void addBinaryOpAction(Node node, Scope scope, String operation) {
		String[] popRegIdStack = popRegIdStack(2);
		addAction(new BinaryOpAction(node, currentRegId(node), popRegIdStack[0], operation, popRegIdStack[1]));
	}
	
	public void addJumpAction(Node node, String target) {
		addAction(new JumpAction(node, target));
	}
	
	public void addConditionalJumpAction(Node node, String target) {
		addAction(new ConditionalJumpAction(node, target));
	}
	
	public void addBuiltInMethodCallAction(Node node, Scope scope, String name) {
		if (!Global.BUILT_IN_METHODS.containsKey(name)) {
			throw new IllegalArgumentException(String.format("Built-in method \"%s\" not defined! %s", name, node));
		}
		else {
			addAction(new BuiltInMethodCallAction(node, name, popRegIdStack(scope.getMethod(node, name).args)));
		}
	}
	
	public void addBuiltInFunctionCallAction(Node node, Scope scope, String name) {
		if (!Global.BUILT_IN_FUNCTIONS.containsKey(name)) {
			throw new IllegalArgumentException(String.format("Built-in function \"%s\" not defined! %s", name, node));
		}
		else {
			addAction(new BuiltInFunctionCallAction(node, currentRegId(node), name, popRegIdStack(scope.getFunction(node, name).args)));
		}
	}
	
	public void addMethodSubroutineCallAction(Node node, Scope scope, String name) {
		if (!program.routineMap.containsKey(name)) {
			throw new IllegalArgumentException(String.format("Subroutine \"%s\" not defined! %s", name, node));
		}
		else {
			program.routineMap.get(name).called = true;
			addAction(new MethodCallAction(node, name, popRegIdStack(scope.getMethod(node, name).args)));
		}
	}
	
	public void addFunctionSubroutineCallAction(Node node, Scope scope, String name) {
		if (!program.routineMap.containsKey(name)) {
			throw new IllegalArgumentException(String.format("Subroutine \"%s\" not defined! %s", name, node));
		}
		else {
			program.routineMap.get(name).called = true;
			addAction(new FunctionCallAction(node, currentRegId(node), name, popRegIdStack(scope.getFunction(node, name).args)));
		}
	}
	
	public void addFunctionReturnAction(Node node, Scope scope) {
		scope.checkExpectingFunctionReturn(node, true);
		addAction(new ReturnValueAction(node, currentRegId(node)));
	}
	
	// Special conditional and iterative section actions
	
	public ConditionalSectionInfo currentConditionalSectionInfo() {
		return conditionalSectionInfoStack.peek();
	}
	
	public void addConditionalSectionSkipJumpActionAndIncrementSectionId(Node node) {
		List<Action> previous = body.get(sectionId);
		incrementSectionId();
		previous.add(new ConditionalJumpAction(node, sectionIdString));
	}
	
	public void addConditionalSectionElseJumpAction(Node node, Scope scope) {
		body.get(currentConditionalSectionInfo().getElseJumpConditionalSectionId(node)).add(new JumpAction(node, sectionIdString));
	}
	
	public void addConditionalSectionExitJumpActions(Node node, Scope scope) {
		for (int sectionId : currentConditionalSectionInfo().getExitJumpConditionalSectionIds(node)) {
			body.get(sectionId).add(new JumpAction(node, sectionIdString));
		}
	}
	
	public IterativeSectionInfo currentIterativeSectionInfo() {
		return iterativeSectionInfoStack.peek();
	}
	
	public void addIterativeSectionContinueAction(Node node, Scope scope) {
		addJumpAction(node, currentIterativeSectionInfo().getIterativeContinueJumpSectionId(node));
	}
	
	public void addIterativeSectionBreakAction(Scope scope) {
		currentIterativeSectionInfo().exitJumpIterativeSectionIds.add(sectionId);
		addAction(Global.ITERATOR_BREAK_PLACEHOLDER);
	}
	
	public void addIterativeSectionJumpActionsAndIncrementSectionId(Node node, Scope scope) {
		currentIterativeSectionInfo().exitJumpIterativeSectionIds.add(sectionId);
		List<Action> previous = body.get(sectionId);
		incrementSectionId();
		previous.add(new ConditionalJumpAction(node, sectionIdString));
		previous.add(Global.ITERATOR_BREAK_PLACEHOLDER);
	}
	
	public void addIterativeSectionExitJumpActions(Node node, Scope scope) {
		for (int sectionId : currentIterativeSectionInfo().getExitJumpIterativeSectionIds(node)) {
			List<Action> list = body.get(sectionId);
			for (int i = 0; i < list.size(); i++) {
				Action action = list.get(i);
				if (Global.ITERATOR_BREAK_PLACEHOLDER.equals(action)) {
					list.set(i, new JumpAction(node, sectionIdString));
				}
			}
		}
	}
	
	// Sections and registers
	
	public void incrementSectionId() {
		++sectionId;
		sectionIdString = Helper.sectionIdString(sectionId);
		body.add(new ArrayList<>());
	}
	
	public String currentSectionId() {
		return sectionIdString;
	}
	
	public void incrementRegId() {
		++regId;
		regIdString = Helper.regIdString(regId);
	}
	
	public String currentRegId(Node node) {
		if (regIdString == null) {
			throw new IllegalArgumentException(String.format("Current register is null! %s", node));
		}
		else {
			return regIdString;
		}
	}
	
	public void pushCurrentRegIdToStack(Node node) {
		regIdStack.push(currentRegId(node));
	}
	
	protected String[] popRegIdStack(int count) {
		String[] out = new String[count];
		for (int i = 0; i < count; i++) {
			out[count - i - 1] = regIdStack.pop();
		}
		return out;
	}
	
	@Override
	public abstract String toString();
}
