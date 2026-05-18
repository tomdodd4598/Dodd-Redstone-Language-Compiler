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
		storeData(loopText, scratchDataInfo(0), true);
		loadMultiplierData(loopText, scratchDataInfo(0));
		loopText.add(new InstructionAddCollation(constantDataInfo(1)));
		storeData(loopText, scratchDataInfo(1), true);
		subtractData(loopText, scratchDataInfo(1));
		loopText.add(new InstructionJumpIfLessThanZero(1));
		storeData(loopText, scratchDataInfo(1), true);
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		
		clearAccumulator(signText);
		signText.add(new InstructionAdd(constantDataInfo(EdsacInt.MIN_VALUE)));
		storeData(signText, scratchDataInfo(1), true);
		
		addData(shiftText, scratchDataInfo(0));
		shiftText.add(new InstructionRightShift(1));
		shiftText.add(new InstructionJumpIfMoreThanOrEqualToZero(4));
		
		clearText.add(new InstructionSubtract(constantDataInfo(EdsacInt.MIN_VALUE)));
		
		addData(rotateText, scratchDataInfo(1));
		storeData(rotateText, x, true);
		rotateText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		clearAccumulator(returnText);
		addData(returnText, x);
		returnFromSubroutine(returnText);
	}
}
