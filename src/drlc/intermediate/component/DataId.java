package drlc.intermediate.component;

import drlc.*;
import drlc.intermediate.Scope;

public class DataId {
	
	public final String raw;
	public final Scope scope;
	
	private final String str;
	public final int dereferenceLevel;
	
	public DataId(String name, Scope scope) {
		raw = name;
		str = scope == null ? name : name.concat(Global.DOUBLE_COLON).concat(scope.toString());
		this.scope = scope;
		dereferenceLevel = Helpers.getDereferenceCount(str);
	}
	
	public DataId removeAddressPrefix() {
		return new DataId(Helpers.removeAddressPrefix(raw), scope);
	}
	
	public DataId removeAllDereferences() {
		return new DataId(Helpers.removeAllDereferences(raw), scope);
	}
	
	public DataId removeDereference() {
		return new DataId(Helpers.removeDereference(raw), scope);
	}
	
	public DataId addDereferences(int count) {
		return new DataId(Helpers.addDereferences(raw, count), scope);
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
