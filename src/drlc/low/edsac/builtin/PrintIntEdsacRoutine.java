package drlc.low.edsac.builtin;

import java.util.*;

import drlc.Global;
import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class PrintIntEdsacRoutine extends EdsacRoutine {
	
	public PrintIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> entryText = new ArrayList<>(), minusText = new ArrayList<>(), minimumText = new ArrayList<>(), callText = new ArrayList<>(), zeroText = new ArrayList<>();
		sectionTextMap.put(0, entryText);
		sectionTextMap.put(1, minusText);
		sectionTextMap.put(2, minimumText);
		sectionTextMap.put(3, callText);
		sectionTextMap.put(4, zeroText);
		
		LowDataInfo info = getDataInfo(params.get(0).dataId(), 0);
		
		entryText.add(new InstructionPrint(constantInfo(EdsacChar.FIGURE_SHIFT)));
		entryText.add(new InstructionAdd(info));
		entryText.add(new InstructionJumpIfMoreThanOrEqualToZero(3));
		
		minusText.add(new InstructionPrint(constantInfo(EdsacChar.of('-'))));
		minusText.add(new InstructionSubtract(constantInfo(1)));
		minusText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		minusText.add(new InstructionAdd(constantInfo(1)));
		minusText.add(new InstructionStoreAndClear(info));
		minusText.add(new InstructionSubtract(info));
		minusText.add(new InstructionJumpIfMoreThanOrEqualToZero(3));
		
		minimumText.add(new InstructionPrint(constantInfo(EdsacChar.of('6'))));
		minimumText.add(new InstructionSubtract(constantInfo(59999)));
		
		callText.add(new InstructionSubtract(constantInfo(1)));
		callText.add(new InstructionJumpIfLessThanZero(4));
		callText.add(new InstructionAdd(constantInfo(1)));
		callText.add(new InstructionDirect("TF")); // [0]
		callText.add(new InstructionAdd(constantInfo(EdsacChar.of('\0'))));
		callText.add(new InstructionDirect("T1F")); // [1]
		callText.add(new InstructionDirect("T6F")); // [6]
		builtInSubroutine(callText, Global.PRINT_DIGITS);
		returnFromSubroutineIfMoreThanOrEqualToZero(callText);
		
		zeroText.add(new InstructionPrint(constantInfo(EdsacChar.of('0'))));
		zeroText.add(new InstructionDirect("TF")); // [0]
		returnFromSubroutineIfMoreThanOrEqualToZero(zeroText);
	}
}
