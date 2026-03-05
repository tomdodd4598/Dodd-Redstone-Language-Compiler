package drlc.low;

public class LowInstruction {
	
	protected LowInstruction() {}
	
	public LowDataInfo getDataInfo() {
		return null;
	}
	
	public boolean isDataFromMemory() {
		return false;
	}
	
	public boolean isDataToMemory() {
		return false;
	}
	
	public void regenerateDataInfo() {}
}
