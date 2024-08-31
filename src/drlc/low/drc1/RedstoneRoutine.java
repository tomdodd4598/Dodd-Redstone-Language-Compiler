package drlc.low.drc1;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.IntStream;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.action.*;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.address.*;
import drlc.low.drc1.instruction.address.offset.*;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.*;
import drlc.low.drc1.instruction.pointer.*;
import drlc.low.drc1.instruction.set.*;
import drlc.low.drc1.instruction.subroutine.*;
import drlc.low.instruction.address.IInstructionAddress;

public class RedstoneRoutine extends LowRoutine<RedstoneCode, RedstoneRoutine, Instruction> {
	
	public RedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	public boolean generateInstructions() {
		if (generated) {
			return false;
		}
		generated = true;
		
		if (isRootRoutine()) {
			if (code.requiresStack) {
				List<Instruction> text = new ArrayList<>();
				textSectionMap.put(-1, text);
				text.add(new InstructionInitializeStackPointer());
				text.add(new InstructionMoveStackPointerToBasePointer());
			}
		}
		else if (isStackRoutine()) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put(-1, text);
			text.add(new InstructionPushBasePointer());
			text.add(new InstructionMoveStackPointerToBasePointer());
			text.add(new InstructionSubtractFromStackPointer());
		}
		
		generateInstructionsInternal();
		
		if (isStackRoutine()) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put(getFinalTextSectionKey(), text);
			text.add(new InstructionAddToStackPointer());
			text.add(new InstructionPopBasePointer());
			text.add(new InstructionReturnFromSubroutine());
		}
		
		return true;
	}
	
	protected void generateInstructionsInternal() {
		List<List<Action>> body = intermediate.body;
		for (int i = 0; i < body.size(); ++i) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put(i, text);
			
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
					RedstoneRoutine subroutine = callerFunction == null ? null : code.getRoutine(callerFunction);
					boolean indirectCall = subroutine == null;
					boolean isStackRoutine = indirectCall || subroutine.isStackRoutine();
					
					int targetSize = target.typeInfo.getSize(), argCount = args.size();
					if (isStackRoutine) {
						if (targetSize > 1) {
							loadScalar(text, target.removeDereference(null));
							text.add(new InstructionPush());
						}
						for (int k = argCount - 1; k >= 0; --k) {
							loadThen(text, true, args.get(k), x -> text.add(new InstructionPush()));
						}
					}
					else {
						for (int k = 0; k < argCount; ++k) {
							DataId paramId = subroutine.params.get(k).dataId();
							loadThen(text, false, args.get(k), x -> subroutine.storeAt(text, paramId, x));
						}
						if (targetSize > 1) {
							loadScalar(text, target.removeDereference(null));
							subroutine.storeScalar(text, subroutine.params.get(argCount).dataId());
						}
					}
					
					if (indirectCall) {
						loadScalar(text, caller);
					}
					else {
						text.add(new InstructionLoadSubroutineAddress(callerFunction));
					}
					text.add(new InstructionCallSubroutine(indirectCall));
					
					if (isStackRoutine) {
						int add = Helpers.sumToInt(args, x -> x.typeInfo.getSize()) + (targetSize > 1 ? 1 : 0);
						if (add > 0) {
							text.add(new InstructionAddToStackPointer((short) add));
						}
					}
					
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
		for (List<Instruction> section : textSectionMap.values()) {
			for (Instruction instruction : section) {
				if (instruction instanceof IInstructionAddress instructionAddress) {
					instructionAddress.regenerateDataInfo();
				}
			}
		}
		
		if (isStackRoutine()) {
			short stackSize = (short) (spanMapSize(dataSpanMap) + spanMapSize(tempSpanMap) - Helpers.sumToInt(params, x -> x.getTypeInfo().getSize()));
			if (stackSize < 0) {
				throw new IllegalArgumentException(String.format("Stack-based subroutine \"%s\" has unexpected stack size %s!", function, stackSize));
			}
			
			boolean flag = true;
			while (flag) {
				flag = false;
				for (Entry<Integer, List<Instruction>> entry : textSectionMap.entrySet()) {
					List<Instruction> section = entry.getValue();
					for (int i = 0; i < section.size(); ++i) {
						Instruction instruction = section.get(i);
						
						if (instruction instanceof InstructionAddToStackPointer iatsp) {
							if (iatsp.value == null) {
								flag = true;
								iatsp.value = stackSize;
							}
							if (iatsp.value == 0) {
								flag = true;
								section.remove(i);
							}
							else {
								flag |= RedstoneOptimization.compressWithNextInstruction(textSectionMap, entry.getKey(), i, true);
							}
						}
						
						else if (instruction instanceof InstructionSubtractFromStackPointer isfsp) {
							if (isfsp.value == null) {
								flag = true;
								isfsp.value = stackSize;
							}
							if (isfsp.value == 0) {
								flag = true;
								section.remove(i);
							}
							else {
								flag |= RedstoneOptimization.compressWithNextInstruction(textSectionMap, entry.getKey(), i, true);
							}
						}
					}
				}
			}
		}
	}
	
	public void generateTextAddresses() {
		int sectionAddressOffset = 0;
		for (Entry<Integer, List<Instruction>> entry : textSectionMap.entrySet()) {
			sectionAddressMap.put(entry.getKey(), sectionAddressOffset);
			sectionAddressOffset += entry.getValue().stream().mapToInt(x -> x.size(code.longAddress)).sum();
		}
		code.textAddressMap.put(function, code.addressOffset);
		code.addressOffset += sectionAddressOffset;
	}
	
	public void generateDataAddresses() {
		int dataAddressOffset = 0;
		if (isStackRoutine()) {
			int paramAddressOffset = 0;
			for (Pair<DataId, LowDataSpan> pair : dataSpanMap.values()) {
				if (pair.right.id < 0) {
					paramAddressOffset += addAddressEntry(dataAddressMap, pair.right, paramAddressOffset, x -> x + 2);
				}
				else {
					dataAddressOffset += addAddressEntry(dataAddressMap, pair.right, dataAddressOffset, x -> -x - 1);
				}
			}
			for (Pair<DataId, LowDataSpan> pair : tempSpanMap.values()) {
				dataAddressOffset += addAddressEntry(tempAddressMap, pair.right, dataAddressOffset, x -> -x - 1);
			}
		}
		else {
			for (Pair<DataId, LowDataSpan> pair : dataSpanMap.values()) {
				dataAddressOffset += addAddressEntry(dataAddressMap, pair.right, dataAddressOffset, x -> x + code.addressOffset);
			}
			for (Pair<DataId, LowDataSpan> pair : tempSpanMap.values()) {
				dataAddressOffset += addAddressEntry(tempAddressMap, pair.right, dataAddressOffset, x -> x + code.addressOffset);
			}
			code.addressOffset += dataAddressOffset;
		}
	}
	
	protected static int addAddressEntry(Map<LowDataSpan, LowAddressSlice> addressMap, LowDataSpan span, int addressOffset, IntUnaryOperator function) {
		int size = span.size;
		int start = Math.min(function.applyAsInt(addressOffset), function.applyAsInt(addressOffset + Math.max(0, size - 1)));
		addressMap.put(span, new LowAddressSlice(start, size));
		return size;
	}
	
	public void finalizeInstructions() {
		for (Entry<Integer, List<Instruction>> entry : textSectionMap.entrySet()) {
			int instructionAddress = sectionAddressMap.get(entry.getKey());
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				int instructionSize = instruction.size(code.longAddress);
				
				if (instruction instanceof InstructionAddress ia) {
					ia.address = (short) getAddress(ia.dataInfo);
				}
				
				else if (instruction instanceof InstructionAddressOffset iao) {
					iao.offset = (short) getAddress(iao.dataInfo);
				}
				
				else if (instruction instanceof InstructionCallSubroutine ics) {
					ics.returnAddress = (short) (code.textAddressMap.get(function) + instructionAddress + instructionSize);
					
					if (!ics.indirectCall && (i == 0 || !(section.get(i - 1) instanceof InstructionLoadSubroutineAddress))) {
						throw new IllegalArgumentException(String.format("Found unexpected direct subroutine call instruction \"%s\" not following call address load instruction as required!", instruction));
					}
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
	
	protected void loadImmediate(List<Instruction> text, short value) {
		if (!RedstoneCode.isLong((short) ~value)) {
			text.add(new InstructionNotImmediate((short) ~value));
		}
		else {
			text.add(new InstructionLoadImmediate(value));
		}
	}
	
	protected void conditionalJump(List<Instruction> text, int section, boolean jumpCondition) {
		if (jumpCondition) {
			text.add(new InstructionConditionalJumpIfNotZero(section));
		}
		else {
			text.add(new InstructionConditionalJumpIfZero(section));
		}
	}
	
	protected void jump(List<Instruction> text, int section) {
		text.add(new InstructionJump(section));
	}
	
	protected void returnFromSubroutine(List<Instruction> text) {
		text.add(isStackRoutine() ? new InstructionJump(getFinalTextSectionKey()) : new InstructionReturnFromSubroutine());
	}
	
	protected static IntStream loadStoreOffsets(int size, boolean reverse) {
		IntStream offsets = IntStream.range(0, size);
		if (reverse) {
			offsets = offsets.map(x -> size - x - 1);
		}
		return offsets;
	}
	
	protected void loadThen(List<Instruction> text, boolean reverse, DataId arg, IntConsumer consumer) {
		Function function = arg.getFunction();
		if (function != null) {
			text.add(new InstructionLoadSubroutineAddress(function));
			consumer.accept(0);
		}
		else if (arg instanceof TransientDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add a transient load instruction! %s", arg));
		}
		else if (arg instanceof ValueDataId valueDataId) {
			List<Short> values = RedstoneCode.raw(valueDataId.value);
			IntStream offsets = loadStoreOffsets(values.size(), reverse);
			offsets.forEach(x -> {
				loadImmediate(text, values.get(x));
				consumer.accept(x);
			});
		}
		else if (arg.isAddress()) {
			LowDataInfo argInfo = getDataInfo(arg.addDereference(null), 0);
			text.add(argInfo.isStackData() ? new InstructionLoadAddressOffset(argInfo) : new InstructionLoadAddress(argInfo));
			consumer.accept(0);
		}
		else {
			IntStream offsets = loadStoreOffsets(arg.typeInfo.getSize(), reverse);
			LowDataInfo loadInfo = getDataInfo(arg, 0);
			if (arg.dereferenceLevel == 0) {
				offsets.forEach(x -> {
					LowDataInfo offsetInfo = loadInfo.offsetBy(x);
					text.add(loadInfo.isStackData() ? new InstructionLoadAOffset(offsetInfo) : new InstructionLoadA(offsetInfo));
					consumer.accept(x);
				});
			}
			else {
				offsets.forEach(x -> {
					text.add(loadInfo.isStackData() ? new InstructionLoadAOffset(loadInfo) : new InstructionLoadA(loadInfo));
					for (int i = 0; i < arg.dereferenceLevel - 1; ++i) {
						text.add(new InstructionDereferenceA());
					}
					text.add(new InstructionAddImmediate((short) x));
					text.add(new InstructionDereferenceA());
					consumer.accept(x);
				});
			}
		}
	}
	
	protected void loadScalar(List<Instruction> text, DataId arg) {
		loadThen(text, false, arg, x -> {});
	}
	
	protected void storeAt(List<Instruction> text, DataId target, int offset) {
		if (target instanceof TransientDataId) {
			return;
		}
		else if (target instanceof ValueDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add an immediate store instruction! %s", target));
		}
		else if (target.isAddress()) {
			throw new IllegalArgumentException(String.format("Attempted to add an address store instruction! %s", target));
		}
		else {
			LowDataInfo storeInfo = getDataInfo(target, 0);
			if (target.dereferenceLevel == 0) {
				LowDataInfo offsetInfo = storeInfo.offsetBy(offset);
				text.add(storeInfo.isStackData() ? new InstructionStoreOffset(offsetInfo) : new InstructionStore(offsetInfo));
			}
			else {
				text.add(storeInfo.isStackData() ? new InstructionLoadBOffset(storeInfo) : new InstructionLoadB(storeInfo));
				for (int i = 0; i < target.dereferenceLevel - 1; ++i) {
					text.add(new InstructionDereferenceB());
				}
				text.add(new InstructionAddBImmediate((short) offset));
				text.add(new InstructionStoreAToBAddress());
			}
		}
	}
	
	protected void storeScalar(List<Instruction> text, DataId target) {
		storeAt(text, target, 0);
	}
	
	protected void binaryOp(List<Instruction> text, BinaryActionType type, DataId arg) {
		if (arg instanceof ValueDataId valueDataId) {
			short value = RedstoneCode.raw(valueDataId.value).get(0);
			Consumer<String> binaryOpBuiltInSubroutine = x -> builtInSubroutine(text, x, () -> loadImmediate(text, value));
			switch (type) {
				case BOOL_EQUAL_TO_BOOL:
				case INT_EQUAL_TO_INT:
				case CHAR_EQUAL_TO_CHAR:
					text.add(new InstructionXorImmediate(value));
					text.add(new InstructionSetIsZero());
					break;
				case BOOL_NOT_EQUAL_TO_BOOL:
				case INT_NOT_EQUAL_TO_INT:
				case CHAR_NOT_EQUAL_TO_CHAR:
					text.add(new InstructionXorImmediate(value));
					text.add(new InstructionSetIsNotZero());
					break;
				case BOOL_LESS_THAN_BOOL:
				case CHAR_LESS_THAN_CHAR:
					text.add(new InstructionSubtractImmediate(value));
					text.add(new InstructionSetIsLessThanZero());
					break;
				case INT_LESS_THAN_INT:
					if (value != 0) {
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
					}
					text.add(new InstructionSetIsLessThanZero());
					break;
				case NAT_LESS_THAN_NAT:
					binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
					text.add(new InstructionSetIsLessThanZero());
					break;
				case BOOL_LESS_OR_EQUAL_BOOL:
				case CHAR_LESS_OR_EQUAL_CHAR:
					text.add(new InstructionSubtractImmediate(value));
					text.add(new InstructionSetIsLessThanOrEqualToZero());
					break;
				case INT_LESS_OR_EQUAL_INT:
					if (value != 0) {
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
					}
					text.add(new InstructionSetIsLessThanOrEqualToZero());
					break;
				case NAT_LESS_OR_EQUAL_NAT:
					if (value == 0) {
						binaryOp(text, BinaryActionType.INT_EQUAL_TO_INT, arg);
					}
					else {
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsLessThanOrEqualToZero());
					}
					break;
				case BOOL_MORE_THAN_BOOL:
				case CHAR_MORE_THAN_CHAR:
					text.add(new InstructionSubtractImmediate(value));
					text.add(new InstructionSetIsMoreThanZero());
					break;
				case INT_MORE_THAN_INT:
					if (value != 0) {
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
					}
					text.add(new InstructionSetIsMoreThanZero());
					break;
				case NAT_MORE_THAN_NAT:
					if (value == 0) {
						binaryOp(text, BinaryActionType.INT_NOT_EQUAL_TO_INT, arg);
					}
					else {
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsMoreThanZero());
					}
					break;
				case BOOL_MORE_OR_EQUAL_BOOL:
				case CHAR_MORE_OR_EQUAL_CHAR:
					text.add(new InstructionSubtractImmediate(value));
					text.add(new InstructionSetIsMoreThanOrEqualToZero());
					break;
				case INT_MORE_OR_EQUAL_INT:
					if (value != 0) {
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
					}
					text.add(new InstructionSetIsMoreThanOrEqualToZero());
					break;
				case NAT_MORE_OR_EQUAL_NAT:
					binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
					text.add(new InstructionSetIsMoreThanOrEqualToZero());
					break;
				case INT_PLUS_INT:
					text.add(new InstructionAddImmediate(value));
					break;
				case CHAR_PLUS_CHAR:
					text.add(new InstructionAddImmediate(value));
					text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
					break;
				case BOOL_AND_BOOL:
				case INT_AND_INT:
				case CHAR_AND_CHAR:
					text.add(new InstructionAndImmediate(value));
					break;
				case BOOL_OR_BOOL:
				case INT_OR_INT:
				case CHAR_OR_CHAR:
					text.add(new InstructionOrImmediate(value));
					break;
				case BOOL_XOR_BOOL:
				case INT_XOR_INT:
				case CHAR_XOR_CHAR:
					text.add(new InstructionXorImmediate(value));
					break;
				case INT_MINUS_INT:
					text.add(new InstructionSubtractImmediate(value));
					break;
				case CHAR_MINUS_CHAR:
					text.add(new InstructionSubtractImmediate(value));
					text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
					break;
				case INT_MULTIPLY_INT:
					text.add(new InstructionMultiplyImmediate(value));
					break;
				case INT_DIVIDE_INT:
					text.add(new InstructionDivideImmediate(value));
					break;
				case INT_REMAINDER_INT:
					text.add(new InstructionRemainderImmediate(value));
					break;
				case INT_LEFT_SHIFT_INT:
					text.add(new InstructionLeftShiftImmediate(RedstoneCode.lowBits(value)));
					break;
				case INT_RIGHT_SHIFT_INT:
					text.add(new InstructionRightShiftImmediate(RedstoneCode.lowBits(value)));
					break;
				case NAT_RIGHT_SHIFT_INT:
					builtInSubroutine(text, Global.NAT_RIGHT_SHIFT_INT, () -> loadImmediate(text, RedstoneCode.lowBits(value)));
					break;
				case INT_LEFT_ROTATE_INT:
					builtInSubroutine(text, Global.INT_LEFT_ROTATE_INT, () -> loadImmediate(text, RedstoneCode.lowBits(value)));
					break;
				case INT_RIGHT_ROTATE_INT:
					builtInSubroutine(text, Global.INT_RIGHT_ROTATE_INT, () -> loadImmediate(text, RedstoneCode.lowBits(value)));
					break;
				default:
					throw new IllegalArgumentException(String.format("Attempted to add immediate binary op instruction of unknown type! %s %s", type, arg.opErrorString()));
			}
		}
		else {
			LowDataInfo argInfo = getDataInfo(arg, 0);
			boolean isAddress = arg.isAddress();
			if (argInfo.isStackData()) {
				Consumer<String> binaryOpBuiltInSubroutine = x -> binaryOpBuiltInSubroutine(text, x, isAddress ? new InstructionLoadAddressOffset(argInfo) : new InstructionLoadAOffset(argInfo));
				switch (type) {
					case BOOL_EQUAL_TO_BOOL:
					case INT_EQUAL_TO_INT:
					case CHAR_EQUAL_TO_CHAR:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case BOOL_NOT_EQUAL_TO_BOOL:
					case INT_NOT_EQUAL_TO_INT:
					case CHAR_NOT_EQUAL_TO_CHAR:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case BOOL_LESS_THAN_BOOL:
					case CHAR_LESS_THAN_CHAR:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case INT_LESS_THAN_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsLessThanZero());
						break;
					case NAT_LESS_THAN_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsLessThanZero());
						break;
					case BOOL_LESS_OR_EQUAL_BOOL:
					case CHAR_LESS_OR_EQUAL_CHAR:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case INT_LESS_OR_EQUAL_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case NAT_LESS_OR_EQUAL_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case BOOL_MORE_THAN_BOOL:
					case CHAR_MORE_THAN_CHAR:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case INT_MORE_THAN_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case NAT_MORE_THAN_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case BOOL_MORE_OR_EQUAL_BOOL:
					case CHAR_MORE_OR_EQUAL_CHAR:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_MORE_OR_EQUAL_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case NAT_MORE_OR_EQUAL_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_PLUS_INT:
						text.add(isAddress ? new InstructionAddAddressOffset(argInfo) : new InstructionAddOffset(argInfo));
						break;
					case CHAR_PLUS_CHAR:
						text.add(new InstructionAddOffset(argInfo));
						text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
						break;
					case BOOL_AND_BOOL:
					case INT_AND_INT:
					case CHAR_AND_CHAR:
						text.add(new InstructionAndOffset(argInfo));
						break;
					case BOOL_OR_BOOL:
					case INT_OR_INT:
					case CHAR_OR_CHAR:
						text.add(new InstructionOrOffset(argInfo));
						break;
					case BOOL_XOR_BOOL:
					case INT_XOR_INT:
					case CHAR_XOR_CHAR:
						text.add(new InstructionXorOffset(argInfo));
						break;
					case INT_MINUS_INT:
						text.add(isAddress ? new InstructionSubtractAddressOffset(argInfo) : new InstructionSubtractOffset(argInfo));
						break;
					case CHAR_MINUS_CHAR:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
						break;
					case INT_MULTIPLY_INT:
						text.add(new InstructionMultiplyOffset(argInfo));
						break;
					case INT_DIVIDE_INT:
						text.add(new InstructionDivideOffset(argInfo));
						break;
					case INT_REMAINDER_INT:
						text.add(new InstructionRemainderOffset(argInfo));
						break;
					case INT_LEFT_SHIFT_INT:
						text.add(new InstructionLeftShiftOffset(argInfo));
						break;
					case INT_RIGHT_SHIFT_INT:
						text.add(new InstructionRightShiftOffset(argInfo));
						break;
					case NAT_RIGHT_SHIFT_INT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_RIGHT_SHIFT_INT);
						break;
					case INT_LEFT_ROTATE_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_LEFT_ROTATE_INT);
						break;
					case INT_RIGHT_ROTATE_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_RIGHT_ROTATE_INT);
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset binary op instruction of unknown type! %s %s", type, arg.opErrorString()));
				}
			}
			else {
				Consumer<String> binaryOpBuiltInSubroutine = x -> binaryOpBuiltInSubroutine(text, x, isAddress ? new InstructionLoadAddress(argInfo) : new InstructionLoadA(argInfo));
				switch (type) {
					case BOOL_EQUAL_TO_BOOL:
					case INT_EQUAL_TO_INT:
					case CHAR_EQUAL_TO_CHAR:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionXor(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case BOOL_NOT_EQUAL_TO_BOOL:
					case INT_NOT_EQUAL_TO_INT:
					case CHAR_NOT_EQUAL_TO_CHAR:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionXor(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case BOOL_LESS_THAN_BOOL:
					case CHAR_LESS_THAN_CHAR:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case INT_LESS_THAN_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsLessThanZero());
						break;
					case NAT_LESS_THAN_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsLessThanZero());
						break;
					case BOOL_LESS_OR_EQUAL_BOOL:
					case CHAR_LESS_OR_EQUAL_CHAR:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case INT_LESS_OR_EQUAL_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case NAT_LESS_OR_EQUAL_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case BOOL_MORE_THAN_BOOL:
					case CHAR_MORE_THAN_CHAR:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case INT_MORE_THAN_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case NAT_MORE_THAN_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case BOOL_MORE_OR_EQUAL_BOOL:
					case CHAR_MORE_OR_EQUAL_CHAR:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_MORE_OR_EQUAL_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_COMPARE_INT);
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case NAT_MORE_OR_EQUAL_NAT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_COMPARE_NAT);
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_PLUS_INT:
						text.add(isAddress ? new InstructionAddAddress(argInfo) : new InstructionAdd(argInfo));
						break;
					case CHAR_PLUS_CHAR:
						text.add(new InstructionAdd(argInfo));
						text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
						break;
					case BOOL_AND_BOOL:
					case INT_AND_INT:
					case CHAR_AND_CHAR:
						text.add(new InstructionAnd(argInfo));
						break;
					case BOOL_OR_BOOL:
					case INT_OR_INT:
					case CHAR_OR_CHAR:
						text.add(new InstructionOr(argInfo));
						break;
					case BOOL_XOR_BOOL:
					case INT_XOR_INT:
					case CHAR_XOR_CHAR:
						text.add(new InstructionXor(argInfo));
						break;
					case INT_MINUS_INT:
						text.add(isAddress ? new InstructionSubtractAddress(argInfo) : new InstructionSubtract(argInfo));
						break;
					case CHAR_MINUS_CHAR:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
						break;
					case INT_MULTIPLY_INT:
						text.add(new InstructionMultiply(argInfo));
						break;
					case INT_DIVIDE_INT:
						text.add(new InstructionDivide(argInfo));
						break;
					case INT_REMAINDER_INT:
						text.add(new InstructionRemainder(argInfo));
						break;
					case INT_LEFT_SHIFT_INT:
						text.add(new InstructionLeftShift(argInfo));
						break;
					case INT_RIGHT_SHIFT_INT:
						text.add(new InstructionRightShift(argInfo));
						break;
					case NAT_RIGHT_SHIFT_INT:
						binaryOpBuiltInSubroutine.accept(Global.NAT_RIGHT_SHIFT_INT);
						break;
					case INT_LEFT_ROTATE_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_LEFT_ROTATE_INT);
						break;
					case INT_RIGHT_ROTATE_INT:
						binaryOpBuiltInSubroutine.accept(Global.INT_RIGHT_ROTATE_INT);
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address binary op instruction of unknown type! %s %s", type, arg.opErrorString()));
				}
			}
		}
	}
	
	protected void binaryOpBuiltInSubroutine(List<Instruction> text, String name, Instruction... load) {
		builtInSubroutine(text, name, () -> Arrays.stream(load).forEach(text::add));
	}
	
	protected void unaryOp(List<Instruction> text, UnaryActionType type, DataId arg) {
		if (arg instanceof ValueDataId valueDataId) {
			short value = RedstoneCode.raw(valueDataId.value).get(0);
			switch (type) {
				case MINUS_INT:
					loadImmediate(text, (short) -value);
					break;
				case NOT_BOOL:
					loadImmediate(text, value);
					text.add(new InstructionSetIsZero());
					break;
				case NOT_INT:
					loadImmediate(text, (short) ~value);
					break;
				case NOT_CHAR:
					loadImmediate(text, RedstoneCode.charBits((short) ~value));
					break;
				default:
					throw new IllegalArgumentException(String.format("Attempted to add immediate unary op instruction of unknown type! %s %s", type, arg.opErrorString()));
			}
			
		}
		else {
			LowDataInfo argInfo = getDataInfo(arg, 0);
			if (argInfo.isStackData()) {
				switch (type) {
					case MINUS_INT:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetNegative());
						break;
					case NOT_BOOL:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case NOT_INT:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetNot());
						break;
					case NOT_CHAR:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetNot());
						text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset unary op instruction of unknown type! %s %s", type, arg.opErrorString()));
				}
			}
			else {
				switch (type) {
					case MINUS_INT:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetNegative());
						break;
					case NOT_BOOL:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case NOT_INT:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetNot());
						break;
					case NOT_CHAR:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetNot());
						text.add(new InstructionAndImmediate(RedstoneCode.CHAR_MASK));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address unary op instruction of unknown type! %s %s", type, arg.opErrorString()));
				}
			}
		}
	}
	
	protected void builtInSubroutine(List<Instruction> text, String name, Runnable... load) {
		Function builtInFunction = Main.generator.getBuiltInFunction(null, name);
		RedstoneRoutine subroutine = code.getRoutine(builtInFunction);
		subroutine.storeScalar(text, subroutine.params.get(0).dataId());
		for (int i = 0; i < load.length; ++i) {
			load[i].run();
			subroutine.storeScalar(text, subroutine.params.get(i + 1).dataId());
		}
		text.add(new InstructionLoadSubroutineAddress(builtInFunction));
		text.add(new InstructionCallSubroutine(false));
		intermediate.onRequiresNesting();
	}
}
