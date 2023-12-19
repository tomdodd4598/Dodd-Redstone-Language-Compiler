package drlc.intermediate.action;

import java.util.Map;

import drlc.intermediate.component.data.*;

public interface IValueAction {
	
	public DataId[] lvalues();
	
	public DataId[] rvalues();
	
	public boolean canRemove();
	
	public boolean canReplaceRvalue();
	
	public DataId getRvalueReplacer();
	
	public <T extends Action & IValueAction> T replaceRvalue(DataId targetId, DataId rvalueReplacer);
	
	public boolean canReplaceLvalue();
	
	public DataId getLvalueReplacer();
	
	public <T extends Action & IValueAction> T replaceLvalue(DataId targetId, DataId lvalueReplacer);
	
	public Action setTransientLvalue();
	
	public boolean canReorderRvalues();
	
	public Action swapRvalues(int i, int j);
	
	public Action foldRvalues();
	
	public Action replaceRegIds(Map<Long, Long> regIdMap);
	
	public static class DataIdReplaceResult {
		
		public final DataId dataId;
		public final boolean success;
		
		public DataIdReplaceResult(DataId dataId, boolean success) {
			this.dataId = dataId;
			this.success = success;
		}
	}
	
	public default DataIdReplaceResult replaceDataId(DataId dataId, DataId targetId, DataId replacer) {
		boolean success = false;
		if (dataId.equals(targetId)) {
			dataId = replacer;
			success = true;
		}
		return new DataIdReplaceResult(dataId, success);
	}
	
	public default DataIdReplaceResult replaceRegId(DataId dataId, Map<Long, Long> regIdMap) {
		boolean success = false;
		if (dataId instanceof RegDataId) {
			RegDataId regDataId = (RegDataId) dataId;
			long regId = regDataId.regId;
			if (regIdMap.containsKey(regId)) {
				long newRegId = regIdMap.get(regId);
				if (regId != newRegId) {
					dataId = regDataId.replaceId(newRegId);
					success = true;
				}
			}
		}
		return new DataIdReplaceResult(dataId, success);
	}
}
