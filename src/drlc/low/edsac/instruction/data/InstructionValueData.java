package drlc.low.edsac.instruction.data;

import java.util.List;
import java.util.stream.Collectors;

import drlc.low.edsac.EdsacInt;

public class InstructionValueData extends InstructionData {
	
	public final List<EdsacInt> values;
	
	public InstructionValueData(List<EdsacInt> values) {
		super();
		this.values = values;
	}
	
	@Override
	public int size() {
		return values.size();
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return values.stream().map(EdsacInt::toAssembly).collect(Collectors.joining(" "));
	}
}
