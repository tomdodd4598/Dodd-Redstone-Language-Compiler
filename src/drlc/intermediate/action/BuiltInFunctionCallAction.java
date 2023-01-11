package drlc.intermediate.action;

import java.util.Arrays;

import drlc.*;
import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class BuiltInFunctionCallAction extends FunctionCallAction {
	
	public BuiltInFunctionCallAction(Node node, DataId target, DataId name, DataId[] args) {
		super(node, target, name, args);
	}
	
	@Override
	protected FunctionCallAction copy(Node node, DataId target, DataId[] rvalues) {
		return new BuiltInFunctionCallAction(node, target, rvalues[0], Arrays.copyOfRange(rvalues, 1, rvalues.length));
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(target).append(" = ").append(Global.CALL).append(' ').append(Global.BUILT_IN).append(' ').append(getCallId().raw);
		Helpers.appendArgs(builder, getArgs());
		return builder.toString();
	}
}
