package drlc.intermediate.routine;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.ExitAction;
import drlc.intermediate.component.DeclaratorInfo;
import drlc.intermediate.component.data.ValueDataId;
import drlc.intermediate.component.type.TypeInfo;

public class RootRoutine extends Routine {
	
	public RootRoutine() {
		super(Global.ROOT_ROUTINE);
		getDestructionActionList().add(new ExitAction(null, new ValueDataId(Main.generator.intValue(0))));
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
		return new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return Global.FN + " " + name + Helpers.listString(Helpers.map(getParams(), DeclaratorInfo::routineString)) + " " + Global.ARROW + " " + Main.generator.rootReturnTypeInfo.routineString();
	}
}
