package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;

public class PrintCharRedstoneRoutine extends RedstoneRoutine {
	
	public PrintCharRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		DataId x = params.get(0).dataId();
		
		loadScalar(text, x);
		text.add(new InstructionOutput());
		
		returnFromSubroutine(text);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 1;
	}
}
