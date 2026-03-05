package drlc.low.edsac;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.IntStream;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.action.*;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.data.*;
import drlc.low.edsac.instruction.deferred.*;
import drlc.low.edsac.instruction.jump.*;
import drlc.low.edsac.instruction.wheeler.*;

public class EdsacRoutine extends LowRoutine<EdsacCode, EdsacRoutine, Instruction> {
	
	protected static final long MAX_ADDRESS = 0x7FFL, WHEELER_STORE_DELTA = 0x18000L;
	
	protected Map<Integer, DataId> tempDataMap = new HashMap<>();
	protected Map<DataId, LowDataInfo> addressDataMap = new HashMap<>();
	protected Map<Instruction, InstructionReturnAddressData> returnAddressMap = new IdentityHashMap<>();
	
	public EdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	public boolean generateInstructions() {
		if (generated) {
			return false;
		}
		generated = true;
		
		if (isRootRoutine()) {
			List<Instruction> initText = new ArrayList<>();
			sectionTextMap.put(-1, initText);
			
			initText.add(new InstructionAdd(constantDataInfo(MAX_ADDRESS)));
			storeData(initText, basePointerDataInfo(), false);
			storeData(initText, stackPointerDataInfo(), true);
		}
		else if (!isStackRoutine()) {
			List<Instruction> returnText = new ArrayList<>();
			sectionTextMap.put(-2, returnText);
			
			returnText.add(new InstructionPlaceholder());
			returnText.add(new InstructionPlaceholder());
			
			List<Instruction> patchText = new ArrayList<>();
			sectionTextMap.put(-1, patchText);
			
			patchText.add(new InstructionWheelerStore(function, -2, 0));
			patchText.add(new InstructionAdd(constantDataInfo(WHEELER_STORE_DELTA)));
			patchText.add(new InstructionWheelerStoreAndClear(function, -2, 1));
		}
		
		generateInstructionsInternal();
		
		if (isStackRoutine()) {
			List<Instruction> returnText = new ArrayList<>();
			sectionTextMap.put(-2, returnText);
			
			storeData(returnText, scratchDataInfo(0), true);
			
			loadData(returnText, basePointerDataInfo(), false);
			storeData(returnText, stackPointerDataInfo(), true);
			
			loadData(returnText, stackPointerDataInfo(), false);
			dynamicInstruction(returnText, EdsacOpcodes.ADD);
			storeData(returnText, basePointerDataInfo(), true);
			
			adjustStackPointer(returnText, 1);
			
			loadData(returnText, stackPointerDataInfo(), false);
			dynamicInstruction(returnText, EdsacOpcodes.ADD);
			Instruction placeholderE = new InstructionPlaceholder();
			patchInstruction(returnText, EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO, placeholderE);
			
			loadData(returnText, stackPointerDataInfo(), false);
			dynamicInstruction(returnText, EdsacOpcodes.ADD);
			Instruction placeholderG = new InstructionPlaceholder();
			patchInstruction(returnText, EdsacOpcodes.JUMP_IF_LESS_THAN_ZERO, placeholderG);
			
			loadData(returnText, scratchDataInfo(0), false);
			returnText.add(placeholderE);
			returnText.add(placeholderG);
			
			int stackSize = getStackSize();
			if (stackSize < 0) {
				throw new IllegalArgumentException(String.format("Stack-based subroutine \"%s\" has unexpected stack size %s!", function, stackSize));
			}
			
			List<Instruction> prologueText = new ArrayList<>();
			sectionTextMap.put(-1, prologueText);
			
			loadData(prologueText, stackPointerDataInfo(), true);
			prologueText.add(new InstructionSubtract(constantDataInfo(1)));
			storeData(prologueText, stackPointerDataInfo(), false);
			dynamicStore(prologueText, false, () -> loadData(prologueText, basePointerDataInfo(), false));
			
			loadData(prologueText, stackPointerDataInfo(), true);
			storeData(prologueText, basePointerDataInfo(), true);
			
			adjustStackPointer(prologueText, -stackSize);
		}
		
		return true;
	}
	
	protected void generateInstructionsInternal() {
		List<List<Action>> body = intermediate.body;
		for (int i = 0; i < body.size(); ++i) {
			List<Instruction> text = new ArrayList<>();
			sectionTextMap.put(i, text);
			
			for (Action action : body.get(i)) {
				if (action instanceof AssignmentAction aa) {
					if (!isRootRoutine() || !tryStaticAssignment(aa.target, Arrays.asList(aa.arg), new int[] {0})) {
						loadThen(text, false, aa.arg, x -> storeAt(text, aa.target, false, x));
					}
				}
				
				else if (action instanceof BinaryOpAction boa) {
					DataId arg2;
					if (boa.arg2.dereferenceLevel > 0 || (boa.arg2.isAddress() && getDataInfo(boa.arg2.addDereference(null), 0).isStackData())) {
						arg2 = tempDataInfo(2).dataId;
						loadThen(text, false, boa.arg2, x -> storeAt(text, arg2, true, x));
					}
					else {
						arg2 = boa.arg2;
					}
					loadScalar(text, boa.arg1);
					binaryOp(text, boa.type, arg2);
					storeScalar(text, boa.target, false);
				}
				
				else if (action instanceof CallAction ca) {
					DataId target = ca.target, caller = ca.caller;
					List<DataId> args = ca.args;
					
					Function callerFunction = caller.getFunction();
					EdsacRoutine subroutine = callerFunction == null ? null : code.getRoutine(callerFunction);
					boolean indirectCall = subroutine == null;
					boolean isStackRoutine = indirectCall || subroutine.isStackRoutine();
					
					int targetSize = target.typeInfo.getSize(), argCount = args.size();
					
					if (isStackRoutine) {
						int delta = Helpers.sumToInt(args, x -> x.typeInfo.getSize()) + 1 + (targetSize > 1 ? 1 : 0);
						if (delta > 0) {
							adjustStackPointer(text, -delta);
						}
						
						int[] pushIndex = {0};
						IntConsumer push = x -> pushToOffset(text, delta - (pushIndex[0]++) - 1);
						
						if (targetSize > 1) {
							loadScalar(text, target.removeDereference(null));
							push.accept(0);
						}
						for (int j = argCount - 1; j >= 0; --j) {
							loadThen(text, true, args.get(j), push);
						}
						
						InstructionReturnAddressData irad = new InstructionReturnAddressData();
						LowDataInfo returnAddressDataInfo = returnAddressDataInfo(irad);
						loadData(text, returnAddressDataInfo, true);
						push.accept(0);
						
						if (indirectCall) {
							loadScalar(text, caller);
						}
						else {
							loadData(text, ensureFunctionInfo(callerFunction, caller), true);
						}
						Instruction placeholder = dynamicInstruction(text, EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO);
						returnAddressMap.put(placeholder, irad);
						
						if (targetSize == 1) {
							storeScalar(text, target, false);
						}
						
						if (delta > 0) {
							adjustStackPointer(text, delta);
						}
					}
					else {
						if (subroutine == null) {
							throw new IllegalArgumentException(String.format("Encountered unknown subroutine \"%s\"!", callerFunction));
						}
						
						for (int j = 0; j < argCount; ++j) {
							DataId paramId = subroutine.params.get(j).dataId();
							loadThen(text, false, args.get(j), x -> subroutine.storeAt(text, paramId, false, x));
						}
						
						if (targetSize > 1) {
							loadScalar(text, target.removeDereference(null));
							subroutine.storeScalar(text, subroutine.params.get(argCount).dataId(), false);
						}
						
						LowDataInfo returnAddressDataInfo = returnAddressDataInfo(new InstructionWheelerReturn());
						InstructionWheelerReturn iwr = (InstructionWheelerReturn) code.staticDataMap.get(returnAddressDataInfo);
						
						loadWheelerReturnAddress(text, returnAddressDataInfo);
						text.add(new InstructionWheelerJump(subroutine.function, iwr));
					}
					
					if (targetSize == 1 && !isStackRoutine) {
						storeScalar(text, target, false);
					}
				}
				
				else if (action instanceof CompoundAssignmentAction caa) {
					int[] acc = {0}, offsets = caa.args.stream().mapToInt(x -> {
						int offset = acc[0];
						acc[0] += x.typeInfo.getSize();
						return offset;
					}).toArray();
					
					if (!isRootRoutine() || !tryStaticAssignment(caa.target, caa.args, offsets)) {
						for (int j = 0; j < caa.args.size(); ++j) {
							int offset = offsets[j];
							loadThen(text, false, caa.args.get(j), x -> storeAt(text, caa.target, false, x + offset));
						}
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
						loadThen(text, false, arg, x -> storeAt(text, target, false, x));
					}
					returnFromSubroutine(text);
				}
				
				else if (action instanceof UnaryOpAction uoa) {
					unaryOp(text, uoa.type, uoa.arg);
					storeScalar(text, uoa.target, false);
				}
				
				else {
					throw new IllegalArgumentException(String.format("Encountered unknown action \"%s\"!", action));
				}
			}
		}
	}
	
	public void prepareDataInfoRegeneration() {
		localSpanMap.clear();
		tempSpanMap.clear();
		
		generateParamDataInfo();
	}
	
	protected boolean usesClearAccumulator() {
		return sectionTextMap.values().stream().flatMap(List::stream).anyMatch(InstructionClearAccumulator.class::isInstance);
	}
	
	@Override
	public void regenerateDataInfoInternal() {
		super.regenerateDataInfoInternal();
		if (usesClearAccumulator()) {
			clearDataInfo();
		}
	}
	
	public void regenerateDataInfo() {
		if (!isRootRoutine()) {
			regenerateDataInfoInternal();
		}
	}
	
	public void generateTextAddresses() {
		if (isRootRoutine()) {
			regenerateDataInfoInternal();
		}
		
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
		if (isStackRoutine()) {
			int paramAddressOffset = 0;
			for (Pair<DataId, LowDataSpan> pair : localSpanMap.values()) {
				if (pair.right.id < 0) {
					paramAddressOffset += addAddressEntry(localAddressMap, pair.right, paramAddressOffset, x -> x + 2);
				}
				else {
					dataAddressOffset += addAddressEntry(localAddressMap, pair.right, dataAddressOffset, x -> -x - 1);
				}
			}
			for (Pair<DataId, LowDataSpan> pair : tempSpanMap.values()) {
				dataAddressOffset += addAddressEntry(tempAddressMap, pair.right, dataAddressOffset, x -> -x - 1);
			}
		}
		else {
			for (Pair<DataId, LowDataSpan> pair : localSpanMap.values()) {
				dataAddressOffset += addAddressEntry(localAddressMap, pair.right, dataAddressOffset, x -> x + code.addressOffset);
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
		Map<Integer, Map<Instruction, Integer>> sectionOffsetMap = new HashMap<>();
		for (Entry<Integer, List<Instruction>> entry : sectionTextMap.entrySet()) {
			Map<Instruction, Integer> offsetMap = new IdentityHashMap<>();
			int offset = 0;
			for (Instruction instruction : entry.getValue()) {
				offsetMap.put(instruction, offset);
				offset += instruction.size();
			}
			sectionOffsetMap.put(entry.getKey(), offsetMap);
		}
		
		for (Entry<Integer, List<Instruction>> entry : sectionTextMap.entrySet()) {
			int instructionAddress = sectionAddressMap.get(entry.getKey());
			List<Instruction> section = entry.getValue();
			for (Instruction instruction : section) {
				int instructionSize = instruction.size();
				
				InstructionReturnAddressData rad = returnAddressMap.get(instruction);
				if (rad != null) {
					rad.setAddress(code.textAddressMap.get(function) + instructionAddress + instructionSize);
				}
				
				if (instruction instanceof InstructionClearAccumulator ica) {
					ica.address = getAddress(clearDataInfo());
				}
				
				else if (instruction instanceof InstructionAddress ia) {
					ia.address = getAddress(ia.dataInfo);
				}
				
				else if (instruction instanceof InstructionJump ij) {
					ij.address = code.textAddressMap.get(function) + sectionAddressMap.get(ij.section);
				}
				
				else if (instruction instanceof InstructionDeferred id) {
					Integer offset = sectionOffsetMap.get(id.section).get(id.target);
					if (offset == null) {
						throw new IllegalArgumentException("Failed to resolve deferred target instruction!");
					}
					id.address = textAddress(id.function, id.section, offset);
				}
				
				else if (instruction instanceof InstructionWheelerJump iwj) {
					iwj.address = textAddress(iwj.function, -1, 0);
					iwj.iwr.setAddress(code.textAddressMap.get(function) + instructionAddress + instructionSize);
				}
				
				else if (instruction instanceof InstructionWheelerStore iws) {
					iws.address = textAddress(iws.function, iws.section, iws.offset);
				}
				
				else if (instruction instanceof InstructionWheelerStoreAndClear iwsac) {
					iwsac.address = textAddress(iwsac.function, iwsac.section, iwsac.offset);
				}
				
				instructionAddress += instructionSize;
			}
		}
	}
	
	protected int getSectionIndex(List<Instruction> text) {
		for (Entry<Integer, List<Instruction>> entry : sectionTextMap.entrySet()) {
			if (entry.getValue() == text) {
				return entry.getKey();
			}
		}
		throw new IllegalArgumentException("Encountered unexpected text section!");
	}
	
	// Helpers
	
	protected LowDataInfo tempDataInfo(int key) {
		DataId dataId = tempDataMap.get(key);
		if (dataId == null) {
			tempDataMap.put(key, dataId = function.functionScope.nextLocalDataId(intermediate, Main.generator.intTypeInfo));
		}
		return getDataInfo(dataId, 0);
	}
	
	public LowDataInfo constantDataInfo(long value) {
		ValueDataId valueDataId = intValueDataId(value);
		LowDataInfo info = getDataInfo(valueDataId, 0);
		code.staticDataMap.putIfAbsent(info, new InstructionValueData(EdsacCode.raw(valueDataId.value)));
		return info;
	}
	
	public LowDataInfo constantDataInfo(EdsacInt value) {
		return constantDataInfo(value.toLong());
	}
	
	protected LowDataInfo constantDataInfo(EdsacChar value) {
		return constantDataInfo(value.toInt());
	}
	
	protected LowDataInfo constantDataInfo(String str) {
		return constantDataInfo(EdsacInt.of(str));
	}
	
	protected LowDataInfo returnAddressDataInfo(Instruction returnAddressInstruction) {
		DataId dataId = Main.generator.nextGlobalDataId(Main.generator.intTypeInfo);
		LowDataInfo info = getDataInfo(dataId, 0);
		code.staticDataMap.putIfAbsent(info, returnAddressInstruction);
		return info;
	}
	
	protected LowDataInfo ensureValueInfo(ValueDataId valueDataId) {
		LowDataInfo info = getDataInfo(valueDataId, 0);
		code.staticDataMap.putIfAbsent(info, new InstructionValueData(EdsacCode.raw(valueDataId.value)));
		return info;
	}
	
	protected LowDataInfo ensureFunctionInfo(Function function, DataId arg) {
		LowDataInfo info = getDataInfo(arg, 0);
		code.staticDataMap.putIfAbsent(info, new InstructionSubroutineAddressData(function));
		return info;
	}
	
	protected LowDataInfo ensureAddressInfo(DataId arg) {
		LowDataInfo info = addressDataMap.get(arg);
		if (info == null) {
			info = getDataInfo(new ValueDataId(new AddressValue(null, arg.typeInfo, code.addressId++)), 0);
			code.staticDataMap.putIfAbsent(info, new InstructionAddressData(getDataInfo(arg.addDereference(null), 0)));
			addressDataMap.put(arg, info);
		}
		return info;
	}
	
	protected ValueDataId intValueDataId(long value) {
		return new ValueDataId(Main.generator.intValue(value));
	}
	
	protected ValueDataId natValueDataId(long value) {
		return new ValueDataId(Main.generator.natValue(value));
	}
	
	protected LowDataInfo ensureDataInfo(DataId arg) {
		Function function = arg.getFunction();
		if (function != null) {
			return ensureFunctionInfo(function, arg);
		}
		else if (arg instanceof ValueDataId valueDataId) {
			return ensureValueInfo(valueDataId);
		}
		else if (arg.isAddress()) {
			return ensureAddressInfo(arg);
		}
		else if (arg.dereferenceLevel == 0) {
			return getDataInfo(arg, 0);
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to ensure dereferenced data! %s", arg));
		}
	}
	
	protected LowDataInfo basePointerDataInfo() {
		return getDataInfo(code.basePointerDataId(), 0);
	}
	
	protected LowDataInfo stackPointerDataInfo() {
		return getDataInfo(code.stackPointerDataId(), 0);
	}
	
	protected LowDataInfo clearDataInfo() {
		return getDataInfo(code.clearDataId(), 0);
	}
	
	protected LowDataInfo scratchDataInfo(int index) {
		return getDataInfo(code.scratchDataId(index), 0);
	}
	
	protected boolean isScratchDataInfo(LowDataInfo info) {
		return info.offset == 0 && code.isScratchDataId(info.dataId);
	}
	
	protected boolean isPrivateDataInfo(LowDataInfo info) {
		return isScratchDataInfo(info) || info.isTempData();
	}
	
	public Long scalarValue(LowDataInfo dataInfo) {
		if (dataInfo.dataId instanceof ValueDataId valueDataId) {
			Value<?> value = valueDataId.value;
			if (value instanceof BasicValue && dataInfo.offset == 0 && dataInfo.span.size == 1) {
				try {
					List<EdsacInt> values = EdsacCode.raw(value);
					if (values.size() == 1) {
						return values.get(0).toLong();
					}
				}
				catch (RuntimeException e) {}
			}
		}
		return null;
	}
	
	// Instructions
	
	protected void conditionalJump(List<Instruction> text, int section, boolean jumpCondition) {
		if (jumpCondition) {
			text.add(new InstructionJumpIfLessThanZero(section));
		}
		else {
			text.add(new InstructionJumpIfMoreThanOrEqualToZero(section));
		}
	}
	
	protected void jump(List<Instruction> text, int section) {
		text.add(new InstructionJumpIfMoreThanOrEqualToZero(section));
		text.add(new InstructionJumpIfLessThanZero(section));
	}
	
	protected void returnFromSubroutineIfMoreThanOrEqualToZero(List<Instruction> text) {
		text.add(new InstructionJumpIfMoreThanOrEqualToZero(-2));
	}
	
	protected void returnFromSubroutineIfLessThanZero(List<Instruction> text) {
		text.add(new InstructionJumpIfLessThanZero(-2));
	}
	
	protected void returnFromSubroutine(List<Instruction> text) {
		returnFromSubroutineIfMoreThanOrEqualToZero(text);
		returnFromSubroutineIfLessThanZero(text);
	}
	
	protected void loadWheelerReturnAddress(List<Instruction> text, LowDataInfo returnAddressDataInfo) {
		clearAccumulator(text);
		text.add(new InstructionAdd(returnAddressDataInfo));
	}
	
	protected void clearAccumulator(List<Instruction> text) {
		clearDataInfo();
		text.add(new InstructionClearAccumulator());
	}
	
	protected void patchInstruction(List<Instruction> text, char opcode, Instruction placeholder) {
		text.add(new InstructionLeftShift(1));
		text.add(new InstructionAdd(constantDataInfo(opcode + "F")));
		text.add(new InstructionDeferredStoreAndClear(function, getSectionIndex(text), placeholder));
	}
	
	protected void patchInstruction(List<Instruction> text, int offset, char opcode, Instruction placeholder) {
		if (offset < 0) {
			throw new IllegalArgumentException(String.format("Encountered unexpected negative immediate EDSAC patch offset %s!", offset));
		}
		text.add(new InstructionLeftShift(1));
		text.add(new InstructionAdd(constantDataInfo(EdsacInt.of(opcode + "F").plus(EdsacInt.of(2L * offset)))));
		text.add(new InstructionDeferredStoreAndClear(function, getSectionIndex(text), placeholder));
	}
	
	protected void patchInstruction(List<Instruction> text, LowDataInfo info, char opcode, Instruction placeholder) {
		text.add(new InstructionAddStackTargetOffset(info));
		patchInstruction(text, opcode, placeholder);
	}
	
	protected Instruction dynamicInstruction(List<Instruction> text, char opcode) {
		Instruction placeholder = new InstructionPlaceholder();
		patchInstruction(text, opcode, placeholder);
		text.add(placeholder);
		return placeholder;
	}
	
	protected Instruction dynamicInstruction(List<Instruction> text, int offset, char opcode) {
		Instruction placeholder = new InstructionPlaceholder();
		patchInstruction(text, offset, opcode, placeholder);
		text.add(placeholder);
		return placeholder;
	}
	
	protected Instruction dynamicInstruction(List<Instruction> text, LowDataInfo info, char opcode) {
		Instruction placeholder = new InstructionPlaceholder();
		patchInstruction(text, info, opcode, placeholder);
		text.add(placeholder);
		return placeholder;
	}
	
	protected Instruction stackInstruction(List<Instruction> text, LowDataInfo info, char opcode, boolean preserveAccumulator) {
		if (preserveAccumulator) {
			storeData(text, scratchDataInfo(0), true);
		}
		loadData(text, basePointerDataInfo(), !preserveAccumulator);
		Instruction placeholder = new InstructionPlaceholder();
		patchInstruction(text, info, opcode, placeholder);
		if (preserveAccumulator) {
			loadData(text, scratchDataInfo(0), false);
		}
		text.add(placeholder);
		return placeholder;
	}
	
	protected void loadData(List<Instruction> text, LowDataInfo info, boolean clearAccumulator) {
		if (info.isStackData()) {
			loadData(text, basePointerDataInfo(), clearAccumulator);
			dynamicInstruction(text, info, EdsacOpcodes.ADD);
		}
		else {
			if (clearAccumulator) {
				clearAccumulator(text);
			}
			text.add(new InstructionAdd(info));
		}
	}
	
	protected void addData(List<Instruction> text, LowDataInfo info) {
		if (info.isStackData()) {
			stackInstruction(text, info, EdsacOpcodes.ADD, true);
		}
		else {
			text.add(new InstructionAdd(info));
		}
	}
	
	protected void subtractData(List<Instruction> text, LowDataInfo info) {
		if (info.isStackData()) {
			stackInstruction(text, info, EdsacOpcodes.SUBTRACT, true);
		}
		else {
			text.add(new InstructionSubtract(info));
		}
	}
	
	protected void loadMultiplierData(List<Instruction> text, LowDataInfo info) {
		if (info.isStackData()) {
			stackInstruction(text, info, EdsacOpcodes.LOAD_MULTIPLIER, false);
		}
		else {
			text.add(new InstructionLoadMultiplier(info));
		}
	}
	
	protected void addCollationData(List<Instruction> text, LowDataInfo info) {
		if (info.isStackData()) {
			stackInstruction(text, info, EdsacOpcodes.ADD_COLLATION, true);
		}
		else {
			text.add(new InstructionAddCollation(info));
		}
	}
	
	protected void addMultiplicationData(List<Instruction> text, LowDataInfo info) {
		if (info.isStackData()) {
			stackInstruction(text, info, EdsacOpcodes.ADD_MULTIPLICATION, true);
		}
		else {
			text.add(new InstructionAddMultiplication(info));
		}
	}
	
	protected void adjustStackPointer(List<Instruction> text, int delta) {
		if (delta == 0) {
			return;
		}
		
		loadData(text, stackPointerDataInfo(), true);
		if (delta > 0) {
			text.add(new InstructionAdd(constantDataInfo(delta)));
		}
		else {
			text.add(new InstructionSubtract(constantDataInfo(-delta)));
		}
		storeData(text, stackPointerDataInfo(), true);
	}
	
	protected void pushToOffset(List<Instruction> text, int offset) {
		storeData(text, scratchDataInfo(0), true);
		loadData(text, stackPointerDataInfo(), false);
		dynamicStore(text, offset, false, () -> loadData(text, scratchDataInfo(0), false));
	}
	
	protected void loadThen(List<Instruction> text, boolean reverse, DataId arg, IntConsumer consumer) {
		Function function = arg.getFunction();
		if (function != null) {
			LowDataInfo info = ensureFunctionInfo(function, arg);
			clearAccumulator(text);
			text.add(new InstructionAdd(info));
			consumer.accept(0);
		}
		else if (arg instanceof TransientDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add a transient load instruction! %s", arg));
		}
		else if (arg instanceof ValueDataId valueDataId) {
			List<EdsacInt> values = EdsacCode.raw(valueDataId.value);
			LowDataInfo baseInfo = ensureValueInfo(valueDataId);
			IntStream offsets = loadStoreOffsets(values.size(), reverse);
			offsets.forEach(x -> {
				clearAccumulator(text);
				text.add(new InstructionAdd(baseInfo.offsetBy(x)));
				consumer.accept(x);
			});
		}
		else if (arg.isAddress()) {
			DataId baseId = arg.addDereference(null);
			LowDataInfo baseInfo = getDataInfo(baseId, 0);
			clearAccumulator(text);
			if (baseInfo.isStackData()) {
				loadData(text, basePointerDataInfo(), false);
				text.add(new InstructionAddStackTargetOffset(baseInfo));
			}
			else {
				text.add(new InstructionAdd(ensureAddressInfo(arg)));
			}
			consumer.accept(0);
		}
		else if (arg.dereferenceLevel == 0) {
			IntStream offsets = loadStoreOffsets(arg.typeInfo.getSize(), reverse);
			LowDataInfo loadInfo = getDataInfo(arg, 0);
			offsets.forEach(x -> {
				if (loadInfo.isStackData()) {
					loadData(text, loadInfo.offsetBy(x), true);
				}
				else {
					clearAccumulator(text);
					text.add(new InstructionAdd(loadInfo.offsetBy(x)));
				}
				consumer.accept(x);
			});
		}
		else {
			DataId baseId = arg;
			for (int i = 0; i < arg.dereferenceLevel; ++i) {
				baseId = baseId.removeDereference(null);
			}
			
			LowDataInfo baseInfo = getDataInfo(baseId, 0);
			IntStream offsets = loadStoreOffsets(arg.typeInfo.getSize(), reverse);
			
			offsets.forEach(x -> {
				if (baseInfo.isStackData()) {
					loadData(text, baseInfo, true);
				}
				else {
					clearAccumulator(text);
					text.add(new InstructionAdd(baseInfo));
				}
				
				for (int i = 0; i < arg.dereferenceLevel - 1; ++i) {
					dynamicInstruction(text, EdsacOpcodes.ADD);
				}
				
				dynamicInstruction(text, x, EdsacOpcodes.ADD);
				consumer.accept(x);
			});
		}
	}
	
	protected void loadScalar(List<Instruction> text, DataId arg) {
		loadThen(text, false, arg, x -> {});
	}
	
	protected boolean tryStaticAssignment(DataId target, List<DataId> args, int[] offsets) {
		if (target instanceof TransientDataId || target instanceof ValueDataId || target.dereferenceLevel != 0) {
			return false;
		}
		
		List<Instruction> instructionList = Helpers.map(args, x -> {
			Function function = x.getFunction();
			if (function != null) {
				return new InstructionSubroutineAddressData(function);
			}
			else if (x instanceof ValueDataId valueDataId) {
				return new InstructionValueData(EdsacCode.raw(valueDataId.value));
			}
			else if (x.isAddress()) {
				return new InstructionAddressData(getDataInfo(x.addDereference(null), 0));
			}
			else {
				return null;
			}
		});
		
		if (instructionList.stream().anyMatch(x -> x == null)) {
			return false;
		}
		
		IntStream.range(0, offsets.length).forEach(x -> code.staticDataMap.put(getDataInfo(target, offsets[x]), instructionList.get(x)));
		return true;
	}
	
	protected void storeAt(List<Instruction> text, DataId target, boolean clearAccumulator, int offset) {
		if (target instanceof TransientDataId) {
			return;
		}
		else if (target instanceof ValueDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add an immediate store instruction! %s", target));
		}
		else if (target.isAddress()) {
			throw new IllegalArgumentException(String.format("Attempted to add an address store instruction! %s", target));
		}
		
		else if (target.dereferenceLevel == 0) {
			LowDataInfo storeInfo = getDataInfo(target, 0).offsetBy(offset);
			storeData(text, storeInfo, clearAccumulator);
		}
		else {
			DataId storeId = target;
			for (int i = 0; i < target.dereferenceLevel; ++i) {
				storeId = storeId.removeDereference(null);
			}
			
			LowDataInfo baseInfo = getDataInfo(storeId, 0);
			
			storeData(text, scratchDataInfo(0), true);
			if (baseInfo.isStackData()) {
				loadData(text, baseInfo, false);
			}
			else {
				text.add(new InstructionAdd(baseInfo));
			}
			
			for (int i = 0; i < target.dereferenceLevel - 1; ++i) {
				dynamicInstruction(text, EdsacOpcodes.ADD);
			}
			
			dynamicStore(text, offset, clearAccumulator, () -> loadData(text, scratchDataInfo(0), false));
		}
	}
	
	protected void storeScalar(List<Instruction> text, DataId target, boolean clearAccumulator) {
		storeAt(text, target, clearAccumulator, 0);
	}
	
	protected void dynamicStore(List<Instruction> text, boolean clear, Runnable load) {
		text.add(new InstructionLeftShift(1));
		text.add(new InstructionAdd(constantDataInfo(clear ? EdsacOpcodes.STORE_AND_CLEAR + "F" : EdsacOpcodes.STORE + "F")));
		Instruction placeholder = new InstructionPlaceholder();
		text.add(new InstructionDeferredStoreAndClear(function, getSectionIndex(text), placeholder));
		load.run();
		text.add(placeholder);
	}
	
	protected void dynamicStore(List<Instruction> text, int offset, boolean clear, Runnable load) {
		Instruction placeholder = new InstructionPlaceholder();
		patchInstruction(text, offset, clear ? EdsacOpcodes.STORE_AND_CLEAR : EdsacOpcodes.STORE, placeholder);
		load.run();
		text.add(placeholder);
	}
	
	protected void dynamicStore(List<Instruction> text, LowDataInfo info, boolean clear, Runnable load) {
		Instruction placeholder = new InstructionPlaceholder();
		patchInstruction(text, info, clear ? EdsacOpcodes.STORE_AND_CLEAR : EdsacOpcodes.STORE, placeholder);
		load.run();
		text.add(placeholder);
	}
	
	protected void storeData(List<Instruction> text, LowDataInfo info, boolean clearAccumulator) {
		if (info.isStackData()) {
			text.add(new InstructionStoreAndClear(scratchDataInfo(0)));
			loadData(text, basePointerDataInfo(), false);
			dynamicStore(text, info, clearAccumulator, () -> loadData(text, scratchDataInfo(0), false));
		}
		else {
			text.add(clearAccumulator ? new InstructionStoreAndClear(info) : new InstructionStore(info));
		}
	}
	
	protected void binaryOp(List<Instruction> text, BinaryActionType type, DataId arg) {
		if (arg instanceof ValueDataId valueDataId) {
			long value = EdsacCode.raw(valueDataId.value).get(0).toLong();
			switch (type) {
				case INT_LEFT_SHIFT_INT:
					text.add(new InstructionLeftShift(value));
					return;
				case INT_RIGHT_SHIFT_INT:
					text.add(new InstructionRightShift(value));
					return;
				default:
					break;
			}
		}
		
		LowDataInfo argDataInfo = ensureDataInfo(arg);
		switch (type) {
			case BOOL_EQUAL_TO_BOOL:
				addData(text, argDataInfo);
				storeData(text, scratchDataInfo(0), true);
				loadMultiplierData(text, scratchDataInfo(0));
				text.add(new InstructionAddCollation(constantDataInfo(1)));
				text.add(new InstructionSubtract(constantDataInfo(1)));
				break;
			case INT_EQUAL_TO_INT:
			case CHAR_EQUAL_TO_CHAR:
				binaryOp(text, BinaryActionType.INT_NOT_EQUAL_TO_INT, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case BOOL_NOT_EQUAL_TO_BOOL:
				binaryOp(text, BinaryActionType.BOOL_XOR_BOOL, arg);
				break;
			case INT_NOT_EQUAL_TO_INT:
			case CHAR_NOT_EQUAL_TO_CHAR:
				builtInSubroutine(text, Global.INT_NOT_EQUAL_TO_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			case BOOL_LESS_THAN_BOOL:
				storeData(text, tempDataInfo(0), true);
				addData(text, argDataInfo);
				subtractData(text, tempDataInfo(0));
				text.add(new InstructionRightShift(16));
				break;
			case INT_LESS_THAN_INT:
				builtInSubroutine(text, Global.INT_LESS_THAN_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			case NAT_LESS_THAN_NAT:
			case CHAR_LESS_THAN_CHAR:
				text.add(new InstructionAdd(constantDataInfo(EdsacInt.MIN_VALUE)));
				builtInSubroutine(text, Global.INT_LESS_THAN_INT, x -> x, true, () -> {
					loadScalar(text, arg);
					text.add(new InstructionAdd(constantDataInfo(EdsacInt.MIN_VALUE)));
				});
				break;
			case BOOL_LESS_OR_EQUAL_BOOL:
				binaryOp(text, BinaryActionType.BOOL_MORE_THAN_BOOL, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case INT_LESS_OR_EQUAL_INT:
				binaryOp(text, BinaryActionType.INT_MORE_THAN_INT, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case NAT_LESS_OR_EQUAL_NAT:
				binaryOp(text, BinaryActionType.NAT_MORE_THAN_NAT, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case CHAR_LESS_OR_EQUAL_CHAR:
				binaryOp(text, BinaryActionType.CHAR_MORE_THAN_CHAR, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case BOOL_MORE_THAN_BOOL:
				subtractData(text, argDataInfo);
				text.add(new InstructionRightShift(16));
				break;
			case INT_MORE_THAN_INT:
				builtInSubroutine(text, Global.INT_LESS_THAN_INT, x -> 1 - x, true, () -> loadScalar(text, arg));
				break;
			case NAT_MORE_THAN_NAT:
			case CHAR_MORE_THAN_CHAR:
				text.add(new InstructionAdd(constantDataInfo(EdsacInt.MIN_VALUE)));
				builtInSubroutine(text, Global.INT_LESS_THAN_INT, x -> 1 - x, true, () -> {
					loadScalar(text, arg);
					text.add(new InstructionAdd(constantDataInfo(EdsacInt.MIN_VALUE)));
				});
				break;
			case BOOL_MORE_OR_EQUAL_BOOL:
				subtractData(text, argDataInfo);
				text.add(new InstructionSubtract(constantDataInfo(1)));
				text.add(new InstructionRightShift(16));
				break;
			case INT_MORE_OR_EQUAL_INT:
				binaryOp(text, BinaryActionType.INT_LESS_THAN_INT, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case NAT_MORE_OR_EQUAL_NAT:
				binaryOp(text, BinaryActionType.NAT_LESS_THAN_NAT, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case CHAR_MORE_OR_EQUAL_CHAR:
				binaryOp(text, BinaryActionType.CHAR_LESS_THAN_CHAR, arg);
				unaryOp(text, UnaryActionType.NOT_BOOL, null);
				break;
			case INT_PLUS_INT:
			case CHAR_PLUS_CHAR:
				addData(text, argDataInfo);
				break;
			case BOOL_AND_BOOL:
			case INT_AND_INT:
			case CHAR_AND_CHAR:
				storeData(text, scratchDataInfo(0), true);
				loadMultiplierData(text, scratchDataInfo(0));
				addCollationData(text, argDataInfo);
				break;
			case BOOL_OR_BOOL:
				addData(text, argDataInfo);
				text.add(new InstructionRightShift(16));
				break;
			case INT_OR_INT:
			case CHAR_OR_CHAR:
				storeData(text, tempDataInfo(0), true);
				loadMultiplierData(text, tempDataInfo(0));
				addCollationData(text, argDataInfo);
				storeData(text, tempDataInfo(1), true);
				addData(text, tempDataInfo(0));
				addData(text, argDataInfo);
				subtractData(text, tempDataInfo(1));
				break;
			case BOOL_XOR_BOOL:
				addData(text, argDataInfo);
				storeData(text, scratchDataInfo(0), true);
				loadMultiplierData(text, scratchDataInfo(0));
				text.add(new InstructionAddCollation(constantDataInfo(1)));
				storeData(text, scratchDataInfo(0), true);
				subtractData(text, scratchDataInfo(0));
				break;
			case INT_XOR_INT:
			case CHAR_XOR_CHAR:
				storeData(text, tempDataInfo(0), true);
				loadMultiplierData(text, tempDataInfo(0));
				addCollationData(text, argDataInfo);
				text.add(new InstructionLeftShift(1));
				storeData(text, tempDataInfo(1), true);
				addData(text, tempDataInfo(0));
				addData(text, argDataInfo);
				subtractData(text, tempDataInfo(1));
				break;
			case INT_MINUS_INT:
			case CHAR_MINUS_CHAR:
				subtractData(text, argDataInfo);
				break;
			case INT_MULTIPLY_INT:
				storeData(text, scratchDataInfo(0), true);
				loadMultiplierData(text, scratchDataInfo(0));
				addMultiplicationData(text, argDataInfo);
				text.add(new InstructionLeftShift(16));
				break;
			// case INT_DIVIDE_INT:
			// TODO
			// break;
			// case NAT_DIVIDE_NAT:
			// TODO
			// break;
			// case INT_REMAINDER_INT:
			// TODO
			// break;
			// case NAT_REMAINDER_NAT:
			// TODO
			// break;
			case INT_LEFT_SHIFT_INT:
				builtInSubroutine(text, Global.INT_LEFT_SHIFT_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			case INT_RIGHT_SHIFT_INT:
				builtInSubroutine(text, Global.INT_RIGHT_SHIFT_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			case NAT_RIGHT_SHIFT_INT:
				builtInSubroutine(text, Global.NAT_RIGHT_SHIFT_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			case INT_LEFT_ROTATE_INT:
				builtInSubroutine(text, Global.INT_LEFT_ROTATE_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			case INT_RIGHT_ROTATE_INT:
				builtInSubroutine(text, Global.INT_RIGHT_ROTATE_INT, x -> x, true, () -> loadScalar(text, arg));
				break;
			default:
				throw new UnsupportedOperationException(String.format("EDSAC backend does not support binary op %s yet!", type));
		}
	}
	
	protected void unaryOp(List<Instruction> text, UnaryActionType type, DataId arg) {
		if (arg != null) {
			loadScalar(text, arg);
		}
		switch (type) {
			case MINUS_INT:
				storeData(text, scratchDataInfo(0), true);
				subtractData(text, scratchDataInfo(0));
				break;
			case NOT_BOOL:
				text.add(new InstructionAdd(constantDataInfo(1)));
				storeData(text, scratchDataInfo(0), true);
				subtractData(text, scratchDataInfo(0));
				break;
			case NOT_INT:
				storeData(text, scratchDataInfo(0), true);
				text.add(new InstructionSubtract(constantDataInfo(1)));
				subtractData(text, scratchDataInfo(0));
				break;
			case NOT_CHAR:
				storeData(text, scratchDataInfo(0), true);
				text.add(new InstructionAdd(constantDataInfo(EdsacInt.CHAR_MASK)));
				subtractData(text, scratchDataInfo(0));
				break;
			default:
				throw new IllegalArgumentException(String.format("Attempted to add unary op instruction of unknown type! %s %s", type, arg == null ? Global.TRANSIENT : arg.opErrorString()));
		}
	}
	
	protected void builtInSubroutine(List<Instruction> text, String name, IntUnaryOperator mapping, boolean clearAccumulator, Runnable... load) {
		Function builtInFunction = Main.generator.getBuiltInFunction(null, name);
		EdsacRoutine subroutine = code.getRoutine(builtInFunction);
		if (!subroutine.params.isEmpty()) {
			subroutine.storeScalar(text, subroutine.params.get(mapping.applyAsInt(0)).dataId(), clearAccumulator);
		}
		for (int i = 0; i < load.length; ++i) {
			load[i].run();
			subroutine.storeScalar(text, subroutine.params.get(mapping.applyAsInt(i + 1)).dataId(), clearAccumulator);
		}
		LowDataInfo returnAddressDataInfo = returnAddressDataInfo(new InstructionWheelerReturn());
		InstructionWheelerReturn iwr = (InstructionWheelerReturn) code.staticDataMap.get(returnAddressDataInfo);
		loadWheelerReturnAddress(text, returnAddressDataInfo);
		text.add(new InstructionWheelerJump(subroutine.function, iwr));
		intermediate.onRequiresNesting();
	}
}
