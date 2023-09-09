package drlc.intermediate.action;

import java.util.Map;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class ReturnValueAction extends Action implements IDefiniteRedirectAction, IValueAction {
	
	public final DataId arg;
	
	public ReturnValueAction(ASTNode node, DataId arg) {
		super(node);
		if (arg == null) {
			throw node.error("Return value action argument was null!");
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
	public Action replaceRegRvalue(long targetId, DataId rvalueReplacer) {
		return new ReturnValueAction(null, rvalueReplacer);
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
	public Action replaceRegLvalue(long targetId, DataId lvalueReplacer) {
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
		RegReplaceResult argResult = replaceRegId(arg, regIdMap);
		if (argResult.success) {
			return new ReturnValueAction(null, argResult.dataId);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.RETURN + ' ' + arg;
	}
}
