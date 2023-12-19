package drlc.intermediate.component;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.type.TypeInfo;

public class DeclaratorInfo {
	
	public final @NonNull Variable variable;
	
	public DeclaratorInfo(ASTNode<?, ?> node, @NonNull Variable variable) {
		this.variable = variable;
	}
	
	public DataId dataId() {
		return new VariableDataId(0, variable);
	}
	
	public @NonNull TypeInfo getTypeInfo() {
		return variable.typeInfo;
	}
	
	@Override
	public String toString() {
		return variable.toString();
	}
}
