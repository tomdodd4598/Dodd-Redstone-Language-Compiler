package drlc.interpret.routine;

import drlc.Global;
import drlc.interpret.Program;
import drlc.interpret.type.*;

public class MethodRoutine extends Subroutine {
	
	public final Method method;
	
	public MethodRoutine(Program program, String name, Method method, VariableReferenceInfo[] params) {
		super(program, name, params);
		this.method = method;
		getDestructionActionList().add(Global.RETURN_FROM_SUBROUTINE);
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
