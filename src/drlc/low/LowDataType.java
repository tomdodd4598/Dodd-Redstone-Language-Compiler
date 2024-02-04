package drlc.low;

public enum LowDataType {
	
	TEMP("TEMP"),
	STATIC("STATIC"),
	STACK("STACK");
	
	private final String str;
	
	private LowDataType(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
