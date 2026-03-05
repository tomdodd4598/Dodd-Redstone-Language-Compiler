package drlc.low.drc1.instruction.data;

import drlc.Helpers;
import drlc.intermediate.component.Function;

public class InstructionSubroutineAddressData extends InstructionData {
	
	public final Function function;
	protected Short value;
	
	public InstructionSubroutineAddressData(Function function) {
		super();
		this.function = function;
	}
	
	public void setValue(short value) {
		this.value = value;
	}
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {Helpers.toBinary(value, 16)};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return Helpers.toHex(value, longAddress ? 4 : 2) + '\t' + function.asmString();
	}
}
