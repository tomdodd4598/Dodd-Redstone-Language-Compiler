package drlc.interpret.routine;

import drlc.Global;
import drlc.interpret.Program;
import drlc.interpret.type.Method;

public class MethodRoutine extends Subroutine {
	
	public final Method method;
	
	public MethodRoutine(Program program, String name, Method method, String[] params) {
		super(program, name, params);
		this.method = method;
		getDestructionActionList().add(Global.RETURN_FROM_SUBROUTINE);
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
