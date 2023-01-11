package drlc.low.drc1.builtin;

import drlc.intermediate.component.info.DeclaratorInfo;
import drlc.intermediate.routine.RoutineType;
import drlc.low.drc1.*;

public abstract class BuiltInRedstoneRoutine extends RedstoneRoutine {
	
	protected RoutineType type;
	
	public BuiltInRedstoneRoutine(RedstoneCode code, String name, RoutineType type, DeclaratorInfo[] params) {
		super(code, name, type, params);
		this.type = type;
		mapParams();
	}
	
	public BuiltInRedstoneRoutine(RedstoneCode code, String name) {
		this(code, name, code.generator.program.builtInRoutineMap.get(name).getType(), code.generator.builtInFunctionMap.get(name).params);
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
