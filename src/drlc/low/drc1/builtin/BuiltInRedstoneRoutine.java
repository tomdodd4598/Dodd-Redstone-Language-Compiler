package drlc.low.drc1.builtin;

import java.util.List;

import drlc.Main;
import drlc.intermediate.component.DeclaratorInfo;
import drlc.intermediate.routine.*;
import drlc.low.drc1.*;

public abstract class BuiltInRedstoneRoutine extends RedstoneRoutine {
	
	protected RoutineCallType type;
	
	public BuiltInRedstoneRoutine(RedstoneCode code, String name, RoutineCallType type, List<DeclaratorInfo> params) {
		super(code, name, type, params);
		this.type = type;
		mapParams();
	}
	
	private BuiltInRedstoneRoutine(RedstoneCode code, FunctionRoutine routine) {
		this(code, routine.name, routine.getType(), routine.getParams());
	}
	
	public BuiltInRedstoneRoutine(RedstoneCode code, String name) {
		this(code, Main.program.builtInRoutineMap.get(name));
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
		throw new IllegalArgumentException(String.format("Built-in redstone routines can not be stack-based!"));
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
