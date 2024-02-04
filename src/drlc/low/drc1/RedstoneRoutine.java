package drlc.low.drc1;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.IntUnaryOperator;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.action.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.data.DataId.RawDataId;
import drlc.intermediate.component.type.TypeInfo;
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

public class RedstoneRoutine {
	
	public final RedstoneCode code;
	public final Routine intermediate;
	public final Function function;
	public final List<DeclaratorInfo> params;
	
	public final Map<Integer, List<Instruction>> textSectionMap = new TreeMap<>();
	
	private final Map<RawDataId, Pair<DataId, LowDataSpan>> dataSpanMap;
	private final Map<RawDataId, Pair<DataId, LowDataSpan>> tempSpanMap = new LinkedHashMap<>();
	
	public final Map<Integer, Short> sectionAddressMap = new LinkedHashMap<>();
	
	public final Map<LowDataSpan, LowAddressSlice> dataAddressMap;
	public final Map<LowDataSpan, LowAddressSlice> tempAddressMap = new LinkedHashMap<>();
	
	public RedstoneRoutine(RedstoneCode code, Routine intermediate) {
		this.code = code;
		this.intermediate = intermediate;
		function = intermediate.function;
		params = intermediate.getParams();
		if (isRootRoutine()) {
			dataSpanMap = code.rootSpanMap;
			dataAddressMap = code.rootAddressMap;
		}
		else {
			dataSpanMap = new LinkedHashMap<>();
			dataAddressMap = new LinkedHashMap<>();
		}
		mapParams();
	}
	
	public void mapParams() {
		for (DeclaratorInfo param : params) {
			DataId dataId = param.dataId();
			dataSpanMap.put(dataId.raw(), new Pair<>(dataId, nextParamSpan(param.getTypeInfo())));
		}
	}
	
	public void generateInstructions() {
		if (isRootRoutine()) {
			if (code.requiresStack) {
				List<Instruction> text = new ArrayList<>();
				textSectionMap.put(-1, text);
				text.add(new InstructionLoadBasePointer(RedstoneCode.MAX_ADDRESS));
				text.add(new InstructionLoadStackPointer(RedstoneCode.MAX_ADDRESS));
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
	}
	
	private void generateInstructionsInternal() {
		List<List<Action>> body = intermediate.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put(i, text);
			
			List<Action> actions = body.get(i);
			for (int j = 0; j < actions.size(); ++j) {
				Action action = actions.get(j);
				
				if (action instanceof AssignmentAction) {
					AssignmentAction aa = (AssignmentAction) action;
					// TODO load & store
					load(text, aa.arg);
					store(text, aa.target);
				}
				
				else if (action instanceof BinaryOpAction) {
					BinaryOpAction boa = (BinaryOpAction) action;
					load(text, boa.arg1);
					binaryOp(text, boa.type, boa.arg2);
					store(text, boa.target);
				}
				
				else if (action instanceof CallAction) {
					CallAction fca = (CallAction) action;
					DataId caller = fca.caller;
					List<DataId> args = fca.args;
					
					Function callerFunction = caller.getFunction();
					RedstoneRoutine subroutine = callerFunction == null ? null : code.getRoutine(callerFunction);
					boolean indirectCall = subroutine == null;
					boolean isStackRoutine = indirectCall || subroutine.isStackRoutine();
					
					int argCount = args.size();
					if (isStackRoutine) {
						for (int k = argCount - 1; k >= 0; --k) {
							// TODO load & push
							load(text, args.get(k));
							text.add(new InstructionPush());
						}
					}
					else {
						for (int k = 0; k < argCount; ++k) {
							// TODO load & subroutine store
							load(text, args.get(k));
							subroutine.store(text, subroutine.params.get(k).dataId());
						}
					}
					
					if (indirectCall) {
						load(text, caller);
					}
					else {
						text.add(new InstructionLoadCallAddressImmediate(callerFunction));
					}
					text.add(new InstructionCallSubroutine(indirectCall));
					
					if (isStackRoutine) {
						text.add(new InstructionAddToStackPointer((short) argCount));
					}
					
					// TODO store return value, RVO for size > 1 returns
					store(text, fca.target);
				}
				
				else if (action instanceof CompoundAssignmentAction) {
					// TODO compound load & store
				}
				
				else if (action instanceof ConditionalJumpAction) {
					ConditionalJumpAction cja = (ConditionalJumpAction) action;
					conditionalJump(text, cja.getTarget(), cja.jumpCondition);
				}
				
				else if (action instanceof ExitAction) {
					ExitAction eva = (ExitAction) action;
					load(text, eva.arg);
					text.add(new InstructionHalt());
				}
				
				else if (action instanceof JumpAction) {
					JumpAction ja = (JumpAction) action;
					jump(text, ja.getTarget());
				}
				
				else if (action instanceof NoOpAction) {
					text.add(new InstructionNoOp());
				}
				
				else if (action instanceof ReturnAction) {
					// TODO load return value, RVO for size > 1 returns
					ReturnAction rva = (ReturnAction) action;
					load(text, rva.arg);
					if (isStackRoutine()) {
						text.add(new InstructionJump(body.size()));
					}
					else {
						text.add(new InstructionReturnFromSubroutine());
					}
				}
				
				else if (action instanceof UnaryOpAction) {
					UnaryOpAction uoa = (UnaryOpAction) action;
					unaryOp(text, uoa.type, uoa.arg);
					store(text, uoa.target);
				}
				
				else {
					throw new IllegalArgumentException(String.format("Encountered unknown action \"%s\"!", action));
				}
			}
		}
	}
	
	public int getFinalTextSectionKey() {
		return intermediate.getBodyActionLists().size();
	}
	
	public void prepareDataIdRegeneration() {
		dataSpanMap.clear();
		tempSpanMap.clear();
		mapParams();
	}
	
	public void regenerateDataIds() {
		for (List<Instruction> section : textSectionMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof IInstructionAddress) {
					IInstructionAddress instructionAddress = (IInstructionAddress) instruction;
					section.set(i, instructionAddress.getDataReplacement(code));
				}
			}
		}
		
		if (isStackRoutine()) {
			short stackSize = (short) (spanMapSize(dataSpanMap) + spanMapSize(tempSpanMap) - Helpers.sumToInt(params, x -> x.getTypeInfo().getSize()));
			if (stackSize < 0) {
				throw new IllegalArgumentException(String.format("Stack-based subroutine \"%s\" has unexpected stack size %s!", function, stackSize));
			}
			
			// Post-optimization!
			
			boolean flag = true;
			while (flag) {
				flag = false;
				for (Entry<Integer, List<Instruction>> entry : textSectionMap.entrySet()) {
					List<Instruction> section = entry.getValue();
					for (int i = 0; i < section.size(); ++i) {
						Instruction instruction = section.get(i);
						boolean asp = instruction instanceof InstructionAddToStackPointer;
						boolean ssp = instruction instanceof InstructionSubtractFromStackPointer;
						if (asp || ssp) {
							if (asp) {
								InstructionAddToStackPointer iatsp = (InstructionAddToStackPointer) instruction;
								if (iatsp.value == null) {
									flag = true;
									iatsp.value = stackSize;
								}
								if (iatsp.value == 0) {
									flag = true;
									section.remove(i);
								}
							}
							else if (ssp) {
								InstructionSubtractFromStackPointer isfsp = (InstructionSubtractFromStackPointer) instruction;
								if (!isfsp.initialized) {
									flag = isfsp.initialized = true;
									isfsp.value = stackSize;
								}
								if (isfsp.value == 0) {
									flag = true;
									section.remove(i);
								}
							}
							
							flag |= RedstoneOptimization.compressWithNextInstruction(textSectionMap, entry.getKey(), i, true);
						}
					}
				}
			}
		}
	}
	
	public void generateTextAddresses() {
		short sectionAddressOffset = 0;
		for (Entry<Integer, List<Instruction>> entry : textSectionMap.entrySet()) {
			sectionAddressMap.put(entry.getKey(), sectionAddressOffset);
			sectionAddressOffset += entry.getValue().size();
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
	
	private static int addAddressEntry(Map<LowDataSpan, LowAddressSlice> addressMap, LowDataSpan span, int addressOffset, IntUnaryOperator function) {
		int size = span.size;
		int start = Math.min(function.applyAsInt(addressOffset), function.applyAsInt(addressOffset + size));
		addressMap.put(span, new LowAddressSlice(start, size));
		return size;
	}
	
	public void finalizeInstructions() {
		for (Entry<Integer, List<Instruction>> entry : textSectionMap.entrySet()) {
			short instructionAddress = sectionAddressMap.get(entry.getKey());
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionAddress) {
					InstructionAddress ia = (InstructionAddress) instruction;
					ia.address = getAddress(ia.info);
				}
				
				else if (instruction instanceof InstructionAddressOffset) {
					InstructionAddressOffset iao = (InstructionAddressOffset) instruction;
					iao.offset = getAddress(iao.info);
				}
				
				else if (instruction instanceof InstructionCallSubroutine) {
					InstructionCallSubroutine ics = (InstructionCallSubroutine) instruction;
					ics.returnAddress = (short) (code.textAddressMap.get(function) + instructionAddress + 1);
					
					if (!ics.indirectCall && !(section.get(i - 1) instanceof InstructionLoadCallAddressImmediate)) {
						throw new IllegalArgumentException(String.format("Found unexpected direct subroutine call instruction \"%s\" not following call address load instruction as required!", instruction));
					}
				}
				
				else if (instruction instanceof InstructionLoadCallAddressImmediate) {
					InstructionLoadCallAddressImmediate ilcai = (InstructionLoadCallAddressImmediate) instruction;
					ilcai.setValue(code.textAddressMap.get(ilcai.function));
				}
				
				else if (instruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) instruction;
					ij.address = (short) (code.textAddressMap.get(function) + sectionAddressMap.get(ij.section));
				}
				
				++instructionAddress;
			}
		}
	}
	
	public void onRequiresNesting() {
		intermediate.onRequiresNesting();
	}
	
	public void onRequiresStack() {
		intermediate.onRequiresStack();
	}
	
	public boolean isStackRoutine() {
		return intermediate.isStackRoutine();
	}
	
	public boolean isRootRoutine() {
		return intermediate.equals(Main.rootRoutine);
	}
	
	private Pair<DataId, LowDataSpan> dataSpanPair(DataId dataId) {
		return getSpanPair(dataSpanMap, dataId);
	}
	
	private Pair<DataId, LowDataSpan> tempSpanPair(DataId dataId) {
		return getSpanPair(tempSpanMap, dataId);
	}
	
	private Pair<DataId, LowDataSpan> getSpanPair(Map<RawDataId, Pair<DataId, LowDataSpan>> spanMap, DataId dataId) {
		RawDataId raw = dataId.raw();
		if (spanMap.containsKey(raw)) {
			return spanMap.get(raw);
		}
		else {
			Pair<DataId, LowDataSpan> pair = new Pair<>(dataId, nextSpan(spanMap, dataId.typeInfo));
			spanMap.put(raw, pair);
			return pair;
		}
	}
	
	private LowDataSpan nextSpan(Map<RawDataId, Pair<DataId, LowDataSpan>> spanMap, TypeInfo typeInfo) {
		int id = 0, size = typeInfo.getSize();
		outer: while (true) {
			LowDataSpan span = new LowDataSpan(function, id, size);
			for (Pair<DataId, LowDataSpan> pair : spanMap.values()) {
				if (pair.right.equals(span)) {
					++id;
					continue outer;
				}
			}
			return span;
		}
	}
	
	private LowDataSpan nextParamSpan(TypeInfo typeInfo) {
		if (isStackRoutine()) {
			int id = -1, size = typeInfo.getSize();
			outer: while (true) {
				LowDataSpan span = new LowDataSpan(function, id, size);
				for (Pair<DataId, LowDataSpan> pair : dataSpanMap.values()) {
					if (pair.right.equals(span)) {
						--id;
						continue outer;
					}
				}
				return span;
			}
		}
		else {
			return nextSpan(dataSpanMap, typeInfo);
		}
	}
	
	private static int spanMapSize(Map<RawDataId, Pair<DataId, LowDataSpan>> spanMap) {
		return Helpers.sumToInt(spanMap.keySet(), x -> x.internal.typeInfo.getSize());
	}
	
	private boolean isStackData(LowDataInfo info) {
		return !info.isStaticData() && isStackRoutine();
	}
	
	// Data Info
	
	public LowDataInfo dataInfo(DataId arg, int extraOffset) {
		if (arg instanceof RegDataId) {
			Pair<DataId, LowDataSpan> pair = tempSpanPair(arg);
			return new LowDataInfo(function, arg.dereferenceLevel == 0 ? arg : pair.left, LowDataType.TEMP, pair.right, extraOffset);
		}
		else {
			RawDataId raw = arg.raw();
			if (code.rootSpanMap.containsKey(raw)) {
				Pair<DataId, LowDataSpan> pair = code.rootSpanMap.get(raw);
				return new LowDataInfo(Main.rootRoutine.function, arg.dereferenceLevel == 0 ? arg : pair.left, LowDataType.STATIC, pair.right, extraOffset);
			}
			else {
				Pair<DataId, LowDataSpan> pair = dataSpanPair(arg);
				return new LowDataInfo(function, arg.dereferenceLevel == 0 ? arg : pair.left, isStackRoutine() ? LowDataType.STACK : LowDataType.STATIC, pair.right, extraOffset);
			}
		}
	}
	
	private short getAddress(LowDataInfo info) {
		RedstoneRoutine routine = code.getRoutine(info.function);
		switch (info.type) {
			case TEMP:
				return dataAddress(routine.tempAddressMap, info);
			case STATIC:
				return dataAddress(code.rootAddressMap, info);
			case STACK:
				return dataAddress(routine.dataAddressMap, info);
			default:
				throw new IllegalArgumentException(String.format("Encountered unknown type for data info \"%s\"!", info));
		}
	}
	
	private static short dataAddress(Map<LowDataSpan, LowAddressSlice> addressMap, LowDataInfo info) {
		return (short) (addressMap.get(info.span).start + info.argId.getOffset() + info.extraOffset);
	}
	
	// Instructions
	
	private void store(List<Instruction> text, DataId target) {
		if (target instanceof TransientDataId) {
			return;
		}
		else if (target instanceof ValueDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add an immediate store instruction! %s", target));
		}
		else {
			if (target.isAddress()) {
				throw new IllegalArgumentException(String.format("Attempted to add an address store instruction! %s", target));
			}
			else {
				int size = target.typeInfo.getSize();
				LowDataInfo targetInfo = dataInfo(target, 0);
				boolean isStackData = isStackData(targetInfo);
				if (target.dereferenceLevel == 0) {
					for (int i = 0; i < size; ++i) {
						LowDataInfo offsetInfo = targetInfo.offset(i);
						text.add(isStackData ? new InstructionStoreOffset(offsetInfo) : new InstructionStore(offsetInfo));
					}
				}
				else {
					for (int i = 0; i < size; ++i) {
						text.add(isStackData ? new InstructionLoadBOffset(targetInfo) : new InstructionLoadB(targetInfo));
						for (int j = 0; j < target.dereferenceLevel - 1; ++j) {
							text.add(new InstructionDereferenceB());
						}
						text.add(new InstructionAddBImmediate((short) i));
						text.add(new InstructionStoreAToBAddress());
					}
				}
			}
		}
	}
	
	private void load(List<Instruction> text, DataId arg) {
		Function function = arg.getFunction();
		if (function != null) {
			text.add(new InstructionLoadCallAddressImmediate(function));
			// TODO: action
		}
		else if (arg instanceof TransientDataId) {
			throw new IllegalArgumentException(String.format("Attempted to add a transient load instruction! %s", arg));
		}
		else if (arg instanceof ValueDataId) {
			List<Short> valueList = RedstoneCode.raw(((ValueDataId) arg).value);
			for (short value : valueList) {
				if (RedstoneCode.isLongImmediate(value)) {
					Instruction illi = new InstructionLoadLongImmediate(value);
					text.add(illi);
					text.add(illi.succeedingData());
				}
				else {
					text.add(new InstructionLoadImmediate(value));
				}
				// TODO: action
			}
		}
		else {
			if (arg.isAddress()) {
				LowDataInfo argInfo = dataInfo(arg.addDereference(null), 0);
				text.add(isStackData(argInfo) ? new InstructionLoadAddressImmediateOffset(argInfo) : new InstructionLoadAddressImmediate(argInfo));
				// TODO: action
			}
			else {
				int size = arg.typeInfo.getSize();
				LowDataInfo argInfo = dataInfo(arg, 0);
				boolean isStackData = isStackData(argInfo);
				if (arg.dereferenceLevel == 0) {
					for (int i = 0; i < size; ++i) {
						LowDataInfo offsetInfo = argInfo.offset(i);
						text.add(isStackData ? new InstructionLoadAOffset(offsetInfo) : new InstructionLoadA(offsetInfo));
						// TODO: action
					}
				}
				else {
					for (int i = 0; i < size; ++i) {
						text.add(isStackData ? new InstructionLoadAOffset(argInfo) : new InstructionLoadA(argInfo));
						for (int j = 0; j < arg.dereferenceLevel - 1; ++j) {
							text.add(new InstructionDereferenceA());
						}
						text.add(new InstructionAddImmediate((short) i));
						text.add(new InstructionDereferenceA());
						// TODO: action
					}
				}
			}
		}
	}
	
	private void binaryOp(List<Instruction> text, BinaryActionType type, DataId arg) {
		if (arg instanceof ValueDataId) {
			short value = RedstoneCode.raw(((ValueDataId) arg).value).get(0);
			if (RedstoneCode.isLongImmediate(value)) {
				Instruction li;
				switch (type) {
					case BOOL_EQUAL_TO_BOOL:
					case INT_EQUAL_TO_INT:
					case CHAR_EQUAL_TO_CHAR:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsZero());
						break;
					case BOOL_NOT_EQUAL_TO_BOOL:
					case INT_NOT_EQUAL_TO_INT:
					case CHAR_NOT_EQUAL_TO_CHAR:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsNotZero());
						break;
					case BOOL_LESS_THAN_BOOL:
					case INT_LESS_THAN_INT:
					case CHAR_LESS_THAN_CHAR:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsLessThanZero());
						break;
					case BOOL_LESS_OR_EQUAL_BOOL:
					case INT_LESS_OR_EQUAL_INT:
					case CHAR_LESS_OR_EQUAL_CHAR:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case BOOL_MORE_THAN_BOOL:
					case INT_MORE_THAN_INT:
					case CHAR_MORE_THAN_CHAR:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case BOOL_MORE_OR_EQUAL_BOOL:
					case INT_MORE_OR_EQUAL_INT:
					case CHAR_MORE_OR_EQUAL_CHAR:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_PLUS_INT:
					case CHAR_PLUS_CHAR:
						text.add(li = new InstructionAddLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case BOOL_AND_BOOL:
					case INT_AND_INT:
					case CHAR_AND_CHAR:
						text.add(li = new InstructionAndLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case BOOL_OR_BOOL:
					case INT_OR_INT:
					case CHAR_OR_CHAR:
						text.add(li = new InstructionOrLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case BOOL_XOR_BOOL:
					case INT_XOR_INT:
					case CHAR_XOR_CHAR:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case INT_MINUS_INT:
					case CHAR_MINUS_CHAR:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case INT_MULTIPLY_INT:
						text.add(li = new InstructionMultiplyLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case INT_DIVIDE_INT:
						text.add(li = new InstructionDivideLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case INT_REMAINDER_INT:
						text.add(li = new InstructionRemainderLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case INT_LEFT_SHIFT_INT:
						text.add(new InstructionLeftShiftImmediate(RedstoneCode.lowBits(value)));
						break;
					case INT_RIGHT_SHIFT_INT:
						text.add(new InstructionRightShiftImmediate(RedstoneCode.lowBits(value)));
						break;
					case NAT_RIGHT_SHIFT_INT:
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
						break;
					case INT_LEFT_ROTATE_INT:
						// (x << y) | (x >>> (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
						break;
					case INT_RIGHT_ROTATE_INT:
						// (x >>> y) | (x << (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add long immediate binary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
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
					case INT_LESS_THAN_INT:
					case CHAR_LESS_THAN_CHAR:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case BOOL_LESS_OR_EQUAL_BOOL:
					case INT_LESS_OR_EQUAL_INT:
					case CHAR_LESS_OR_EQUAL_CHAR:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case BOOL_MORE_THAN_BOOL:
					case INT_MORE_THAN_INT:
					case CHAR_MORE_THAN_CHAR:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case BOOL_MORE_OR_EQUAL_BOOL:
					case INT_MORE_OR_EQUAL_INT:
					case CHAR_MORE_OR_EQUAL_CHAR:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_PLUS_INT:
					case CHAR_PLUS_CHAR:
						text.add(new InstructionAddImmediate(value));
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
					case CHAR_MINUS_CHAR:
						text.add(new InstructionSubtractImmediate(value));
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
						text.add(new InstructionLeftShiftImmediate(value));
						break;
					case INT_RIGHT_SHIFT_INT:
						text.add(new InstructionRightShiftImmediate(value));
						break;
					case NAT_RIGHT_SHIFT_INT:
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadImmediate(value));
						break;
					case INT_LEFT_ROTATE_INT:
						// (x << y) | (x >>> (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadImmediate(value));
						break;
					case INT_RIGHT_ROTATE_INT:
						// (x >>> y) | (x << (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadImmediate(value));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add immediate binary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
		else {
			LowDataInfo argInfo = dataInfo(arg, 0);
			if (isStackData(argInfo)) {
				switch (type) {
					case BOOL_EQUAL_TO_BOOL:
					case INT_EQUAL_TO_INT:
					case CHAR_EQUAL_TO_CHAR:
						text.add(new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case BOOL_NOT_EQUAL_TO_BOOL:
					case INT_NOT_EQUAL_TO_INT:
					case CHAR_NOT_EQUAL_TO_CHAR:
						text.add(new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case BOOL_LESS_THAN_BOOL:
					case INT_LESS_THAN_INT:
					case CHAR_LESS_THAN_CHAR:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case BOOL_LESS_OR_EQUAL_BOOL:
					case INT_LESS_OR_EQUAL_INT:
					case CHAR_LESS_OR_EQUAL_CHAR:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case BOOL_MORE_THAN_BOOL:
					case INT_MORE_THAN_INT:
					case CHAR_MORE_THAN_CHAR:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case BOOL_MORE_OR_EQUAL_BOOL:
					case INT_MORE_OR_EQUAL_INT:
					case CHAR_MORE_OR_EQUAL_CHAR:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_PLUS_INT:
					case CHAR_PLUS_CHAR:
						text.add(new InstructionAddOffset(argInfo));
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
					case CHAR_MINUS_CHAR:
						text.add(new InstructionSubtractOffset(argInfo));
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
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadAOffset(argInfo));
						break;
					case INT_LEFT_ROTATE_INT:
						// (x << y) | (x >>> (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadAOffset(argInfo));
						break;
					case INT_RIGHT_ROTATE_INT:
						// (x >>> y) | (x << (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadAOffset(argInfo));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset binary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
					case BOOL_EQUAL_TO_BOOL:
					case INT_EQUAL_TO_INT:
					case CHAR_EQUAL_TO_CHAR:
						text.add(new InstructionXor(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case BOOL_NOT_EQUAL_TO_BOOL:
					case INT_NOT_EQUAL_TO_INT:
					case CHAR_NOT_EQUAL_TO_CHAR:
						text.add(new InstructionXor(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case BOOL_LESS_THAN_BOOL:
					case INT_LESS_THAN_INT:
					case CHAR_LESS_THAN_CHAR:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case BOOL_LESS_OR_EQUAL_BOOL:
					case INT_LESS_OR_EQUAL_INT:
					case CHAR_LESS_OR_EQUAL_CHAR:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case BOOL_MORE_THAN_BOOL:
					case INT_MORE_THAN_INT:
					case CHAR_MORE_THAN_CHAR:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case BOOL_MORE_OR_EQUAL_BOOL:
					case INT_MORE_OR_EQUAL_INT:
					case CHAR_MORE_OR_EQUAL_CHAR:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case INT_PLUS_INT:
					case CHAR_PLUS_CHAR:
						text.add(new InstructionAdd(argInfo));
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
					case CHAR_MINUS_CHAR:
						text.add(new InstructionSubtract(argInfo));
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
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadA(argInfo));
						break;
					case INT_LEFT_ROTATE_INT:
						// (x << y) | (x >>> (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadA(argInfo));
						break;
					case INT_RIGHT_ROTATE_INT:
						// (x >>> y) | (x << (-y))
						binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadA(argInfo));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address binary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
	}
	
	private void binaryOpBuiltInSubroutine(List<Instruction> text, String name, Instruction loadArg) {
		Function builtInFunction = Main.rootScope.getFunction(null, name, false);
		RedstoneRoutine subroutine = code.getRoutine(builtInFunction);
		subroutine.store(text, subroutine.params.get(0).dataId());
		text.add(loadArg);
		subroutine.store(text, subroutine.params.get(1).dataId());
		text.add(new InstructionLoadCallAddressImmediate(builtInFunction));
		text.add(new InstructionCallSubroutine(false));
		onRequiresNesting();
	}
	
	private void conditionalJump(List<Instruction> text, int section, boolean jumpCondition) {
		if (jumpCondition) {
			text.add(new InstructionConditionalJumpIfNotZero(section));
		}
		else {
			text.add(new InstructionConditionalJumpIfZero(section));
		}
	}
	
	private void jump(List<Instruction> text, int section) {
		text.add(new InstructionJump(section));
	}
	
	private void unaryOp(List<Instruction> text, UnaryActionType type, DataId arg) {
		if (arg instanceof ValueDataId) {
			short value = RedstoneCode.raw(((ValueDataId) arg).value).get(0);
			Instruction li;
			if (RedstoneCode.isLongImmediate(value)) {
				switch (type) {
					case MINUS_INT:
						short minus = (short) -value;
						if (RedstoneCode.isLongImmediate(minus)) {
							text.add(li = new InstructionLoadLongImmediate(minus));
							text.add(li.succeedingData());
						}
						else {
							text.add(new InstructionLoadImmediate(minus));
						}
						break;
					case NOT_BOOL:
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsZero());
						break;
					case NOT_INT:
						short not = (short) ~value;
						if (RedstoneCode.isLongImmediate(not)) {
							text.add(li = new InstructionLoadLongImmediate(not));
							text.add(li.succeedingData());
						}
						else {
							text.add(new InstructionLoadImmediate(not));
						}
						break;
					case NOT_CHAR:
						text.add(new InstructionLoadImmediate(RedstoneCode.lowBits((short) ~value)));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add long immediate unary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
					case MINUS_INT:
						short minus = (short) -value;
						if (RedstoneCode.isLongImmediate(minus)) {
							text.add(li = new InstructionLoadLongImmediate(minus));
							text.add(li.succeedingData());
						}
						else {
							text.add(new InstructionLoadImmediate(minus));
						}
						break;
					case NOT_BOOL:
						text.add(new InstructionLoadImmediate(value));
						text.add(new InstructionSetIsZero());
						break;
					case NOT_INT:
						text.add(new InstructionNotImmediate(value));
						break;
					case NOT_CHAR:
						text.add(new InstructionLoadImmediate(RedstoneCode.lowBits((short) ~value)));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add immediate unary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
		else {
			LowDataInfo argInfo = dataInfo(arg, 0);
			if (isStackData(argInfo)) {
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
						text.add(new InstructionAndImmediate((short) 0x7F));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset unary op instruction of unknown type! %s %s", type, arg));
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
						text.add(new InstructionAndImmediate((short) 0x7F));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address unary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
	}
}
