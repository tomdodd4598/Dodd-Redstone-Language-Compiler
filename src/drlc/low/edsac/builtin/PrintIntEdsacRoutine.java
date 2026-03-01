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
		
		entryText.add(new InstructionPrint(constantDataInfo(EdsacChar.FIGURE_SHIFT)));
		addData(entryText, info);
		entryText.add(new InstructionJumpIfMoreThanOrEqualToZero(3));
		
		minusText.add(new InstructionPrint(constantDataInfo(EdsacChar.of('-'))));
		minusText.add(new InstructionSubtract(constantDataInfo(1)));
		minusText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		minusText.add(new InstructionAdd(constantDataInfo(1)));
		storeData(minusText, info, true);
		subtractData(minusText, info);
		minusText.add(new InstructionJumpIfMoreThanOrEqualToZero(3));
		
		minimumText.add(new InstructionPrint(constantDataInfo(EdsacChar.of('6'))));
		minimumText.add(new InstructionSubtract(constantDataInfo(59999)));
		
		callText.add(new InstructionSubtract(constantDataInfo(1)));
		callText.add(new InstructionJumpIfLessThanZero(4));
		callText.add(new InstructionAdd(constantDataInfo(1)));
		callText.add(new InstructionRaw("TF")); // [0]
		callText.add(new InstructionAdd(constantDataInfo(EdsacChar.of('\0'))));
		callText.add(new InstructionRaw("T1F")); // [1]
		callText.add(new InstructionRaw("T6F")); // [6]
		builtInSubroutine(callText, Global.PRINT_DIGITS, x -> x, false);
		returnFromSubroutineIfMoreThanOrEqualToZero(callText);
		
		zeroText.add(new InstructionPrint(constantDataInfo(EdsacChar.of('0'))));
		zeroText.add(new InstructionRaw("TF")); // [0]
		returnFromSubroutineIfMoreThanOrEqualToZero(zeroText);
	}
}
