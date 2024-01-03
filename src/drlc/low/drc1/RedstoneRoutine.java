package drlc.low.drc1;

import java.util.*;
import java.util.Map.Entry;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.routine.*;
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
	public final Routine intermediateRoutine;
	public final String name;
	public final List<DeclaratorInfo> params;
	
	public final Map<Short, List<Instruction>> textSectionMap = new TreeMap<>();
	
	public final Map<DataId, Long> dataIdMap;
	public int tempSize = 0;
	public final Map<DataId, Long> tempIdMap = new LinkedHashMap<>();
	public long extraTempRegId = -1;
	private boolean dataIdRegeneration = false;
	
	public final Map<Short, Short> sectionAddressMap = new HashMap<>();
	public final Map<RedstoneAddressKey, Short> dataAddressMap;
	public final Map<RedstoneAddressKey, Short> tempAddressMap = new HashMap<>();
	
	public RedstoneRoutine(RedstoneCode code, String name, RoutineCallType type, List<DeclaratorInfo> params) {
		this.code = code;
		intermediateRoutine = null;
		this.name = name;
		this.params = params;
		dataIdMap = new LinkedHashMap<>();
		dataAddressMap = new HashMap<>();
	}
	
	public RedstoneRoutine(RedstoneCode code, Routine intermediateRoutine) {
		this.code = code;
		this.intermediateRoutine = intermediateRoutine;
		name = intermediateRoutine.name;
		params = intermediateRoutine.getParams();
		if (isRootRoutine()) {
			dataIdMap = code.rootIdMap;
			dataAddressMap = code.rootAddressMap;
		}
		else {
			dataIdMap = new LinkedHashMap<>();
			dataAddressMap = new HashMap<>();
		}
		mapParams();
	}
	
	public void mapParams() {
		for (DeclaratorInfo param : params) {
			dataIdMap.put(param.dataId(), nextParamId());
		}
	}
	
	public void generateInstructions() {
		if (isRootRoutine()) {
			if (code.requiresStack) {
				List<Instruction> text = new ArrayList<>();
				textSectionMap.put((short) -1, text);
				text.add(new InstructionLoadBasePointer((short) (RedstoneCode.MAX_ADDRESS - params.length)));
				text.add(new InstructionLoadStackPointer((short) (RedstoneCode.MAX_ADDRESS - params.length)));
			}
		}
		else if (isStackRoutine()) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put((short) -1, text);
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
	
	protected void generateInstructionsInternal() {
		List<List<Action>> body = intermediateRoutine.getBodyActionLists();
		for (int i = 0; i < body.size(); ++i) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put((short) i, text);
			
			if (i == 0 && !isRootRoutine()) {
				for (DeclaratorInfo param : params) {
					DataId paramId = param.dataId();
					if (paramId.dereferenceLevel > 0) {
						declare(text, paramId, false);
					}
				}
			}
			
			List<Action> actions = body.get(i);
			for (int j = 0; j < actions.size(); ++j) {
				Action action = actions.get(j);
				
				if (action instanceof AssignmentAction) {
					AssignmentAction aa = (AssignmentAction) action;
					load(text, aa.arg);
					store(text, aa.target, true);
				}
				
				else if (action instanceof BasicAction) {
					if (action instanceof ExitAction) {
						load(text, Helpers.immediateDataId(0L));
						text.add(new InstructionHalt());
					}
					else {
						throw new IllegalArgumentException(String.format("Encountered unknown basic action \"%s\"!", action));
					}
				}
				
				else if (action instanceof BinaryOpAction) {
					BinaryOpAction boa = (BinaryOpAction) action;
					load(text, boa.arg1);
					binaryOp(text, boa.opType, boa.arg2);
					store(text, boa.target, true);
				}
				
				else if (action instanceof ConditionalJumpAction) {
					ConditionalJumpAction cja = (ConditionalJumpAction) action;
					conditionalJump(text, cja.target, cja.jumpCondition);
				}
				
				else if (action instanceof DeclarationAction) {
					DeclarationAction da = (DeclarationAction) action;
					declare(text, da.target, false);
				}
				
				else if (action instanceof ExitAction) {
					ExitAction eva = (ExitAction) action;
					load(text, eva.arg);
					text.add(new InstructionHalt());
				}
				
				else if (action instanceof InitializationAction) {
					InitializationAction ia = (InitializationAction) action;
					load(text, ia.arg);
					declare(text, ia.target, true);
				}
				
				else if (action instanceof JumpAction) {
					JumpAction ja = (JumpAction) action;
					jump(text, ja.target);
				}
				
				else if (action instanceof NoOpAction) {
					text.add(new InstructionNoOp());
				}
				
				else if (action instanceof PlaceholderAction) {
					PlaceholderAction pa = (PlaceholderAction) action;
					throw new IllegalArgumentException(String.format("Placeholder action \"%s\" not correctly substituted!", pa.type));
				}
				
				else if (action instanceof ReturnAction) {
					if (isRootRoutine()) {
						throw new IllegalArgumentException(String.format("Root routine can not return! Use an exit statement!"));
					}
					else if (isStackRoutine()) {
						text.add(new InstructionJump(body.size()));
					}
					else {
						text.add(new InstructionReturnFromSubroutine());
					}
				}
				
				else if (action instanceof ReturnAction) {
					if (isRootRoutine()) {
						throw new IllegalArgumentException(String.format("Root routine can not return a value! Use an exit value statement!"));
					}
					else {
						ReturnAction rva = (ReturnAction) action;
						load(text, rva.arg);
						if (isStackRoutine()) {
							text.add(new InstructionJump(body.size()));
						}
						else {
							text.add(new InstructionReturnFromSubroutine());
						}
					}
				}
				
				else if (action instanceof FunctionCallAction) {
					if (action instanceof BuiltInFunctionCallAction) {
						builtInFunctionCall(text, (BuiltInFunctionCallAction) action);
					}
					else {
						FunctionCallAction fca = (FunctionCallAction) action;
						DataId function = fca.function;
						List<DataId> args = fca.args;
						
						RedstoneRoutine subroutine = code.getRoutine(function.name);
						boolean indirectCall = subroutine == null;
						boolean isStackRoutine = indirectCall || subroutine.isStackRoutine();
						
						int argCount = args.size();
						if (isStackRoutine) {
							for (int k = argCount - 1; k >= 0; --k) {
								load(text, args.get(k));
								text.add(new InstructionPush());
							}
						}
						else {
							for (int k = 0; k < argCount; ++k) {
								load(text, args.get(k));
								subroutine.store(text, subroutine.params[k].dataId(), false);
							}
						}
						
						if (indirectCall) {
							load(text, function);
						}
						else {
							text.add(new InstructionLoadCallAddressImmediate(function.name));
						}
						text.add(new InstructionCallSubroutine(indirectCall));
						
						if (isStackRoutine) {
							text.add(new InstructionAddToStackPointer((short) argCount));
						}
						
						store(text, fca.target, true);
					}
				}
				
				else if (action instanceof DereferenceAction) {
					DereferenceAction da = (DereferenceAction) action;
					dereference(text, 1, da.arg);
					store(text, da.target, true);
				}
				
				else if (action instanceof UnaryOpAction) {
					UnaryOpAction uoa = (UnaryOpAction) action;
					unaryOp(text, uoa.opType, uoa.arg);
					store(text, uoa.target, true);
				}
				
				else {
					throw new IllegalArgumentException(String.format("Encountered unknown action \"%s\"!", action));
				}
			}
		}
	}
	
	public short getFinalTextSectionKey() {
		return (short) intermediateRoutine.getBodyActionLists().size();
	}
	
	public void prepareDataIdRegeneration() {
		dataIdMap.clear();
		tempSize = 0;
		tempIdMap.clear();
		extraTempRegId = 0;
		dataIdRegeneration = true;
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
			short stackSize = (short) (dataIdMap.size() + tempSize - params.length);
			if (stackSize < 0) {
				throw new IllegalArgumentException(String.format("Stack-based subroutine \"%s\" has unexpected stack size %s!", name, stackSize));
			}
			
			// Post-optimization!
			
			boolean flag = true;
			while (flag) {
				flag = false;
				for (Entry<Short, List<Instruction>> entry : textSectionMap.entrySet()) {
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
		for (Entry<Short, List<Instruction>> entry : textSectionMap.entrySet()) {
			sectionAddressMap.put(entry.getKey(), sectionAddressOffset);
			sectionAddressOffset += entry.getValue().size();
		}
		code.textAddressMap.put(name, code.addressOffset);
		code.addressOffset += sectionAddressOffset;
	}
	
	public void generateDataAddresses() {
		int dataAddressOffset = 0;
		if (isStackRoutine()) {
			int paramAddressOffset = 0;
			for (long id : dataIdMap.values()) {
				if (id < 0) {
					dataAddressMap.put(addressKey(id), (short) (paramAddressOffset + 2));
					++paramAddressOffset;
				}
				else {
					dataAddressMap.put(addressKey(id), (short) (-dataAddressOffset - 1));
					++dataAddressOffset;
				}
			}
			for (long i = 0; i < tempSize; ++i) {
				tempAddressMap.put(addressKey(i), (short) (-dataAddressOffset - 1));
				++dataAddressOffset;
			}
		}
		else {
			for (Entry<DataId, Long> entry : dataIdMap.entrySet()) {
				if (isRootRoutine() && RedstoneGenerator.isRootParam(entry.getKey().name)) {
					dataAddressMap.put(addressKey(entry.getValue()), (short) (RedstoneCode.MAX_ADDRESS - RedstoneGenerator.parseRootParam(entry.getKey().name)));
				}
				else {
					dataAddressMap.put(addressKey(entry.getValue()), (short) (code.addressOffset + dataAddressOffset));
					++dataAddressOffset;
				}
			}
			for (long i = 0; i < tempSize; ++i) {
				tempAddressMap.put(addressKey(i), (short) (code.addressOffset + dataAddressOffset));
				++dataAddressOffset;
			}
			code.addressOffset += dataAddressOffset;
		}
	}
	
	public void finalizeInstructions() {
		for (Entry<Short, List<Instruction>> entry : textSectionMap.entrySet()) {
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
					ics.returnAddress = (short) (code.textAddressMap.get(name) + instructionAddress + 1);
					
					if (!ics.indirectCall && !(section.get(i - 1) instanceof InstructionLoadCallAddressImmediate)) {
						throw new IllegalArgumentException(String.format("Found unexpected direct subroutine call instruction \"%s\" not following call address load instruction as required!", instruction));
					}
				}
				
				else if (instruction instanceof InstructionLoadCallAddressImmediate) {
					InstructionLoadCallAddressImmediate ilcai = (InstructionLoadCallAddressImmediate) instruction;
					ilcai.setValue(code.textAddressMap.get(ilcai.subroutine));
				}
				
				else if (instruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) instruction;
					ij.address = (short) (code.textAddressMap.get(name) + sectionAddressMap.get(ij.section));
				}
				
				++instructionAddress;
			}
		}
	}
	
	public void onRequiresNesting() {
		intermediateRoutine.onRequiresNesting();
	}
	
	public void onRequiresStack() {
		intermediateRoutine.onRequiresStack();
	}
	
	public boolean isStackRoutine() {
		return intermediateRoutine.isStackRoutine();
	}
	
	public boolean isRootRoutine() {
		return intermediateRoutine.isRootRoutine();
	}
	
	protected long dataId(DataId key) {
		if (dataIdMap.containsKey(key)) {
			return dataIdMap.get(key);
		}
		else {
			long id = nextDataId();
			dataIdMap.put(key, id);
			return id;
		}
	}
	
	protected long nextDataId() {
		return nextId(dataIdMap.values());
	}
	
	protected long tempId(DataId key) {
		if (tempIdMap.containsKey(key)) {
			return dataIdRegeneration ? tempIdMap.get(key) : tempIdMap.remove(key);
		}
		else {
			long id = nextTempId();
			tempIdMap.put(key, id);
			tempSize = Math.max(tempSize, tempIdMap.size());
			return id;
		}
	}
	
	protected long nextTempId() {
		return nextId(tempIdMap.values());
	}
	
	protected static long nextId(Collection<Long> keys) {
		long i = 0;
		while (true) {
			if (!keys.contains(i)) {
				return i;
			}
			++i;
		}
	}
	
	protected long nextParamId() {
		if (isStackRoutine()) {
			long i = -1;
			while (true) {
				if (!dataIdMap.values().contains(i)) {
					return i;
				}
				--i;
			}
		}
		else {
			return nextId(dataIdMap.values());
		}
	}
	
	protected DataId nextExtraTempRegArg() {
		return new RegDataId(extraTempRegId--);
	}
	
	protected boolean isStackData(RedstoneDataInfo info) {
		return info.type != RedstoneDataType.STATIC && isStackRoutine();
	}
	
	// Data Info
	
	public RedstoneDataInfo dataInfo(DataId arg) {
		if (Helpers.isRegId(arg.name)) {
			return new RedstoneDataInfo(name, arg, RedstoneDataType.TEMP, tempId(arg));
		}
		else if (code.rootIdMap.containsKey(arg)) {
			return new RedstoneDataInfo(Global.ROOT_ROUTINE, arg, RedstoneDataType.STATIC, code.rootIdMap.get(arg));
		}
		else {
			return new RedstoneDataInfo(name, arg, isStackRoutine() ? RedstoneDataType.STACK : RedstoneDataType.STATIC, dataId(arg));
		}
	}
	
	public RedstoneAddressKey addressKey(long id) {
		return new RedstoneAddressKey(name, id);
	}
	
	protected short getAddress(RedstoneDataInfo info) {
		RedstoneRoutine routine = code.getRoutine(info.routineName);
		switch (info.type) {
			case TEMP:
				return routine.tempAddressMap.get(info.key);
			case STATIC:
				if (code.rootAddressMap.containsKey(info.key)) {
					return code.rootAddressMap.get(info.key);
				}
			case STACK:
				return routine.dataAddressMap.get(info.key);
			default:
				throw new IllegalArgumentException(String.format("Encountered unknown address data type \"%s\"!", info.type));
		}
	}
	
	// Instructions
	
	protected void store(List<Instruction> text, DataId arg, boolean autoDereference) {
		if (arg instanceof TransientDataId) {
			return;
		}
		else if (Helpers.isImmediateValue(arg.name)) {
			throw new IllegalArgumentException(String.format("Attempted to add an immediate store instruction! %s", arg));
		}
		else {
			int dereferenceLevel = autoDereference ? arg.dereferenceLevel : 0;
			if (dereferenceLevel == 0) {
				RedstoneDataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionStoreOffset(argInfo));
				}
				else {
					text.add(new InstructionStore(argInfo));
				}
			}
			else {
				arg = arg.removeAllDereferences();
				final RedstoneDataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadBOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadB(argInfo));
				}
				
				for (int i = 0; i < dereferenceLevel - 1; ++i) {
					text.add(new InstructionDereferenceB());
				}
				
				text.add(new InstructionStoreAToBAddress());
			}
		}
	}
	
	protected void declare(List<Instruction> text, DataId arg, boolean initialize) {
		if (arg instanceof TransientDataId) {
			return;
		}
		else if (Helpers.isImmediateValue(arg.name)) {
			throw new IllegalArgumentException(String.format("Attempted to add an immediate declaration instruction! %s", arg));
		}
		else {
			RedstoneDataInfo argInfo = dataInfo(arg);
			if (initialize) {
				if (isStackData(argInfo)) {
					text.add(new InstructionStoreOffset(argInfo));
				}
				else {
					text.add(new InstructionStore(argInfo));
				}
			}
			
			for (int i = 0; i < arg.dereferenceLevel; ++i) {
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadImmediateAddressOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadAddressImmediate(argInfo));
				}
				
				arg = arg.removeDereference();
				argInfo = dataInfo(arg);
				
				if (isStackData(argInfo)) {
					text.add(new InstructionStoreOffset(argInfo));
				}
				else {
					text.add(new InstructionStore(argInfo));
				}
			}
		}
	}
	
	protected void load(List<Instruction> text, DataId arg) {
		if (Helpers.isImmediateValue(arg.name)) {
			final short value = Helpers.parseImmediateValue(arg.name).shortValue();
			if (RedstoneCode.isLongImmediate(value)) {
				Instruction illi = new InstructionLoadLongImmediate(value);
				text.add(illi);
				text.add(illi.succeedingData());
			}
			else {
				text.add(new InstructionLoadImmediate(value));
			}
		}
		else if (code.routineExists(arg.name)) {
			text.add(new InstructionLoadCallAddressImmediate(arg.name));
			code.unusedBuiltInRoutineSet.remove(arg.name);
		}
		else {
			final boolean hasAddressPrefix = Helpers.hasAddressPrefix(arg.name);
			if (hasAddressPrefix) {
				final RedstoneDataInfo argInfo = dataInfo(arg.removeAddressPrefix());
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadImmediateAddressOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadAddressImmediate(argInfo));
				}
			}
			else {
				final RedstoneDataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadAOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadA(argInfo));
				}
			}
		}
	}
	
	protected void binaryOp(List<Instruction> text, BinaryOpType type, DataId arg) {
		DataId t0, t1;
		if (Helpers.isImmediateValue(arg.name)) {
			final short value = Helpers.parseImmediateValue(arg.name).shortValue();
			if (RedstoneCode.isLongImmediate(value)) {
				Instruction li;
				switch (type) {
					case LOGICAL_AND:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.AND, t0);
						break;
					case LOGICAL_OR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.OR, t0);
						break;
					case LOGICAL_XOR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.XOR, t0);
						break;
					case EQUAL_TO:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsZero());
						break;
					case NOT_EQUAL_TO:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsNotZero());
						break;
					case LESS_THAN:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsLessThanZero());
						break;
					case LESS_OR_EQUAL:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case MORE_THAN:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case MORE_OR_EQUAL:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case PLUS:
						text.add(li = new InstructionAddLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case AND:
						text.add(li = new InstructionAndLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case OR:
						text.add(li = new InstructionOrLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case XOR:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case MINUS:
						text.add(li = new InstructionSubtractLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case ARITHMETIC_LEFT_SHIFT:
						text.add(new InstructionLeftShiftImmediate(RedstoneCode.lowBits(value)));
						break;
					case ARITHMETIC_RIGHT_SHIFT:
						text.add(new InstructionRightShiftImmediate(RedstoneCode.lowBits(value)));
						break;
					case LOGICAL_RIGHT_SHIFT:
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						if (code.routineExists(Global.LOGICAL_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
						}
						else {
							text.add(new InstructionRightShiftImmediate(RedstoneCode.lowBits(value)));
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(RedstoneCode.LOAD_MIN_VALUE);
							text.add(RedstoneCode.LOAD_MIN_VALUE_SUCCEEDING);
							text.add(new InstructionRightShiftImmediate(RedstoneCode.lowBits(value)));
							text.add(new InstructionLeftShiftImmediate((short) 1));
							text.add(new InstructionSetNot());
							binaryOp(text, BinaryOpType.AND, t0);
						}
						break;
					case CIRCULAR_LEFT_SHIFT:
						// (x << y) | (x >>> (-y))
						if (code.routineExists(Global.CIRCULAR_LEFT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(new InstructionLoadImmediate(RedstoneCode.lowBits((short) -value)));
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							text.add(new InstructionLeftShiftImmediate(RedstoneCode.lowBits(value)));
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case CIRCULAR_RIGHT_SHIFT:
						// (x >>> y) | (x << (-y))
						if (code.routineExists(Global.CIRCULAR_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(new InstructionLoadImmediate(RedstoneCode.lowBits((short) -value)));
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.ARITHMETIC_LEFT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, Helpers.immediateDataId(RedstoneCode.lowBits(value)));
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case MULTIPLY:
						text.add(li = new InstructionMultiplyLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case DIVIDE:
						text.add(li = new InstructionDivideLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case REMAINDER:
						text.add(li = new InstructionRemainderLongImmediate(value));
						text.add(li.succeedingData());
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add long immediate binary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
					case LOGICAL_AND:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadImmediate(value));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.AND, t0);
						break;
					case LOGICAL_OR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadImmediate(value));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.OR, t0);
						break;
					case LOGICAL_XOR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadImmediate(value));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.XOR, t0);
						break;
					case EQUAL_TO:
						text.add(new InstructionXorImmediate(value));
						text.add(new InstructionSetIsZero());
						break;
					case NOT_EQUAL_TO:
						text.add(new InstructionXorImmediate(value));
						text.add(new InstructionSetIsNotZero());
						break;
					case LESS_THAN:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case LESS_OR_EQUAL:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case MORE_THAN:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case MORE_OR_EQUAL:
						text.add(new InstructionSubtractImmediate(value));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case PLUS:
						text.add(new InstructionAddImmediate(value));
						break;
					case AND:
						text.add(new InstructionAndImmediate(value));
						break;
					case OR:
						text.add(new InstructionOrImmediate(value));
						break;
					case XOR:
						text.add(new InstructionXorImmediate(value));
						break;
					case MINUS:
						text.add(new InstructionSubtractImmediate(value));
						break;
					case ARITHMETIC_LEFT_SHIFT:
						text.add(new InstructionLeftShiftImmediate(value));
						break;
					case ARITHMETIC_RIGHT_SHIFT:
						text.add(new InstructionRightShiftImmediate(value));
						break;
					case LOGICAL_RIGHT_SHIFT:
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						if (code.routineExists(Global.LOGICAL_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadImmediate(value));
						}
						else {
							text.add(new InstructionRightShiftImmediate(value));
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(RedstoneCode.LOAD_MIN_VALUE);
							text.add(RedstoneCode.LOAD_MIN_VALUE_SUCCEEDING);
							text.add(new InstructionRightShiftImmediate(value));
							text.add(new InstructionLeftShiftImmediate((short) 1));
							text.add(new InstructionSetNot());
							binaryOp(text, BinaryOpType.AND, t0);
						}
						break;
					case CIRCULAR_LEFT_SHIFT:
						// (x << y) | (x >>> (-y))
						if (code.routineExists(Global.CIRCULAR_LEFT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadImmediate(value));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(new InstructionLoadImmediate((short) -value));
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							text.add(new InstructionLeftShiftImmediate(value));
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case CIRCULAR_RIGHT_SHIFT:
						// (x >>> y) | (x << (-y))
						if (code.routineExists(Global.CIRCULAR_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadImmediate(value));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(new InstructionLoadImmediate((short) -value));
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.ARITHMETIC_LEFT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, Helpers.immediateDataId(value));
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case MULTIPLY:
						text.add(new InstructionMultiplyImmediate(value));
						break;
					case DIVIDE:
						text.add(new InstructionDivideImmediate(value));
						break;
					case REMAINDER:
						text.add(new InstructionRemainderImmediate(value));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add immediate binary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
		else {
			final RedstoneDataInfo argInfo = dataInfo(arg);
			if (isStackData(argInfo)) {
				switch (type) {
					case LOGICAL_AND:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.AND, t0);
						break;
					case LOGICAL_OR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.OR, t0);
						break;
					case LOGICAL_XOR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.XOR, t0);
						break;
					case EQUAL_TO:
						text.add(new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case NOT_EQUAL_TO:
						text.add(new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case LESS_THAN:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case LESS_OR_EQUAL:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case MORE_THAN:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case MORE_OR_EQUAL:
						text.add(new InstructionSubtractOffset(argInfo));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case PLUS:
						text.add(new InstructionAddOffset(argInfo));
						break;
					case AND:
						text.add(new InstructionAndOffset(argInfo));
						break;
					case OR:
						text.add(new InstructionOrOffset(argInfo));
						break;
					case XOR:
						text.add(new InstructionXorOffset(argInfo));
						break;
					case MINUS:
						text.add(new InstructionSubtractOffset(argInfo));
						break;
					case ARITHMETIC_LEFT_SHIFT:
						text.add(new InstructionLeftShiftOffset(argInfo));
						break;
					case ARITHMETIC_RIGHT_SHIFT:
						text.add(new InstructionRightShiftOffset(argInfo));
						break;
					case LOGICAL_RIGHT_SHIFT:
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						if (code.routineExists(Global.LOGICAL_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadAOffset(argInfo));
						}
						else {
							text.add(new InstructionRightShiftOffset(argInfo));
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(RedstoneCode.LOAD_MIN_VALUE);
							text.add(RedstoneCode.LOAD_MIN_VALUE_SUCCEEDING);
							text.add(new InstructionRightShiftOffset(argInfo));
							text.add(new InstructionLeftShiftImmediate((short) 1));
							text.add(new InstructionSetNot());
							binaryOp(text, BinaryOpType.AND, t0);
						}
						break;
					case CIRCULAR_LEFT_SHIFT:
						// (x << y) | (x >>> (-y))
						if (code.routineExists(Global.CIRCULAR_LEFT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadAOffset(argInfo));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							unaryOp(text, UnaryOpType.MINUS, argInfo.argId);
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							text.add(new InstructionLeftShiftOffset(argInfo));
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case CIRCULAR_RIGHT_SHIFT:
						// (x >>> y) | (x << (-y))
						if (code.routineExists(Global.CIRCULAR_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadAOffset(argInfo));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							unaryOp(text, UnaryOpType.MINUS, argInfo.argId);
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.ARITHMETIC_LEFT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, argInfo.argId);
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case MULTIPLY:
						text.add(new InstructionMultiplyOffset(argInfo));
						break;
					case DIVIDE:
						text.add(new InstructionDivideOffset(argInfo));
						break;
					case REMAINDER:
						text.add(new InstructionRemainderOffset(argInfo));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset binary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
					case LOGICAL_AND:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.AND, t0);
						break;
					case LOGICAL_OR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.OR, t0);
						break;
					case LOGICAL_XOR:
						text.add(new InstructionSetIsNotZero());
						store(text, t0 = nextExtraTempRegArg(), true);
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetIsNotZero());
						binaryOp(text, BinaryOpType.XOR, t0);
						break;
					case EQUAL_TO:
						text.add(new InstructionXor(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case NOT_EQUAL_TO:
						text.add(new InstructionXor(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case LESS_THAN:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsLessThanZero());
						break;
					case LESS_OR_EQUAL:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsLessThanOrEqualToZero());
						break;
					case MORE_THAN:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsMoreThanZero());
						break;
					case MORE_OR_EQUAL:
						text.add(new InstructionSubtract(argInfo));
						text.add(new InstructionSetIsMoreThanOrEqualToZero());
						break;
					case PLUS:
						text.add(new InstructionAdd(argInfo));
						break;
					case AND:
						text.add(new InstructionAnd(argInfo));
						break;
					case OR:
						text.add(new InstructionOr(argInfo));
						break;
					case XOR:
						text.add(new InstructionXor(argInfo));
						break;
					case MINUS:
						text.add(new InstructionSubtract(argInfo));
						break;
					case ARITHMETIC_LEFT_SHIFT:
						text.add(new InstructionLeftShift(argInfo));
						break;
					case ARITHMETIC_RIGHT_SHIFT:
						text.add(new InstructionRightShift(argInfo));
						break;
					case LOGICAL_RIGHT_SHIFT:
						// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
						if (code.routineExists(Global.LOGICAL_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.LOGICAL_RIGHT_SHIFT, new InstructionLoadA(argInfo));
						}
						else {
							text.add(new InstructionRightShift(argInfo));
							store(text, t0 = nextExtraTempRegArg(), true);
							text.add(RedstoneCode.LOAD_MIN_VALUE);
							text.add(RedstoneCode.LOAD_MIN_VALUE_SUCCEEDING);
							text.add(new InstructionRightShift(argInfo));
							text.add(new InstructionLeftShiftImmediate((short) 1));
							text.add(new InstructionSetNot());
							binaryOp(text, BinaryOpType.AND, t0);
						}
						break;
					case CIRCULAR_LEFT_SHIFT:
						// (x << y) | (x >>> (-y))
						if (code.routineExists(Global.CIRCULAR_LEFT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_LEFT_SHIFT, new InstructionLoadA(argInfo));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							unaryOp(text, UnaryOpType.MINUS, argInfo.argId);
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							text.add(new InstructionLeftShift(argInfo));
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case CIRCULAR_RIGHT_SHIFT:
						// (x >>> y) | (x << (-y))
						if (code.routineExists(Global.CIRCULAR_RIGHT_SHIFT)) {
							binaryOpBuiltInSubroutine(text, Global.CIRCULAR_RIGHT_SHIFT, new InstructionLoadA(argInfo));
						}
						else {
							store(text, t0 = nextExtraTempRegArg(), true);
							unaryOp(text, UnaryOpType.MINUS, argInfo.argId);
							store(text, t1 = nextExtraTempRegArg(), true);
							load(text, t0);
							binaryOp(text, BinaryOpType.ARITHMETIC_LEFT_SHIFT, t1);
							store(text, t1, true);
							load(text, t0);
							binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, argInfo.argId);
							binaryOp(text, BinaryOpType.OR, t1);
						}
						break;
					case MULTIPLY:
						text.add(new InstructionMultiply(argInfo));
						break;
					case DIVIDE:
						text.add(new InstructionDivide(argInfo));
						break;
					case REMAINDER:
						text.add(new InstructionRemainder(argInfo));
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address binary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
	}
	
	protected void binaryOpBuiltInSubroutine(List<Instruction> text, String name, Instruction... load) {
		RedstoneRoutine subroutine = code.getRoutine(name);
		subroutine.store(text, subroutine.params[0].dataId(), false);
		for (Instruction li : load) {
			text.add(li);
		}
		subroutine.store(text, subroutine.params[1].dataId(), false);
		text.add(new InstructionLoadCallAddressImmediate(name));
		text.add(new InstructionCallSubroutine(false));
		onRequiresNesting();
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
	
	protected void dereference(List<Instruction> text, int dereferenceLevel, DataId arg) {
		if (Helpers.isImmediateValue(arg.name)) {
			final short value = Helpers.parseImmediateValue(arg.name).shortValue();
			if (RedstoneCode.isLongImmediate(value)) {
				text.add(new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
			}
			else {
				text.add(new InstructionLoadImmediate(value));
			}
		}
		else {
			final RedstoneDataInfo argInfo = dataInfo(arg);
			if (isStackData(argInfo)) {
				text.add(new InstructionLoadAOffset(argInfo));
			}
			else {
				text.add(new InstructionLoadA(argInfo));
			}
		}
		
		for (int i = 0; i < dereferenceLevel; ++i) {
			text.add(new InstructionDereferenceA());
		}
	}
	
	protected void unaryOp(List<Instruction> text, UnaryOpType type, DataId arg) {
		if (Helpers.isImmediateValue(arg.name)) {
			final short value = Helpers.parseImmediateValue(arg.name).shortValue();
			Instruction li;
			if (RedstoneCode.isLongImmediate(value)) {
				switch (type) {
					case PLUS:
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case MINUS:
						if (RedstoneCode.isLongImmediate((short) (-value))) {
							text.add(li = new InstructionLoadLongImmediate((short) (-value)));
							text.add(li.succeedingData());
						}
						else {
							text.add(new InstructionLoadImmediate((short) (-value)));
						}
						break;
					case COMPLEMENT:
						text.add(li = new InstructionNotLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case TO_BOOL:
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsNotZero());
						break;
					case NOT:
						text.add(li = new InstructionLoadLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsZero());
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add long immediate unary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
					case PLUS:
						text.add(new InstructionLoadImmediate(value));
						break;
					case MINUS:
						if (RedstoneCode.isLongImmediate((short) (-value))) {
							text.add(li = new InstructionLoadLongImmediate((short) (-value)));
							text.add(li.succeedingData());
						}
						else {
							text.add(new InstructionLoadImmediate((short) (-value)));
						}
						break;
					case COMPLEMENT:
						text.add(new InstructionNotImmediate(value));
						break;
					case TO_BOOL:
						text.add(new InstructionLoadImmediate(value));
						text.add(new InstructionSetIsNotZero());
						break;
					case NOT:
						text.add(new InstructionLoadImmediate(value));
						text.add(new InstructionSetIsZero());
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add immediate unary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
		else {
			final RedstoneDataInfo argInfo = dataInfo(arg);
			if (isStackData(argInfo)) {
				switch (type) {
					case PLUS:
						text.add(new InstructionLoadAOffset(argInfo));
						break;
					case MINUS:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetNegative());
						break;
					case COMPLEMENT:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetNot());
						break;
					case TO_BOOL:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case NOT:
						text.add(new InstructionLoadAOffset(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset unary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
					case PLUS:
						text.add(new InstructionLoadA(argInfo));
						break;
					case MINUS:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetNegative());
						break;
					case COMPLEMENT:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetNot());
						break;
					case TO_BOOL:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetIsNotZero());
						break;
					case NOT:
						text.add(new InstructionLoadA(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address unary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
	}
	
	protected void builtInFunctionCall(List<Instruction> text, BuiltInFunctionCallAction action) {
		String functionName = action.function.name;
		if (functionName.equals(Global.OUTCHAR)) {
			load(text, action.args.get(0));
			text.add(new InstructionAndImmediate((short) 0x7F));
			text.add(new InstructionOutput());
		}
		else if (functionName.equals(Global.OUTINT)) {
			load(text, action.args.get(0));
			text.add(new InstructionOutput());
		}
		else if (functionName.equals(Global.ARGV_FUNCTION)) {
			DataId arg = action.args.get(0);
			if (Helpers.isImmediateValue(arg.name)) {
				load(text, code.generator.rootParamDataId(Main.program.rootRoutine, Helpers.parseImmediateValue(arg.name).intValue()));
			}
			else {
				load(text, arg);
				text.add(new InstructionSetNot());
				text.add(new InstructionDereferenceA());
			}
			store(text, action.target, true);
		}
		else {
			throw new IllegalArgumentException(String.format("Encountered unsupported built-in function call action \"%s\"!", action));
		}
	}
}
