package drlc.intermediate.component.type;

import drlc.Global;
import drlc.intermediate.Scope;
import drlc.node.Node;

public class IntTypeInfo extends BuiltInTypeInfo {
	
	protected IntTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public IntTypeInfo(Node node, Scope scope, int referenceLevel) {
		super(node, scope, Global.INT, referenceLevel);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new IntTypeInfo(node, type, newReferenceLevel);
	}
	
	@Override
	public boolean isInteger(Node node) {
		return !isAddress(node);
	}
}
