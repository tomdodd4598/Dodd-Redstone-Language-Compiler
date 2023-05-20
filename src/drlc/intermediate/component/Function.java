package drlc.intermediate.component;

import drlc.*;
import drlc.intermediate.component.info.DeclaratorInfo;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class Function {
	
	public final String name;
	public final boolean builtIn;
	public final TypeInfo returnTypeInfo;
	public final DeclaratorInfo[] params;
	public final boolean defined;
	public boolean required;
	
	public Function(Node node, String name, boolean builtIn, TypeInfo returnTypeInfo, DeclaratorInfo[] params, boolean defined) {
		this.name = name;
		this.builtIn = builtIn;
		this.returnTypeInfo = returnTypeInfo;
		this.params = params;
		this.defined = defined;
	}
	
	public int getArgumentCount() {
		return params.length;
	}
	
	public void updateFromExistingFunction(Function existingFunction) {
		required |= existingFunction.required;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function) {
			Function other = (Function) obj;
			if (name.equals(other.name) && returnTypeInfo.equals(other.returnTypeInfo) && params.length == other.params.length) {
				for (int i = 0; i < params.length; ++i) {
					if (!params[i].getTypeInfo().equals(other.params[i].getTypeInfo())) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Global.FN).append(' ').append(name);
		Helpers.appendParams(builder, params);
		return builder.append(' ').append(returnTypeInfo.toString()).toString();
	}
}
