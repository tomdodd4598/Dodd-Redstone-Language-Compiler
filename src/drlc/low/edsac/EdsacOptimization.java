package drlc.low.edsac;

import java.util.List;

import drlc.low.edsac.instruction.*;

public class EdsacOptimization {
	
	public static boolean removeNoOps(EdsacRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				if (section.get(i) instanceof InstructionNoOp) {
					flag = true;
					section.remove(i);
				}
			}
		}
		return flag;
	}
}
