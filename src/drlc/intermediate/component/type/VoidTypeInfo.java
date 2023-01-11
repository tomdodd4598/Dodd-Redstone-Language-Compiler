package drlc.intermediate.component.type;

import drlc.Global;
import drlc.intermediate.Scope;
import drlc.node.Node;

public class VoidTypeInfo extends BuiltInTypeInfo {
	
	protected VoidTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public VoidTypeInfo(Node node, Scope scope, int referenceLevel) {
		super(node, scope, Global.VOID, referenceLevel);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new VoidTypeInfo(node, type, newReferenceLevel);
	}
	
	@Override
	public boolean isVoid(Node node) {
		return !isAddress(node);
	}
}
