package drlc.intermediate.component.value;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;

public class ArrayValue extends Value {
	
	public final List<Value> values;
	
	public ArrayValue(ASTNode node, @NonNull ArrayTypeInfo typeInfo, List<Value> values) {
		super(node, typeInfo);
		
		int valueCount = values.size();
		if (typeInfo.length != valueCount) {
			throw node.error("Array value of type \"%s\" can not be created with %d values!", typeInfo, valueCount);
		}
		
		@NonNull TypeInfo elementType = typeInfo.elementTypeInfo;
		for (Value value : values) {
			if (!value.typeInfo.canImplicitCastTo(elementType)) {
				throw node.error("Can not cast value \"%s\" to array element type \"%s\"!", value, elementType);
			}
		}
		
		this.values = values;
	}
	
	public ArrayValue(ASTNode node, @NonNull ArrayTypeInfo typeInfo, @NonNull Value value, int length) {
		this(node, typeInfo, Collections.nCopies(length, value));
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
		return Helpers.arrayString(values);
	}
}
