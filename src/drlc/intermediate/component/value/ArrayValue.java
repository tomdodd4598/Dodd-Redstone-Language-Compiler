package drlc.intermediate.component.value;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;

public class ArrayValue extends Value {
	
	protected final List<Value> values;
	protected final int length;
	
	public ArrayValue(ASTNode<?, ?> node, @NonNull ArrayTypeInfo typeInfo, List<Value> values) {
		super(node, typeInfo);
		
		length = values.size();
		if (typeInfo.length != length) {
			throw Helpers.nodeError(node, "Array value of type \"%s\" can not be created with %d values!", typeInfo, length);
		}
		
		@NonNull TypeInfo elementType = typeInfo.elementTypeInfo;
		for (Value value : values) {
			if (!value.typeInfo.canImplicitCastTo(elementType)) {
				throw Helpers.nodeError(node, "Can not cast value \"%s\" to array element type \"%s\"!", value, elementType);
			}
		}
		
		this.values = values;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Value atIndex(ASTNode<?, ?> node, int index) {
		return values.get(index);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, values);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArrayValue) {
			ArrayValue other = (ArrayValue) obj;
			return typeInfo.equals(other.typeInfo) && values.equals(other.values);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.arrayString(Helpers.map(values, Value::valueString));
	}
}
