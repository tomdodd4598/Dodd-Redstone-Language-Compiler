package drlc.intermediate.component.info;

public class FunctionModifierInfo {
	
	// public boolean stack_;
	
	public FunctionModifierInfo() {
		// this.stack_ = stack_;
	}
	
	public void updateFromExistingModifierInfo(FunctionModifierInfo existingModifierInfo) {
		// stack_ |= existingModifierInfo.stack_;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		/*if (stack_) {
			builder.append(Global.STACK).append(' ');
		}*/
		return builder.toString();
	}
}
