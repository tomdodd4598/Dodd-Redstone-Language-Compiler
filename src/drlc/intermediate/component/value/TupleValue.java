package drlc.intermediate.component.value;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TupleTypeInfo;

public class TupleValue extends CompoundValue {
	
	public TupleValue(ASTNode<?, ?> node, @NonNull TupleTypeInfo typeInfo, List<Value> values) {
		super(node, typeInfo, values);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TupleValue) {
			return super.equals(obj);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.tupleString(Helpers.map(values, Value::valueString));
	}
}
