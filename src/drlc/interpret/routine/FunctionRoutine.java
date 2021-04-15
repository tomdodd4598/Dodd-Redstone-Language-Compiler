package drlc.interpret.routine;

import drlc.interpret.Program;
import drlc.interpret.type.Function;

public class FunctionRoutine extends Subroutine {
	
	public final Function function;
	
	public FunctionRoutine(Program program, String name, Function function, String[] params) {
		super(program, name, params);
		this.function = function;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		for (String param : params) {
			builder.append(" ").append(param);
		}
		return builder.toString();
	}
}
