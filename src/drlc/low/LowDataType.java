package drlc.low;

import drlc.Global;

public enum LowDataType {
	
	TEMP(Global.TEMP),
	STATIC(Global.STATIC),
	STACK(Global.STACK);
	
	private final String str;
	
	private LowDataType(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
