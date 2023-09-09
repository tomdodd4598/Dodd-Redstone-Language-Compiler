package drlc.intermediate.routine;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.component.DeclaratorInfo;
import drlc.intermediate.component.type.TypeInfo;

public class RootRoutine extends Routine {
	
	public List<DeclaratorInfo> params;
	
	public RootRoutine() {
		super(Global.ROOT_ROUTINE);
		getDestructionActionList().add(Global.EXIT_PROGRAM);
	}
	
	@Override
	public RoutineCallType getType() {
		RoutineCallType type = super.getType();
		if (type.equals(RoutineCallType.STACK)) {
			throw new IllegalArgumentException(String.format("Root routine can not be stack-based!"));
		}
		return type;
	}
	
	@Override
	public void onRequiresNesting() {
		type = type.onRequiresNesting();
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
	public @NonNull TypeInfo getReturnTypeInfo() {
		throw new IllegalArgumentException(String.format("Unexpectedly attempted to get return type of root routine!"));
	}
	
	@Override
	public List<DeclaratorInfo> getParams() {
		return params;
	}
	
	@Override
	public String toString() {
		return Global.FN + ' ' + name + Helpers.listString(params) + " -> " + Main.generator.rootReturnTypeInfo;
	}
}
