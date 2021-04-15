package drlc.interpret.action;

import drlc.Global;
import drlc.node.Node;

public class BuiltInFunctionCallAction extends FunctionCallAction {
	
	public BuiltInFunctionCallAction(Node node, String target, String name, String... args) {
		super(node, target, name, args);
	}
	
	@Override
	protected FunctionCallAction copy(Node node, String target, String name, String... args) {
		return new BuiltInFunctionCallAction(node, target, name, args);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(target).append(" = ").append(Global.HARDWARE).append(" ").append(Global.FUN).append(" ").append(name);
		for (String arg : args) {
			builder.append(" ").append(arg);
		}
		return builder.toString();
	}
}
