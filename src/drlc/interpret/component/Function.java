package drlc.interpret.component;

import drlc.*;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class Function {
	
	public final String name;
	public final boolean builtIn;
	public final FunctionModifierInfo modifierInfo;
	public final TypeInfo returnTypeInfo;
	public final DeclaratorInfo[] params;
	public final boolean defined;
	public boolean required;
	
	public Function(Node node, String name, boolean builtIn, FunctionModifierInfo modifierInfo, TypeInfo returnTypeInfo, DeclaratorInfo[] params, boolean defined) {
		this.name = name;
		this.builtIn = builtIn;
		this.modifierInfo = modifierInfo;
		this.returnTypeInfo = returnTypeInfo;
		this.params = params;
		this.defined = defined;
	}
	
	public int getArgumentCount() {
		return params.length;
	}
	
	public void updateFromExistingFunction(Function existingFunction) {
		modifierInfo.updateFromExistingModifierInfo(existingFunction.modifierInfo);
		required |= existingFunction.required;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function) {
			Function other = (Function) obj;
			if (name.equals(other.name) && returnTypeInfo.equals(other.returnTypeInfo) && params.length == other.params.length) {
				for (int i = 0; i < params.length; ++i) {
					if (!params[i].typeInfo.equals(other.params[i].typeInfo)) {
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
		builder.append(Global.FUN).append(' ').append(name);
		Helpers.appendParams(builder, params);
		return builder.append(' ').append(returnTypeInfo.toString()).toString();
	}
}
