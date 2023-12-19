package drlc.intermediate.action;

import java.util.Map;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class ExitValueAction extends Action implements IDefiniteRedirectAction, IValueAction {
	
	public final DataId arg;
	
	public ExitValueAction(ASTNode<?, ?> node, DataId arg) {
		super(node);
		if (arg == null) {
			throw Helpers.nodeError(node, "Exit value action argument was null!");
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {};
	}
	
	@Override
	public DataId[] rvalues() {
		return new DataId[] {arg};
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRvalue() {
		return true;
	}
	
	@Override
	public DataId getRvalueReplacer() {
		return null;
	}
	
	@Override
	public ExitValueAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
		return new ExitValueAction(null, rvalueReplacer);
	}
	
	@Override
	public boolean canReplaceLvalue() {
		return false;
	}
	
	@Override
	public DataId getLvalueReplacer() {
		return null;
	}
	
	@Override
	public ExitValueAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return null;
	}
	
	@Override
	public Action setTransientLvalue() {
		return null;
	}
	
	@Override
	public boolean canReorderRvalues() {
		return false;
	}
	
	@Override
	public Action swapRvalues(int i, int j) {
		return null;
	}
	
	@Override
	public Action foldRvalues() {
		return null;
	}
	
	@Override
	public Action replaceRegIds(Map<Long, Long> regIdMap) {
		DataIdReplaceResult argResult = replaceRegId(arg, regIdMap);
		if (argResult.success) {
			return new ExitValueAction(null, argResult.dataId);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.EXIT + ' ' + arg;
	}
}
