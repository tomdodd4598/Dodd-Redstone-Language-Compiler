package drlc.low.drc1.instruction.constant;

import java.util.*;

import drlc.low.LowDataInfo;

public class InstructionAddressData extends InstructionData {
	
	public LowDataInfo dataInfo;
	public Short address;
	
	public InstructionAddressData(LowDataInfo dataInfo) {
		super();
		this.dataInfo = dataInfo;
	}
	
	@Override
	protected List<Short> values() {
		return Arrays.asList(address);
	}
}
