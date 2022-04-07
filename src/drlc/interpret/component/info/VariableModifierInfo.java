package drlc.interpret.component.info;

import drlc.Global;

public class VariableModifierInfo {
	
	public boolean init_, stack_, static_;
	
	public VariableModifierInfo(boolean init_, boolean stack_, boolean static_) {
		this.init_ = init_;
		this.stack_ = stack_;
		this.static_ = static_;
	}
	
	public void updateFromExistingModifierInfo(VariableModifierInfo existingModifierInfo) {
		init_ |= existingModifierInfo.init_;
		stack_ |= existingModifierInfo.stack_;
		static_ |= existingModifierInfo.static_;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (stack_) {
			builder.append(Global.STACK).append(' ');
		}
		else if (static_) {
			builder.append(Global.STATIC).append(' ');
		}
		return builder.toString();
	}
}
