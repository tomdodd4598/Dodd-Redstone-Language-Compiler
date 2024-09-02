package drlc.low.drc1.builtin;

import java.util.*;

import drlc.Global;
import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.*;
import drlc.low.drc1.instruction.set.InstructionSetIsLessThanZero;

public class PrintNatRedstoneRoutine extends RedstoneRoutine {
	
	public PrintNatRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> positiveText = new ArrayList<>(), negativeText = new ArrayList<>(), arithmeticText = new ArrayList<>(), callText = new ArrayList<>();
		sectionTextMap.put(0, positiveText);
		sectionTextMap.put(1, negativeText);
		sectionTextMap.put(2, arithmeticText);
		sectionTextMap.put(3, callText);
		
		DataId x = params.get(0).dataId();
		DataId t = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		DataId b = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		DataId c = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		
		loadScalar(positiveText, x);
		positiveText.add(new InstructionSetIsLessThanZero());
		storeScalar(positiveText, b);
		positiveText.add(new InstructionConditionalJumpIfNotZero(1));
		
		loadImmediate(positiveText, (short) 10000);
		storeScalar(positiveText, t);
		loadScalar(positiveText, x);
		positiveText.add(new InstructionJump(3));
		
		loadImmediate(negativeText, (short) 1000);
		storeScalar(negativeText, t);
		
		loadScalar(negativeText, x);
		negativeText.add(new InstructionAddImmediate((short) 5536));
		storeScalar(negativeText, c);
		negativeText.add(new InstructionConditionalJumpIfLessThanZero(2));
		
		loadImmediate(negativeText, (short) 54);
		negativeText.add(new InstructionOutput());
		
		loadScalar(negativeText, c);
		negativeText.add(new InstructionJump(3));
		
		loadScalar(arithmeticText, x);
		arithmeticText.add(new InstructionSubtractImmediate((short) 30000));
		arithmeticText.add(new InstructionDivideImmediate((short) 10000));
		
		arithmeticText.add(new InstructionAddImmediate((short) 51));
		arithmeticText.add(new InstructionOutput());
		
		arithmeticText.add(new InstructionSubtractImmediate((short) 48));
		arithmeticText.add(new InstructionMultiplyImmediate((short) 10000));
		storeScalar(arithmeticText, c);
		
		loadScalar(arithmeticText, x);
		binaryOp(arithmeticText, BinaryActionType.INT_MINUS_INT, c);
		
		builtInSubroutine(callText, Global.PRINT_DIGITS, () -> loadScalar(callText, t), () -> loadScalar(callText, b));
		
		returnFromSubroutine(callText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 4;
	}
}
