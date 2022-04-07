package drlc.interpret.action;

import java.util.Map;

import drlc.interpret.component.DataId;

public interface IValueAction {
	
	public DataId[] lvalues();
	
	public DataId[] rvalues();
	
	public boolean canRemove();
	
	public boolean canReplaceRvalue();
	
	public DataId getRvalueReplacer();
	
	public Action replaceRvalue(DataId replaceTarget, DataId rvalueReplacer);
	
	public boolean canReplaceLvalue();
	
	public DataId getLvalueReplacer();
	
	public Action replaceLvalue(DataId replaceTarget, DataId lvalueReplacer);
	
	public boolean canReorderRvalues();
	
	public Action swapRvalues(int i, int j);
	
	public Action replaceRegIds(Map<DataId, DataId> regIdMap);
}
