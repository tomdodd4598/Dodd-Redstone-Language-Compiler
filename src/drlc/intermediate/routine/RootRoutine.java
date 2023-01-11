package drlc.intermediate.routine;

import drlc.*;
import drlc.intermediate.component.info.DeclaratorInfo;
import drlc.intermediate.component.type.TypeInfo;

public class RootRoutine extends Routine {
	
	public DeclaratorInfo[] params;
	
	public RootRoutine(Generator generator) {
		super(generator, Global.ROOT_ROUTINE);
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
	public void onRequiresNesting() {
		type = type.onNesting();
	}
	
	@Override
	public void onRequiresStack() {
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
		builder.append(Global.FN).append(' ').append(name);
		Helpers.appendParams(builder, params);
		return builder.append(' ').append(Global.INT).toString();
	}
}
