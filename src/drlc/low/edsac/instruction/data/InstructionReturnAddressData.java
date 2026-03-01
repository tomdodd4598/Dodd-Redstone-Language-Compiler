package drlc.low.edsac.instruction.data;

import drlc.low.edsac.EdsacInt;

public class InstructionReturnAddressData extends InstructionData {
	
	protected Integer address;
	
	public InstructionReturnAddressData() {
		super();
	}
	
	public void setAddress(int address) {
		if (this.address == null) {
			this.address = address;
		}
		else {
			throw new UnsupportedOperationException(String.format("Attempted to modify non-null immediate return address!"));
		}
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return EdsacInt.of(address + (offset == null ? 0 : offset)).toAssembly();
	}
}
