package drlc.intermediate.component.value;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;

public class StructValue extends CompoundValue<StructTypeInfo> {
	
	public final @NonNull TypeDefinition typedef;
	
	public StructValue(ASTNode<?> node, @NonNull StructTypeInfo typeInfo, List<Value<?>> values) {
		super(node, typeInfo, values);
		this.typedef = typeInfo.typedef;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StructValue) {
			return super.equals(obj);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.structString(typedef, Helpers.map(values, Value::valueString));
	}
}
