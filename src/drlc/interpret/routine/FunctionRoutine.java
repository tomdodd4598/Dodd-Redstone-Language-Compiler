package drlc.interpret.routine;

import drlc.interpret.Program;
import drlc.interpret.type.*;
import drlc.interpret.type.info.VariableReferenceInfo;

public class FunctionRoutine extends Subroutine {
	
	public final Function function;
	
	public FunctionRoutine(Program program, String name, Function function, VariableReferenceInfo[] params) {
		super(program, name, params);
		this.function = function;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		for (VariableReferenceInfo param : params) {
			builder.append(" ").append(param.toString());
		}
		return builder.toString();
	}
}
