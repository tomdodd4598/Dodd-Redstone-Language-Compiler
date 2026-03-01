package drlc.low.edsac.builtin;

import java.util.*;

import drlc.Global;
import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class PrintNatEdsacRoutine extends EdsacRoutine {
	
	public PrintNatEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> entryText = new ArrayList<>(), lowText = new ArrayList<>(), highText = new ArrayList<>(), loopText = new ArrayList<>(), breakText = new ArrayList<>(), prepareText = new ArrayList<>(), callText = new ArrayList<>(), zeroText = new ArrayList<>();
		sectionTextMap.put(0, entryText);
		sectionTextMap.put(1, lowText);
		sectionTextMap.put(2, highText);
		sectionTextMap.put(3, loopText);
		sectionTextMap.put(4, breakText);
		sectionTextMap.put(5, prepareText);
		sectionTextMap.put(6, callText);
		sectionTextMap.put(7, zeroText);
		
		LowDataInfo info = getDataInfo(params.get(0).dataId(), 0);
		
		entryText.add(new InstructionPrint(constantDataInfo(EdsacChar.FIGURE_SHIFT)));
		entryText.add(new InstructionRaw("T6F")); // [6]
		addData(entryText, info);
		entryText.add(new InstructionJumpIfMoreThanOrEqualToZero(1));
		entryText.add(new InstructionAdd(constantDataInfo(31072)));
		entryText.add(new InstructionJumpIfLessThanZero(2));
		entryText.add(new InstructionRaw("TF")); // [0]
		entryText.add(new InstructionAdd(constantDataInfo(EdsacChar.of('0'))));
		entryText.add(new InstructionRaw("T1F")); // [1]
		entryText.add(new InstructionPrint(constantDataInfo(EdsacChar.of('1'))));
		entryText.add(new InstructionJumpIfMoreThanOrEqualToZero(6));
		
		lowText.add(new InstructionSubtract(constantDataInfo(1)));
		lowText.add(new InstructionJumpIfLessThanZero(7));
		lowText.add(new InstructionAdd(constantDataInfo(1)));
		lowText.add(new InstructionRaw("TF")); // [0]
		lowText.add(new InstructionJumpIfMoreThanOrEqualToZero(5));
		
		highText.add(new InstructionAdd(constantDataInfo(40000)));
		storeData(highText, info, true);
		highText.add(new InstructionAdd(constantDataInfo(EdsacChar.of('5'))));
		highText.add(new InstructionRaw("T7F")); // [7]
		addData(highText, info);
		
		storeData(loopText, info, true);
		loopText.add(new InstructionRaw("A7F")); // [7]
		loopText.add(new InstructionAdd(constantDataInfo(EdsacChar.of('1'))));
		loopText.add(new InstructionRaw("T7F")); // [7]
		addData(loopText, info);
		loopText.add(new InstructionSubtract(constantDataInfo(10000)));
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(3));
		
		breakText.add(new InstructionRaw("U6F")); // [6]
		breakText.add(new InstructionAdd(constantDataInfo(10000)));
		breakText.add(new InstructionRaw("TF")); // [0]
		breakText.add(new InstructionRaw("O7F")); // [7]
		
		prepareText.add(new InstructionAdd(constantDataInfo(EdsacChar.of('\0'))));
		prepareText.add(new InstructionRaw("T1F")); // [1]
		
		builtInSubroutine(callText, Global.PRINT_DIGITS, x -> x, false);
		returnFromSubroutineIfMoreThanOrEqualToZero(callText);
		
		zeroText.add(new InstructionPrint(constantDataInfo(EdsacChar.of('0'))));
		zeroText.add(new InstructionRaw("TF")); // [0]
		returnFromSubroutineIfMoreThanOrEqualToZero(zeroText);
	}
}
