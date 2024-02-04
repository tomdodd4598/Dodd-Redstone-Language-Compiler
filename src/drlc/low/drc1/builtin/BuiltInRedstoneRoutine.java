package drlc.low.drc1.builtin;

import drlc.intermediate.routine.*;
import drlc.low.drc1.*;

public abstract class BuiltInRedstoneRoutine extends RedstoneRoutine {
	
	protected RoutineCallType type;
	
	public BuiltInRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public abstract void generateInstructionsInternal();
	
	@Override
	public abstract short getFinalTextSectionKey();
	
	@Override
	public void onRequiresNesting() {
		type.onRequiresNesting();
	}
	
	@Override
	public void onRequiresStack() {
		throw new IllegalArgumentException(String.format("Built-in redstone routine \"%\" can not use stack!", function));
	}
	
	@Override
	public boolean isStackRoutine() {
		return type.equals(RoutineCallType.STACK);
	}
	
	@Override
	public boolean isRootRoutine() {
		return false;
	}
}
