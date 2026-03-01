package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;

public class ReadIntEdsacRoutine extends EdsacRoutine {
	
	public ReadIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		text.add(new InstructionDial());
		text.add(new InstructionRightShift(1));
		text.add(new InstructionSubtract(constantDataInfo(10)));
		returnFromSubroutineIfMoreThanOrEqualToZero(text);
		text.add(new InstructionAdd(constantDataInfo(10)));
		returnFromSubroutineIfMoreThanOrEqualToZero(text);
	}
}
