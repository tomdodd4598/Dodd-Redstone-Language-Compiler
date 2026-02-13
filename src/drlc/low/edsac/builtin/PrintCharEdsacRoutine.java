package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.edsac.instruction.address.InstructionPrint;

public class PrintCharEdsacRoutine extends EdsacRoutine {
	
	public PrintCharEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		LowDataInfo info = getDataInfo(params.get(0).dataId(), 0);
		
		text.add(new InstructionPrint(info));
		returnFromSubroutineIfMoreThanOrEqualToZero(text);
	}
}
