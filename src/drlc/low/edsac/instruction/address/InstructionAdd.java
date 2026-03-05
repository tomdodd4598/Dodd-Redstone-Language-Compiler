package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;

public class InstructionAdd extends InstructionAddress {
	
	public InstructionAdd(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isDataFromMemory() {
		return true;
	}

	@Override
	public boolean isAccumulatorUsed() {
		return true;
	}

	@Override
	public boolean isAccumulatorModified() {
		return true;
	}

	@Override
	public Instruction getReplacement(EdsacRoutine routine) {
		Long value = routine.scalarValue(dataInfo);
		return value != null && value == 0 ? new InstructionNoOp() : null;
	}

	@Override
	public Instruction getCompressedWithNextInstruction(EdsacRoutine routine, Instruction next, boolean sameSection) {
		LowDataInfo secondInfo;
		int secondSign;
		if (next instanceof InstructionAdd add) {
			secondInfo = add.dataInfo;
			secondSign = 1;
		}
		else if (next instanceof InstructionSubtract sub) {
			if (dataInfo.equalsOther(sub.dataInfo, false)) {
				return new InstructionNoOp();
			}
			secondInfo = sub.dataInfo;
			secondSign = -1;
		}
		else {
			return null;
		}
		
		Long firstRaw = routine.scalarValue(dataInfo);
		Long secondRaw = routine.scalarValue(secondInfo);
		if (firstRaw == null || secondRaw == null) {
			return null;
		}
		
		EdsacInt firstValue = EdsacInt.of(firstRaw);
		EdsacInt secondValue = EdsacInt.of(secondRaw);
		if (secondSign < 0) {
			secondValue = secondValue.minus();
		}
		
		EdsacInt sum = firstValue.plus(secondValue);
		long signedSum = sum.toSigned();
		if (signedSum == 0) {
			return new InstructionNoOp();
		}
		else if (signedSum > 0) {
			return new InstructionAdd(routine.constantDataInfo(sum.toLong()));
		}
		else {
			return new InstructionSubtract(routine.constantDataInfo(sum.minus().toLong()));
		}
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.ADD;
	}
}
