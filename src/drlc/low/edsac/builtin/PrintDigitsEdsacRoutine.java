package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class PrintDigitsEdsacRoutine extends EdsacRoutine {
	
	public PrintDigitsEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> entryText = new ArrayList<>(), digitText = new ArrayList<>(), returnText = new ArrayList<>(), skipText = new ArrayList<>(), continueText = new ArrayList<>();
		sectionTextMap.put(0, entryText);
		sectionTextMap.put(1, digitText);
		sectionTextMap.put(2, returnText);
		sectionTextMap.put(3, skipText);
		sectionTextMap.put(4, continueText);
		
		entryText.add(new InstructionLoadMultiplier(constantInfo("J995F")));
		entryText.add(new InstructionRaw("VF")); // [0]
		entryText.add(new InstructionRaw("T4D")); // [5,4]
		entryText.add(new InstructionAdd(constantInfo("VF")));
		entryText.add(new InstructionRaw("TF")); // [0]
		entryText.add(new InstructionLoadMultiplier(constantInfo("JF")));
		entryText.add(new InstructionSubtract(constantInfo("TF")));
		
		digitText.add(new InstructionRaw("T7F")); // [7]
		digitText.add(new InstructionRaw("V4D")); // [5,4]
		digitText.add(new InstructionRaw("U4D")); // [5,4]
		digitText.add(new InstructionRaw("AF")); // [0]
		digitText.add(new InstructionJumpIfLessThanZero(3));
		digitText.add(new InstructionRaw("TF")); // [0]
		digitText.add(new InstructionRaw("TF")); // [0]
		digitText.add(new InstructionRaw("O5F")); // [5]
		digitText.add(new InstructionRaw("A4D")); // [5,4]
		digitText.add(new InstructionRaw("F4F")); // [4]
		digitText.add(new InstructionRaw("S4F")); // [4]
		
		returnText.add(new InstructionRaw("L4F")); // [4]
		returnText.add(new InstructionRaw("T4D")); // [5,4]
		returnText.add(new InstructionRaw("A7F")); // [7]
		returnText.add(new InstructionSubtract(constantInfo("VF")));
		returnText.add(new InstructionJumpIfLessThanZero(1));
		returnFromSubroutineIfMoreThanOrEqualToZero(returnText);
		
		skipText.add(new InstructionRaw("SF")); // [0]
		skipText.add(new InstructionRaw("O1F")); // [1]
		skipText.add(new InstructionRaw("T8D")); // [9,8]
		skipText.add(new InstructionRaw("A6F")); // [6]
		skipText.add(new InstructionJumpIfMoreThanOrEqualToZero(4));
		skipText.add(new InstructionRaw("T6F")); // [6]
		skipText.add(new InstructionRaw("T6F")); // [6]
		skipText.add(new InstructionAdd(constantInfo(EdsacChar.of('0'))));
		skipText.add(new InstructionRaw("T1F")); // [1]
		
		continueText.add(new InstructionRaw("A8D")); // [9,8]
		continueText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
	}
}
