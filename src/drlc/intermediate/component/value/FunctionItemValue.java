package drlc.intermediate.component.value;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.FunctionItemTypeInfo;

public class FunctionItemValue extends Value {
	
	public final String name;
	
	public FunctionItemValue(ASTNode<?, ?> node, @NonNull FunctionItemTypeInfo typeInfo, String name) {
		super(node, typeInfo);
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionItemValue) {
			FunctionItemValue other = (FunctionItemValue) obj;
			return typeInfo.equals(other.typeInfo) && name.equals(other.name);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return name;
	}
}
