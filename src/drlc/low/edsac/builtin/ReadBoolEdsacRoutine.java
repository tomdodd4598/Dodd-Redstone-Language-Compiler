package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;

public class ReadBoolEdsacRoutine extends EdsacRoutine {
	
	public ReadBoolEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		text.add(new InstructionDial());
		text.add(new InstructionRightShift(1));
		text.add(new InstructionSubtract(constantInfo(10)));
		returnFromSubroutineIfMoreThanOrEqualToZero(text);
		text.add(new InstructionStoreAndClear(tempDataInfo(0)));
		text.add(new InstructionAdd(constantInfo(1)));
		returnFromSubroutine(text);
	}
}
