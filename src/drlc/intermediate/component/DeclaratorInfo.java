package drlc.intermediate.component;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
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
		if (variable.scope == null) {
			return variable.toString();
		}
		else {
			return Global.SCOPE_ID_START + variable.scope.globalId + Global.SCOPE_ID_END + " " + variable.toString();
		}
	}
}
