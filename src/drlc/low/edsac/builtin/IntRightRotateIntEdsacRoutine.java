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
		
		loopText.add(new InstructionAdd(y));
		loopText.add(new InstructionSubtract(constantInfo(1)));
		loopText.add(new InstructionJumpIfLessThanZero(5));
		loopText.add(new InstructionStoreAndClear(y));
		loopText.add(new InstructionAdd(x));
		loopText.add(new InstructionDirect("T1F")); // [1]
		loopText.add(new InstructionDirect("H1F")); // [1]
		loopText.add(new InstructionAddCollation(constantInfo(1)));
		loopText.add(new InstructionDirect("TF")); // [0]
		loopText.add(new InstructionDirect("SF")); // [0]
		loopText.add(new InstructionJumpIfLessThanZero(1));
		loopText.add(new InstructionDirect("TF")); // [0]
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		
		signText.add(new InstructionDirect("T4F")); // [4]
		signText.add(new InstructionAdd(constantInfo(EdsacInt.MIN_VALUE)));
		signText.add(new InstructionDirect("TF")); // [0]
		
		shiftText.add(new InstructionDirect("A1F")); // [1]
		shiftText.add(new InstructionRightShift(1));
		shiftText.add(new InstructionJumpIfMoreThanOrEqualToZero(4));
		
		clearText.add(new InstructionSubtract(constantInfo(EdsacInt.MIN_VALUE)));
		
		rotateText.add(new InstructionDirect("AF")); // [0]
		rotateText.add(new InstructionStoreAndClear(x));
		rotateText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		returnText.add(new InstructionAdd(x));
		returnFromSubroutine(returnText);
	}
}
