package drlc.interpret.action;

import java.util.Map;

public interface IValueAction {
	
	public String[] lValues();
	
	public String[] rValues();
	
	public boolean canRemove();
	
	public boolean canReplaceRValue();
	
	public String getRValueReplacer();
	
	public Action replaceRValue(String replaceTarget, String rValueReplacer);
	
	public boolean canReplaceLValue();
	
	public String getLValueReplacer();
	
	public Action replaceLValue(String replaceTarget, String lValueReplacer);
	
	public boolean canReorderRValues();
	
	public Action swapRValues(int i, int j);
	
	public Action replaceRegIds(Map<String, String> regIdMap);
}
