package drlc.intermediate.component.type;

import drlc.Global;
import drlc.intermediate.Scope;
import drlc.node.Node;

public class NatTypeInfo extends BuiltInTypeInfo {
	
	protected NatTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public NatTypeInfo(Node node, Scope scope, int referenceLevel) {
		super(node, scope, Global.NAT, referenceLevel);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new NatTypeInfo(node, type, newReferenceLevel);
	}
	
	@Override
	public boolean isInteger(Node node) {
		return !isAddress(node);
	}
}
