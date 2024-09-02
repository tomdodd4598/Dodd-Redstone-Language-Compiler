package drlc.low.edsac;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.IntUnaryOperator;

import drlc.Helpers.Pair;
import drlc.intermediate.action.*;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.drc1.instruction.InstructionHalt;
import drlc.low.drc1.instruction.address.InstructionAddress;
import drlc.low.drc1.instruction.subroutine.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.jump.*;
import drlc.low.instruction.address.IInstructionAddress;

public class EdsacRoutine extends LowRoutine<EdsacCode, EdsacRoutine, Instruction> {
	
	public EdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
		
		if (isStackRoutine()) {
			throw new IllegalArgumentException(String.format("Recursive routine \"%s\" is not supported by EDSAC backend!", intermediate));
		}
	}
	
	public boolean generateInstructions() {
		if (generated) {
			return false;
		}
		generated = true;
		
		if (!isRootRoutine()) {
			// TODO Complete the Wheeler jump
		}
		
		generateInstructionsInternal();
		
		return true;
	}
	
	protected void generateInstructionsInternal() {
		List<List<Action>> body = intermediate.body;
		for (int i = 0; i < body.size(); ++i) {
			List<Instruction> text = new ArrayList<>();
			sectionTextMap.put(i, text);
			
			for (Action action : body.get(i)) {
				if (action instanceof AssignmentAction aa) {
					loadThen(text, false, aa.arg, x -> storeAt(text, aa.target, x));
				}
				
				else if (action instanceof BinaryOpAction boa) {
					loadScalar(text, boa.arg1);
					binaryOp(text, boa.type, boa.arg2);
					storeScalar(text, boa.target);
				}
				
				else if (action instanceof CallAction ca) {
					DataId target = ca.target, caller = ca.caller;
					List<DataId> args = ca.args;
					
					Function callerFunction = caller.getFunction();
					EdsacRoutine subroutine = callerFunction == null ? null : code.getRoutine(callerFunction);
					boolean indirectCall = subroutine == null;
					int targetSize = target.typeInfo.getSize(), argCount = args.size();
					
					for (int k = 0; k < argCount; ++k) {
						DataId paramId = subroutine.params.get(k).dataId();
						loadThen(text, false, args.get(k), x -> subroutine.storeAt(text, paramId, x));
					}
					if (targetSize > 1) {
						loadScalar(text, target.removeDereference(null));
						subroutine.storeScalar(text, subroutine.params.get(argCount).dataId());
					}
					
					if (indirectCall) {
						loadScalar(text, caller);
					}
					else {
						text.add(new InstructionLoadSubroutineAddress(callerFunction));
					}
					text.add(new InstructionCallSubroutine(indirectCall));
					
					if (targetSize == 1) {
						storeScalar(text, target);
					}
				}
				
				else if (action instanceof CompoundAssignmentAction caa) {
					int acc = 0;
					for (DataId arg : caa.args) {
						int offset = acc;
						loadThen(text, false, arg, x -> storeAt(text, caa.target, x + offset));
						acc += arg.typeInfo.getSize();
					}
				}
				
				else if (action instanceof ConditionalJumpAction cja) {
					conditionalJump(text, cja.getTarget(), cja.jumpCondition);
				}
				
				else if (action instanceof ExitAction ea) {
					loadScalar(text, ea.arg);
					text.add(new InstructionHalt());
				}
				
				else if (action instanceof JumpAction ja) {
					jump(text, ja.getTarget());
				}
				
				else if (action instanceof NoOpAction) {
					text.add(new InstructionNoOp());
				}
				
				else if (action instanceof ReturnAction ra) {
					DataId arg = ra.arg;
					int size = arg.typeInfo.getSize();
					if (size == 1) {
						loadScalar(text, arg);
					}
					else if (size > 1) {
						DataId target = params.get(params.size() - 1).dataId().addDereference(null);
						loadThen(text, false, arg, x -> storeAt(text, target, x));
					}
					returnFromSubroutine(text);
				}
				
				else if (action instanceof UnaryOpAction uoa) {
					unaryOp(text, uoa.type, uoa.arg);
					storeScalar(text, uoa.target);
				}
				
				else {
					throw new IllegalArgumentException(String.format("Encountered unknown action \"%s\"!", action));
				}
			}
		}
	}
	
	public void regenerateDataInfo() {
		for (List<Instruction> section : sectionTextMap.values()) {
			for (Instruction instruction : section) {
				if (instruction instanceof IInstructionAddress instructionAddress) {
					instructionAddress.regenerateDataInfo();
				}
			}
		}
	}
	
	public void generateTextAddresses() {
		int sectionAddressOffset = 0;
		for (Entry<Integer, List<Instruction>> entry : sectionTextMap.entrySet()) {
			sectionAddressMap.put(entry.getKey(), sectionAddressOffset);
			sectionAddressOffset += entry.getValue().stream().mapToInt(Instruction::size).sum();
		}
		code.textAddressMap.put(function, code.addressOffset);
		code.addressOffset += sectionAddressOffset;
	}
	
	public void generateDataAddresses() {
		int dataAddressOffset = 0;
		for (Pair<DataId, LowDataSpan> pair : localSpanMap.values()) {
			dataAddressOffset += addAddressEntry(localAddressMap, pair.right, dataAddressOffset, x -> x + code.addressOffset);
		}
		for (Pair<DataId, LowDataSpan> pair : tempSpanMap.values()) {
			dataAddressOffset += addAddressEntry(tempAddressMap, pair.right, dataAddressOffset, x -> x + code.addressOffset);
		}
		code.addressOffset += dataAddressOffset;
	}
	
	protected static int addAddressEntry(Map<LowDataSpan, LowAddressSlice> addressMap, LowDataSpan span, int addressOffset, IntUnaryOperator function) {
		int size = span.size;
		int start = Math.min(function.applyAsInt(addressOffset), function.applyAsInt(addressOffset + Math.max(0, size - 1)));
		addressMap.put(span, new LowAddressSlice(start, size));
		return size;
	}
	
	public void finalizeInstructions() {
		for (Entry<Integer, List<Instruction>> entry : sectionTextMap.entrySet()) {
			int instructionAddress = sectionAddressMap.get(entry.getKey());
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				int instructionSize = instruction.size();
				
				if (instruction instanceof InstructionAddress ia) {
					ia.address = (short) getAddress(ia.dataInfo);
				}
				
				else if (instruction instanceof InstructionCallSubroutine ics) {
					ics.returnAddress = (short) (code.textAddressMap.get(function) + instructionAddress + instructionSize);
				}
				
				else if (instruction instanceof InstructionLoadSubroutineAddress ilsa) {
					ilsa.setValue(code.textAddressMap.get(ilsa.function).shortValue());
				}
				
				else if (instruction instanceof InstructionJump ij) {
					ij.address = (short) (code.textAddressMap.get(function) + sectionAddressMap.get(ij.section));
				}
				
				instructionAddress += instructionSize;
			}
		}
	}
	
	// Instructions
	
	protected void jump(List<Instruction> text, int section) {
		text.add(new InstructionJumpIfMoreThanOrEqualToZero(section));
		text.add(new InstructionJumpIfLessThanZero(section));
	}
}
