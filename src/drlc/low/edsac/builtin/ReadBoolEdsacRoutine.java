package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.InstructionSubtract;

public class ReadBoolEdsacRoutine extends EdsacRoutine {
	
	public ReadBoolEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		text.add(new InstructionDial());
		text.add(new InstructionSubtract(constantDataInfo(20)));
		returnFromSubroutineIfMoreThanOrEqualToZero(text);
		text.add(new InstructionRaw("TF")); // [0]
		text.add(new InstructionSubtract(constantDataInfo(1)));
		returnFromSubroutineIfLessThanZero(text);
	}
}
