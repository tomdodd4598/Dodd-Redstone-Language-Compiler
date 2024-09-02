package drlc.low.drc1.instruction.data;

import java.util.List;
import java.util.stream.Collectors;

import drlc.*;

public class InstructionValueData extends InstructionData {
	
	public final List<Short> values;
	
	public InstructionValueData(List<Short> values) {
		super();
		this.values = values;
	}
	
	@Override
	public int size(boolean longAddress) {
		return values.size();
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return values.stream().map(x -> Helpers.toBinary(x, 16)).toArray(String[]::new);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return values.stream().map(x -> Global.IMMEDIATE + Helpers.toHex(x)).collect(Collectors.joining("\t"));
	}
}
