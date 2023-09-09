package drlc.intermediate.action;

import java.util.Map;

import drlc.intermediate.component.data.*;

public interface IValueAction {
	
	public DataId[] lvalues();
	
	public DataId[] rvalues();
	
	public boolean canRemove();
	
	public boolean canReplaceRvalue();
	
	public DataId getRvalueReplacer();
	
	public Action replaceRegRvalue(long targetId, DataId rvalueReplacer);
	
	public boolean canReplaceLvalue();
	
	public DataId getLvalueReplacer();
	
	public Action replaceRegLvalue(long targetId, DataId lvalueReplacer);
	
	public Action setTransientLvalue();
	
	public boolean canReorderRvalues();
	
	public Action swapRvalues(int i, int j);
	
	public Action foldRvalues();
	
	public Action replaceRegIds(Map<Long, Long> regIdMap);
	
	public static class RegReplaceResult {
		
		public final DataId dataId;
		public final boolean success;
		
		public RegReplaceResult(DataId dataId, boolean success) {
			this.dataId = dataId;
			this.success = success;
		}
	}
	
	public default RegReplaceResult replaceRegId(DataId dataId, long targetId, DataId replacer) {
		boolean success = false;
		if (dataId instanceof RegDataId) {
			RegDataId regDataId = (RegDataId) dataId;
			if (regDataId.regId == targetId) {
				dataId = replacer;
				success = true;
			}
		}
		return new RegReplaceResult(dataId, success);
	}
	
	public default RegReplaceResult replaceRegId(DataId dataId, Map<Long, Long> regIdMap) {
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
		return new RegReplaceResult(dataId, success);
	}
}
