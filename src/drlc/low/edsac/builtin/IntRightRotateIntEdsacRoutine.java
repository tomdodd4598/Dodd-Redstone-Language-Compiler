package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class IntRightRotateIntEdsacRoutine extends EdsacRoutine {
	
	public IntRightRotateIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> loopText = new ArrayList<>(), signText = new ArrayList<>(), shiftText = new ArrayList<>(), clearText = new ArrayList<>(), rotateText = new ArrayList<>(), returnText = new ArrayList<>();
		sectionTextMap.put(0, loopText);
		sectionTextMap.put(1, signText);
		sectionTextMap.put(2, shiftText);
		sectionTextMap.put(3, clearText);
		sectionTextMap.put(4, rotateText);
		sectionTextMap.put(5, returnText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0), y = getDataInfo(params.get(1).dataId(), 0);
		
		addData(loopText, y);
		loopText.add(new InstructionSubtract(constantDataInfo(1)));
		loopText.add(new InstructionJumpIfLessThanZero(5));
		storeData(loopText, y, true);
		addData(loopText, x);
		loopText.add(new InstructionRaw("T1F")); // [1]
		loopText.add(new InstructionRaw("H1F")); // [1]
		loopText.add(new InstructionAddCollation(constantDataInfo(1)));
		loopText.add(new InstructionRaw("TF")); // [0]
		loopText.add(new InstructionRaw("SF")); // [0]
		loopText.add(new InstructionJumpIfLessThanZero(1));
		loopText.add(new InstructionRaw("TF")); // [0]
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		
		signText.add(new InstructionRaw("T4F")); // [4]
		signText.add(new InstructionAdd(constantDataInfo(EdsacInt.MIN_VALUE)));
		signText.add(new InstructionRaw("TF")); // [0]
		
		shiftText.add(new InstructionRaw("A1F")); // [1]
		shiftText.add(new InstructionRightShift(1));
		shiftText.add(new InstructionJumpIfMoreThanOrEqualToZero(4));
		
		clearText.add(new InstructionSubtract(constantDataInfo(EdsacInt.MIN_VALUE)));
		
		rotateText.add(new InstructionRaw("AF")); // [0]
		storeData(rotateText, x, true);
		rotateText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		addData(returnText, x);
		returnFromSubroutine(returnText);
	}
}
