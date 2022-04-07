package drlc.generate.drc1;

public enum RedstoneDataType {
	
	TEMP("TEMP"),
	STATIC("STATIC"),
	STACK("STACK");
	
	private final String str;
	
	private RedstoneDataType(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
