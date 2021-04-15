package drlc.interpret.action;

import drlc.Global;
import drlc.node.Node;

public class JumpAction extends Action implements IJumpAction<JumpAction>, IStopAction {
	
	public final String target;
	
	public JumpAction(Node node, String target) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Jump action target was null! %s", node));
		}
		else {
			this.target = target;
		}
	}
	
	@Override
	public boolean conditional() {
		return false;
	}
	
	@Override
	public String getTarget() {
		return target;
	}
	
	@Override
	public JumpAction copy(String target) {
		return new JumpAction(null, target);
	}
	
	@Override
	public String toString() {
		return Global.JUMP.concat(" ").concat(target);
	}
}
