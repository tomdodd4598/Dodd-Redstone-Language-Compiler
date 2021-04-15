package drlc.generate.drc1;

import drlc.generate.Generator;
import drlc.interpret.Program;
import drlc.node.Node;

public abstract class RedstoneGenerator extends Generator {
	
	public RedstoneGenerator(Boolean intermediateOptimization, Boolean machineOptimization, String outputFile) {
		super(intermediateOptimization, machineOptimization, outputFile);
	}
	
	@Override
	public void checkInteger(Node node, int value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Integer \"%s\" is out of range! %s", value, node));
		}
	}
	
	@Override
	public int inverse(int value) {
		return ~((short) value);
	}
	
	public RedstoneCode generateCode(Program program) {
		RedstoneCode code = new RedstoneCode(program);
		code.generate(machineOptimization);
		return code;
	}
}
