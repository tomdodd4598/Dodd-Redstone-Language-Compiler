package drlc.intermediate.component.info;

import java.util.Stack;

import drlc.intermediate.component.constant.Constant;

public class ConstantParseInfo {
	
	public final Stack<Constant> constantStack = new Stack<>();
	
	public ConstantParseInfo() {}
	
	public Constant[] popConstantStack(int count) {
		Constant[] out = new Constant[count];
		for (int i = 0; i < count; ++i) {
			out[count - i - 1] = constantStack.pop();
		}
		return out;
	}
}
