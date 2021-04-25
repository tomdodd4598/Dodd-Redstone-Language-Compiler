package drlc.interpret.type;

import drlc.*;

public class VariableReferenceInfo {
	
	public final Variable variable;
	public int dereferenceLevel = 0;
	
	public VariableReferenceInfo(Variable variable) {
		this.variable = variable;
	}
	
	@Override
	public String toString() {
		if (dereferenceLevel < 0) {
			throw new IllegalArgumentException(String.format("Can not get the address of a raw variable \"%s\"!", variable.name));
		}
		else if (dereferenceLevel == 0) {
			return variable.name;
		}
		else {
			return Helper.charLine(Global.DEREFERENCE, dereferenceLevel).concat(variable.name);
		}
	}
}
