package drlc.intermediate.component.type;

import drlc.Global;
import drlc.intermediate.Scope;
import drlc.node.Node;

public class BoolTypeInfo extends BuiltInTypeInfo {
	
	protected BoolTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public BoolTypeInfo(Node node, Scope scope, int referenceLevel) {
		super(node, scope, Global.BOOL, referenceLevel);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new BoolTypeInfo(node, type, newReferenceLevel);
	}
}
