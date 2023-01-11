package drlc.intermediate.component.type;

import drlc.Global;
import drlc.intermediate.Scope;
import drlc.node.Node;

public class CharTypeInfo extends BuiltInTypeInfo {
	
	protected CharTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public CharTypeInfo(Node node, Scope scope, int referenceLevel) {
		super(node, scope, Global.CHAR, referenceLevel);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new CharTypeInfo(node, type, newReferenceLevel);
	}
}
