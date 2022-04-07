package drlc.interpret.routine;

import drlc.*;
import drlc.interpret.Program;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.type.TypeInfo;

public class RootRoutine extends Routine {
	
	public DeclaratorInfo[] params;
	
	public RootRoutine(Program program) {
		super(program, Global.ROOT_ROUTINE);
		getDestructionActionList().add(Global.EXIT_PROGRAM);
	}
	
	@Override
	public RoutineType getType() {
		RoutineType type = super.getType();
		if (type.equals(RoutineType.STACK)) {
			throw new IllegalArgumentException(String.format("Root routine can not be stack-based!"));
		}
		return type;
	}
	
	@Override
	public void onRequiresNesting(boolean force) {
		baseType = baseType.onNesting();
	}
	
	@Override
	public void onRequiresStack(boolean force) {
		throw new IllegalArgumentException(String.format("Root routine can not be stack-based!"));
	}
	
	@Override
	public boolean isRootRoutine() {
		return true;
	}
	
	@Override
	public TypeInfo getReturnTypeInfo() {
		throw new IllegalArgumentException(String.format("Unexpectedly attempted to get return type of root routine!"));
	}
	
	@Override
	public DeclaratorInfo[] getParams() {
		return params;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Global.FUN).append(' ').append(name);
		Helpers.appendParams(builder, params);
		return builder.append(' ').append(Global.INT).toString();
	}
}
