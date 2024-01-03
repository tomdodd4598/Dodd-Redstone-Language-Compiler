package drlc.intermediate.component.value;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;

public abstract class CompoundValue extends Value {
	
	protected final List<Value> values;
	public final int count;
	
	public CompoundValue(ASTNode<?, ?> node, @NonNull CompoundTypeInfo typeInfo, List<Value> values) {
		super(node, typeInfo);
		this.values = values;
		count = values.size();
		
		if (typeInfo.count != count) {
			throw Helpers.nodeError(node, "Value of type \"%s\" can not be created with %d values!", typeInfo, count);
		}
		
		for (int i = 0; i < count; ++i) {
			Value value = values.get(i);
			TypeInfo memberType = typeInfo.typeInfos.get(i);
			if (!value.typeInfo.canImplicitCastTo(memberType)) {
				throw Helpers.nodeError(node, "Can not cast value \"%s\" to member type \"%s\"!", value, memberType);
			}
		}
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
		if (obj instanceof CompoundValue) {
			CompoundValue other = (CompoundValue) obj;
			return typeInfo.equals(other.typeInfo) && values.equals(other.values);
		}
		else {
			return false;
		}
	}
}
