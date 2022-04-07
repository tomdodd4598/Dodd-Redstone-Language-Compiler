package drlc.generate.drc1.builtin;

import drlc.generate.drc1.*;
import drlc.interpret.component.info.DeclaratorInfo;
import drlc.interpret.routine.RoutineType;

public abstract class BuiltInRedstoneRoutine extends RedstoneRoutine {
	
	protected RoutineType type;
	
	public BuiltInRedstoneRoutine(RedstoneCode code, String name, RoutineType type, DeclaratorInfo[] params) {
		super(code, name, type, params);
		this.type = type;
		mapParams();
	}
	
	public BuiltInRedstoneRoutine(RedstoneCode code, String name) {
		this(code, name, code.program.getBuiltInRoutineMap().get(name).getType(), code.program.generator.builtInFunctionMap.get(name).params);
	}
	
	@Override
	public abstract void generateInstructionsInternal();
	
	@Override
	public abstract short getFinalTextSectionKey();
	
	@Override
	public void onRequiresNesting() {
		type.onNesting();
	}
	
	@Override
	public void onRequiresStack() {
		throw new IllegalArgumentException(String.format("Built-in redstone routines can not be stack-based!"));
	}
	
	@Override
	public boolean isStackRoutine() {
		return type.equals(RoutineType.STACK);
	}
	
	@Override
	public boolean isRootRoutine() {
		return false;
	}
}
