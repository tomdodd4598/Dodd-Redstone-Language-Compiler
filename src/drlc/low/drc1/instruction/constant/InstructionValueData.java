package drlc.low.drc1.instruction.constant;

import java.util.List;

public class InstructionValueData extends InstructionData {
	
	public final List<Short> raw;
	
	public InstructionValueData(List<Short> raw) {
		super();
		this.raw = raw;
	}
	
	@Override
	protected List<Short> values() {
		return raw;
	}
}
