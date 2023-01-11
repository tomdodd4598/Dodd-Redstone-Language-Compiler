package drlc.intermediate.component.type;

import drlc.intermediate.Scope;
import drlc.node.Node;

public abstract class BuiltInTypeInfo extends TypeInfo {
	
	protected BuiltInTypeInfo(Node node, Type type, int referenceLevel) {
		super(node, type, referenceLevel);
	}
	
	public BuiltInTypeInfo(Node node, Scope scope, String typeName, int referenceLevel) {
		this(node, scope.getType(node, typeName), referenceLevel);
	}
	
	@Override
	public abstract TypeInfo copy(Node node, int newReferenceLevel);
	
	@Override
	public boolean isFunction() {
		return false;
	}
	
	@Override
	public boolean isAddressable() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BuiltInTypeInfo) {
			BuiltInTypeInfo other = (BuiltInTypeInfo) obj;
			return type.equals(other.type) && referenceLevel == other.referenceLevel;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String typeString() {
		return type.toString();
	}
}
