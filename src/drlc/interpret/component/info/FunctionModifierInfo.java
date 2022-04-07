package drlc.interpret.component.info;

import drlc.Global;

public class FunctionModifierInfo {
	
	public boolean stack_, static_;
	
	public FunctionModifierInfo(boolean stack_, boolean static_) {
		this.stack_ = stack_;
		this.static_ = static_;
	}
	
	public void updateFromExistingModifierInfo(FunctionModifierInfo existingModifierInfo) {
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
