package drlc.intermediate.component.info;

import drlc.Global;

public class VariableModifierInfo {
	
	public boolean statik;
	
	public VariableModifierInfo(boolean statik) {
		this.statik = statik;
	}
	
	public void updateFromExistingModifierInfo(VariableModifierInfo existingModifierInfo) {
		statik |= existingModifierInfo.statik;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (statik) {
			builder.append(Global.STATIC).append(' ');
		}
		return builder.toString();
	}
}
