package drlc.interpret.component.info;

import drlc.interpret.component.Variable;
import drlc.node.Node;

public class LvalueParseInfo {
	
	public final Node node;
	
	public Variable variable = null;
	public int dereferenceLevel = 0;
	
	public LvalueParseInfo(Node node) {
		this.node = node;
	}
	
	public void error() {
		throw new IllegalArgumentException(String.format("Attempted to parse invalid lvalue expression! %s", node));
	}
	
	public void checkIsValid() {
		if (variable == null && dereferenceLevel == 0) {
			error();
		}
	}
}
