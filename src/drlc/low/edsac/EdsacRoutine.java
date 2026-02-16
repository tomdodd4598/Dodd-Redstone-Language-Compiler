package drlc.low.edsac;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.IntStream;

import drlc.Helpers.Pair;
import drlc.Main;
import drlc.intermediate.action.*;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.data.*;
import drlc.low.edsac.instruction.jump.*;
import drlc.low.edsac.instruction.wheeler.*;

public class EdsacRoutine extends LowRoutine<EdsacCode, EdsacRoutine, Instruction> {
	
	protected static final long WHEELER_STORE_DELTA = 0x18000L;
	
	protected Map<Integer, DataId> tempDataMap = new HashMap<>();
	
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
			List<Instruction> returnText = new ArrayList<>();
			sectionTextMap.put(-2, returnText);
			
			returnText.add(new InstructionPlaceholder());
			returnText.add(new InstructionPlaceholder());
			
			List<Instruction> patchText = new ArrayList<>();
			sectionTextMap.put(-1, patchText);
			
			patchText.add(new InstructionWheelerStore(function, -2, 0));
			patchText.add(new InstructionAdd(constantInfo(WHEELER_STORE_DELTA)));
			patchText.add(new InstructionWheelerStoreAndClear(function, -2, 1));
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
					if (callerFunction == null) {
						throw new UnsupportedOperationException("EDSAC backend does not support indirect calls yet!");
					}
					
					EdsacRoutine subroutine = code.getRoutine(callerFunction);
					if (subroutine == null) {
						throw new IllegalArgumentException(String.format("Encountered unknown subroutine \"%s\"!", callerFunction));
					}
					
					int targetSize = target.typeInfo.getSize(), argCount = args.size();
					if (targetSize > 1) {
						throw new UnsupportedOperationException("EDSAC backend does not support compound returns yet!");
					}
					
					for (int j = 0; j < argCount; ++j) {
						DataId paramId = subroutine.params.get(j).dataId();
						loadThen(text, false, args.get(j), x -> subroutine.storeAt(text, paramId, x));
					}
					
					if (targetSize > 1) {
						loadScalar(text, target.removeDereference(null));
						subroutine.storeScalar(text, subroutine.params.get(argCount).dataId());
					}
					
					LowDataInfo returnAddressInfo = returnAddressInfo();
					InstructionWheelerReturn iwr = (InstructionWheelerReturn) code.staticDataMap.get(returnAddressInfo);
					
					text.add(new InstructionStoreAndClear(tempDataInfo(0)));
					text.add(new InstructionAdd(returnAddressInfo));
					text.add(new InstructionWheelerJump(subroutine.function, iwr));
					
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
	
	public void prepareDataInfoRegeneration() {
		localSpanMap.clear();
		tempSpanMap.clear();
		
		generateParamDataInfo();
		
		if (isRootRoutine()) {
			regenerateDataInfoInternal();
		}
	}
	
	public void regenerateDataInfo() {
		if (!isRootRoutine()) {
			regenerateDataInfoInternal();
		}
		
		if (isStackRoutine()) {
			
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
					ia.address = getAddress(ia.dataInfo);
				}
				
				else if (instruction instanceof InstructionJump ij) {
					ij.address = code.textAddressMap.get(function) + sectionAddressMap.get(ij.section);
				}
				
				else if (instruction instanceof InstructionWheelerStore iws) {
					iws.address = textAddress(iws.function, iws.section, iws.offset);
				}
				
				else if (instruction instanceof InstructionWheelerStoreAndClear iwsac) {
					iwsac.address = textAddress(iwsac.function, iwsac.section, iwsac.offset);
				}
				
				else if (instruction instanceof InstructionWheelerJump iwj) {
					iwj.address = textAddress(iwj.function, -1, 0);
					iwj.iwr.setAddress(code.textAddressMap.get(function) + instructionAddress + instructionSize);
				}
				
				instructionAddress += instructionSize;
			}
		}
		
		if (isRootRoutine()) {
			for (Instruction data : code.staticDataMap.values()) {
				if (data instanceof InstructionAddressData iad) {
					iad.address = getAddress(iad.dataInfo);
				}
				else if (data instanceof InstructionSubroutineAddressData isad) {
					isad.setValue(code.textAddressMap.get(isad.function));
				}
			}
		}
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
	
	protected int textAddress(Function function, int section, int offset) {
		EdsacRoutine routine = code.getRoutine(function);
		return code.textAddressMap.get(function) + routine.sectionAddressMap.get(section) + offset;
	}
	
	protected static IntStream loadStoreOffsets(int size, boolean reverse) {
		IntStream offsets = IntStream.range(0, size);
		if (reverse) {
			offsets = offsets.map(x -> size - x - 1);
		}
		return offsets;
	}
	
	protected LowDataInfo tempDataInfo(int key) {
		DataId dataId = tempDataMap.get(key);
		if (dataId == null) {
			tempDataMap.put(key, dataId = function.scope.nextLocalDataId(intermediate, Main.generator.intTypeInfo));
		}
		return getDataInfo(dataId, 0);
	}
	
	protected LowDataInfo constantInfo(long value) {
		ValueDataId valueDataId = intValueDataId(value);
		LowDataInfo info = getDataInfo(valueDataId, 0);
		code.staticDataMap.putIfAbsent(info, new InstructionValueData(EdsacCode.raw(valueDataId.value)));
		return info;
	}
	
	protected LowDataInfo constantInfo(EdsacInt value) {
		return constantInfo(value.toLong());
	}
	
	protected LowDataInfo constantInfo(EdsacChar value) {
		return constantInfo(value.toInt());
	}
	
	protected LowDataInfo constantInfo(String str) {
		return constantInfo(EdsacInt.of(str));
	}
	
	protected LowDataInfo returnAddressInfo() {
		DataId dataId = function.scope.nextLocalDataId(intermediate, Main.generator.intTypeInfo);
		LowDataInfo info = getDataInfo(dataId, 0);
		code.staticDataMap.putIfAbsent(info, new InstructionWheelerReturn());
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
		LowDataInfo addressInfo = getDataInfo(arg.addDereference(null), 0);
		LowDataInfo info = getDataInfo(arg, 0);
		code.staticDataMap.putIfAbsent(info, new InstructionAddressData(addressInfo));
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
			throw new UnsupportedOperationException(String.format("EDSAC backend does not support dereferenced loads yet! %s", arg));
		}
	}
	
	protected void loadThen(List<Instruction> text, boolean reverse, DataId arg, IntConsumer consumer) {
		if (arg instanceof TransientDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add a transient load instruction! %s", arg));
		}
		else if (arg instanceof ValueDataId valueDataId) {
			List<EdsacInt> values = EdsacCode.raw(valueDataId.value);
			LowDataInfo baseInfo = ensureValueInfo(valueDataId);
			IntStream offsets = loadStoreOffsets(values.size(), reverse);
			offsets.forEach(x -> {
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionAdd(baseInfo.offsetBy(x)));
				consumer.accept(x);
			});
		}
		else if (arg.isAddress()) {
			text.add(new InstructionStoreAndClear(tempDataInfo(0)));
			text.add(new InstructionAdd(ensureAddressInfo(arg)));
			consumer.accept(0);
		}
		else if (arg.dereferenceLevel == 0) {
			IntStream offsets = loadStoreOffsets(arg.typeInfo.getSize(), reverse);
			LowDataInfo loadInfo = getDataInfo(arg, 0);
			offsets.forEach(x -> {
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionAdd(loadInfo.offsetBy(x)));
				consumer.accept(x);
			});
		}
		else {
			throw new UnsupportedOperationException(String.format("EDSAC backend does not support dereferenced loads yet! %s", arg));
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
		else if (target.dereferenceLevel == 0) {
			LowDataInfo storeInfo = getDataInfo(target, 0).offsetBy(offset);
			text.add(new InstructionStore(storeInfo));
		}
		else {
			throw new UnsupportedOperationException(String.format("EDSAC backend does not support dereferenced stores yet! %s", target));
		}
	}
	
	protected void storeScalar(List<Instruction> text, DataId target) {
		storeAt(text, target, 0);
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
				text.add(new InstructionAdd(argDataInfo));
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionLoadMultiplier(tempDataInfo(0)));
				text.add(new InstructionAddCollation(constantInfo(1)));
				text.add(new InstructionSubtract(constantInfo(1)));
				break;
			case INT_EQUAL_TO_INT:
				// TODO
				break;
			case CHAR_EQUAL_TO_CHAR:
				// TODO
				break;
			case BOOL_NOT_EQUAL_TO_BOOL:
				binaryOp(text, BinaryActionType.BOOL_XOR_BOOL, arg);
				break;
			case INT_NOT_EQUAL_TO_INT:
				// TODO
				break;
			case CHAR_NOT_EQUAL_TO_CHAR:
				// TODO
				break;
			case BOOL_LESS_THAN_BOOL:
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionAdd(argDataInfo));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				text.add(new InstructionRightShift(16));
				break;
			case INT_LESS_THAN_INT:
				// TODO
				break;
			case NAT_LESS_THAN_NAT:
				// TODO
				break;
			case CHAR_LESS_THAN_CHAR:
				// TODO
				break;
			case BOOL_LESS_OR_EQUAL_BOOL:
				binaryOp(text, BinaryActionType.BOOL_MORE_THAN_BOOL, arg);
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionSubtract(constantInfo(1)));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				break;
			case INT_LESS_OR_EQUAL_INT:
				// TODO
				break;
			case NAT_LESS_OR_EQUAL_NAT:
				// TODO
				break;
			case CHAR_LESS_OR_EQUAL_CHAR:
				// TODO
				break;
			case BOOL_MORE_THAN_BOOL:
				text.add(new InstructionSubtract(argDataInfo));
				text.add(new InstructionRightShift(16));
				break;
			case INT_MORE_THAN_INT:
				// TODO
				break;
			case NAT_MORE_THAN_NAT:
				// TODO
				break;
			case CHAR_MORE_THAN_CHAR:
				// TODO
				break;
			case BOOL_MORE_OR_EQUAL_BOOL:
				text.add(new InstructionSubtract(argDataInfo));
				text.add(new InstructionSubtract(constantInfo(1)));
				text.add(new InstructionRightShift(16));
				break;
			case INT_MORE_OR_EQUAL_INT:
				// TODO
				break;
			case NAT_MORE_OR_EQUAL_NAT:
				// TODO
				break;
			case CHAR_MORE_OR_EQUAL_CHAR:
				// TODO
				break;
			case INT_PLUS_INT:
			case CHAR_PLUS_CHAR:
				text.add(new InstructionAdd(argDataInfo));
				break;
			case BOOL_AND_BOOL:
			case INT_AND_INT:
			case CHAR_AND_CHAR:
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionLoadMultiplier(tempDataInfo(0)));
				text.add(new InstructionAddCollation(argDataInfo));
				break;
			case BOOL_OR_BOOL:
				text.add(new InstructionAdd(argDataInfo));
				text.add(new InstructionRightShift(16));
				break;
			case INT_OR_INT:
			case CHAR_OR_CHAR:
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionLoadMultiplier(tempDataInfo(0)));
				text.add(new InstructionAddCollation(argDataInfo));
				text.add(new InstructionStoreAndClear(tempDataInfo(1)));
				text.add(new InstructionAdd(tempDataInfo(0)));
				text.add(new InstructionAdd(argDataInfo));
				text.add(new InstructionSubtract(tempDataInfo(1)));
				break;
			case BOOL_XOR_BOOL:
				text.add(new InstructionAdd(argDataInfo));
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionLoadMultiplier(tempDataInfo(0)));
				text.add(new InstructionAddCollation(constantInfo(1)));
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				break;
			case INT_XOR_INT:
			case CHAR_XOR_CHAR:
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionLoadMultiplier(tempDataInfo(0)));
				text.add(new InstructionAddCollation(argDataInfo));
				text.add(new InstructionLeftShift(1));
				text.add(new InstructionStoreAndClear(tempDataInfo(1)));
				text.add(new InstructionAdd(tempDataInfo(0)));
				text.add(new InstructionAdd(argDataInfo));
				text.add(new InstructionSubtract(tempDataInfo(1)));
				break;
			case INT_MINUS_INT:
			case CHAR_MINUS_CHAR:
				text.add(new InstructionSubtract(argDataInfo));
				break;
			case INT_MULTIPLY_INT:
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionLoadMultiplier(tempDataInfo(0)));
				text.add(new InstructionAddMultiplication(argDataInfo));
				text.add(new InstructionLeftShift(16));
				break;
			case INT_DIVIDE_INT:
				// TODO
				break;
			case NAT_DIVIDE_NAT:
				// TODO
				break;
			case INT_REMAINDER_INT:
				// TODO
				break;
			case NAT_REMAINDER_NAT:
				// TODO
				break;
			case INT_LEFT_SHIFT_INT:
				// TODO
				break;
			case INT_RIGHT_SHIFT_INT:
				// TODO
				break;
			case NAT_RIGHT_SHIFT_INT:
				// TODO
				break;
			case INT_LEFT_ROTATE_INT:
				// TODO
				break;
			case INT_RIGHT_ROTATE_INT:
				// TODO
				break;
			default:
				throw new UnsupportedOperationException(String.format("EDSAC backend does not support binary op %s yet!", type));
		}
	}
	
	protected void unaryOp(List<Instruction> text, UnaryActionType type, DataId arg) {
		switch (type) {
			case MINUS_INT:
				loadScalar(text, arg);
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				break;
			case NOT_BOOL:
				loadScalar(text, arg);
				text.add(new InstructionAdd(constantInfo(1)));
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				break;
			case NOT_INT:
				loadScalar(text, arg);
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionSubtract(constantInfo(1)));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				break;
			case NOT_CHAR:
				loadScalar(text, arg);
				text.add(new InstructionStoreAndClear(tempDataInfo(0)));
				text.add(new InstructionAdd(constantInfo(EdsacInt.CHAR_MASK)));
				text.add(new InstructionSubtract(tempDataInfo(0)));
				break;
			default:
				throw new IllegalArgumentException(String.format("Attempted to add unary op instruction of unknown type! %s %s", type, arg.opErrorString()));
		}
	}
	
	protected void builtInSubroutine(List<Instruction> text, String name, Runnable... load) {
		Function builtInFunction = Main.generator.getBuiltInFunction(null, name);
		EdsacRoutine subroutine = code.getRoutine(builtInFunction);
		if (!subroutine.params.isEmpty()) {
			subroutine.storeScalar(text, subroutine.params.get(0).dataId());
		}
		for (int i = 0; i < load.length; ++i) {
			load[i].run();
			subroutine.storeScalar(text, subroutine.params.get(i + 1).dataId());
		}
		LowDataInfo returnAddressInfo = returnAddressInfo();
		InstructionWheelerReturn iwr = (InstructionWheelerReturn) code.staticDataMap.get(returnAddressInfo);
		text.add(new InstructionStoreAndClear(tempDataInfo(0)));
		text.add(new InstructionAdd(returnAddressInfo));
		text.add(new InstructionWheelerJump(subroutine.function, iwr));
		intermediate.onRequiresNesting();
	}
}
