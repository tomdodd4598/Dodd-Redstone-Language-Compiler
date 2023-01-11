package drlc.intermediate.component.info;

import drlc.intermediate.component.Variable;
import drlc.node.Node;

public class LvalueParseInfo {
	
	public final Node node;
	
	protected Variable variable = null;
	public boolean variableIsNull = false;
	public int dereferenceLevel = 0;
	
	public LvalueParseInfo(Node node) {
		this.node = node;
	}
	
	public Variable getVariable() {
		return variableIsNull ? null : variable;
	}
	
	public void setVariable(Variable variable) {
		if (variableIsNull) {
			this.variable = null;
		}
		else {
			this.variable = variable;
			variableIsNull = variable == null;
		}
	}
	
	public void checkIsValid() {
		if (variable == null && dereferenceLevel == 0) {
			throw new IllegalArgumentException(String.format("Attempted to parse invalid lvalue expression! %s", node));
		}
	}
}
