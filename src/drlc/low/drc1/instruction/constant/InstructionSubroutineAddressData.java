package drlc.low.drc1.instruction.constant;

import java.util.*;

import drlc.intermediate.component.Function;

public class InstructionSubroutineAddressData extends InstructionData {
	
	public final Function function;
	protected Short value;
	
	public InstructionSubroutineAddressData(Function function) {
		super();
		this.function = function;
	}
	
	public void setValue(short value) {
		if (this.value == null) {
			this.value = value;
		}
		else {
			throw new UnsupportedOperationException(String.format("Attempted to modify non-null immediate call address!"));
		}
	}
	
	@Override
	protected List<Short> values() {
		return Arrays.asList(value);
	}
}
