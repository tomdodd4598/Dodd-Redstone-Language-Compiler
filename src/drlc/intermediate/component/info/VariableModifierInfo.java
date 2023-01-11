package drlc.intermediate.component.info;

import drlc.Global;

public class VariableModifierInfo {
	
	public boolean static_;
	
	public VariableModifierInfo(boolean static_) {
		this.static_ = static_;
	}
	
	public void updateFromExistingModifierInfo(VariableModifierInfo existingModifierInfo) {
		static_ |= existingModifierInfo.static_;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (static_) {
			builder.append(Global.STATIC).append(' ');
		}
		return builder.toString();
	}
}
