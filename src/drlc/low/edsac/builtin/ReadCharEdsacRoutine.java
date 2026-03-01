package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.InstructionRead;

public class ReadCharEdsacRoutine extends EdsacRoutine {
	
	public ReadCharEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		text.add(new InstructionRead(scratchInfo()));
		loadData(text, scratchInfo());
		returnFromSubroutine(text);
	}
}
