package drlc.interpret.component;

import drlc.*;

public class DataId {
	
	public final String raw;
	public final Integer scopeId;
	
	private final String str;
	public final int dereferenceLevel;
	
	public DataId(String name, Integer scopeId) {
		raw = name;
		str = scopeId == null ? name : name.concat(Global.DOUBLE_COLON).concat(Integer.toString(scopeId));
		this.scopeId = scopeId == null ? null : new Integer(scopeId);
		dereferenceLevel = Helpers.getDereferenceCount(str);
	}
	
	public DataId removeAddressPrefix() {
		return new DataId(Helpers.removeAddressPrefix(raw), scopeId);
	}
	
	public DataId removeAllDereferences() {
		return new DataId(Helpers.removeAllDereferences(raw), scopeId);
	}
	
	public DataId removeDereference() {
		return new DataId(Helpers.removeDereference(raw), scopeId);
	}
	
	public DataId addDereferences(int count) {
		return new DataId(Helpers.addDereferences(raw, count), scopeId);
	}
	
	@Override
	public int hashCode() {
		return str.hashCode();
	}
	
	public boolean equalsOther(DataId other, boolean ignoreDereferenceLevels) {
		if (ignoreDereferenceLevels) {
			return Helpers.removeAllDereferences(str).equals(Helpers.removeAllDereferences(other.str));
		}
		else {
			return str.equals(other.str);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataId) {
			return equalsOther((DataId) obj, false);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return str;
	}
}
