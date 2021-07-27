package drlc.interpret.action;

import drlc.Global;
import drlc.node.Node;

public class BuiltInMethodCallAction extends MethodCallAction {
	
	public BuiltInMethodCallAction(Node node, String name, String... args) {
		super(node, name, args);
	}
	
	@Override
	protected MethodCallAction copy(Node node, String name, String... args) {
		return new BuiltInMethodCallAction(node, name, args);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Global.HARDWARE).append(" ").append(Global.FUN).append(" ").append(name);
		for (String arg : args) {
			builder.append(" ").append(arg);
		}
		return builder.toString();
	}
}
