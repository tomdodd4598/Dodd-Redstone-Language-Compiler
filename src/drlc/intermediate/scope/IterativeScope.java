package drlc.intermediate.scope;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.action.JumpAction;
import drlc.intermediate.ast.ASTNode;

public class IterativeScope extends Scope {
	
	public final @Nullable String label;
	
	public final @NonNull JumpAction continueJump = new JumpAction(null, -1), breakJump = new JumpAction(null, -1);
	
	public IterativeScope(Scope parent, boolean definiteExecution, @Nullable String label) {
		super(parent);
		this.definiteExecution = definiteExecution;
		this.label = label;
	}
	
	protected boolean labelMatch(@Nullable String label) {
		return label == null || label.equals(this.label);
	}
	
	@Override
	public boolean isBreakable(@Nullable String label) {
		return labelMatch(label) || super.isBreakable(label);
	}
	
	@Override
	public @NonNull JumpAction getContinueJump(ASTNode<?, ?> node, @Nullable String label) {
		return labelMatch(label) ? continueJump : parent.getContinueJump(node, label);
	}
	
	@Override
	public @NonNull JumpAction getBreakJump(ASTNode<?, ?> node, @Nullable String label) {
		return labelMatch(label) ? breakJump : parent.getBreakJump(node, label);
	}
}
