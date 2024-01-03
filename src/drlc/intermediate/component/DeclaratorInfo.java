package drlc.intermediate.component;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.type.TypeInfo;

public class DeclaratorInfo {
	
	public final @NonNull Variable variable;
	
	public DeclaratorInfo(@NonNull Variable variable) {
		this.variable = variable;
	}
	
	public DataId dataId() {
		return new VariableDataId(0, variable);
	}
	
	public @NonNull TypeInfo getTypeInfo() {
		return variable.typeInfo;
	}
	
	@Override
	public String toString() {
		return Helpers.scopeStringPrefix(variable.scope) + variable;
	}
	
	public String routineString() {
		return Helpers.scopeStringPrefix(variable.scope) + variable.routineString();
	}
}
