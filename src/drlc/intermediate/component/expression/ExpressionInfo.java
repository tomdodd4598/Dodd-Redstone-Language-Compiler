package drlc.intermediate.component.expression;

import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public abstract class ExpressionInfo {
	
	protected TypeInfo typeInfo;
	
	public boolean isDirectFunction = false;
	
	protected ExpressionInfo() {
		this(null);
	}
	
	protected ExpressionInfo(TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	public abstract ExpressionInfo copy(Node node);
	
	public abstract boolean isLvalue();
	
	public abstract boolean isRvalue();
	
	public TypeInfo getTypeInfo() {
		return typeInfo;
	}
	
	public void setTypeInfo(TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	public void incrementReferenceLevel(Node node) {
		typeInfo = typeInfo.copy(node, typeInfo.referenceLevel + 1);
	}
	
	public void decrementReferenceLevel(Node node) {
		if (typeInfo.referenceLevel < 1) {
			throw new IllegalArgumentException(String.format("Can not dereference expression of type \"%s\"! %s", typeInfo.typeString(), node));
		}
		else {
			typeInfo = typeInfo.copy(node, typeInfo.referenceLevel - 1);
		}
	}
	
	@Override
	public abstract boolean equals(Object obj);
}
