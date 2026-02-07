package drlc.low.edsac.instruction.data;

import drlc.intermediate.component.Function;
import drlc.low.edsac.EdsacInt;

public class InstructionSubroutineAddressData extends InstructionData {
	
	public final Function function;
	protected Integer value;
	
	public InstructionSubroutineAddressData(Function function) {
		super();
		this.function = function;
	}
	
	public void setValue(int value) {
		if (this.value == null) {
			this.value = value;
		}
		else {
			throw new UnsupportedOperationException(String.format("Attempted to modify non-null immediate call address!"));
		}
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return EdsacInt.of(value + (offset == null ? 0 : offset)).toAssembly() + " [" + function.asmString() + "]";
	}
}
