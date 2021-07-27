package drlc.generate.drc1;

import java.util.*;
import java.util.Map.Entry;

import drlc.*;
import drlc.generate.drc1.DataInfo.DataType;
import drlc.generate.drc1.instruction.*;
import drlc.generate.drc1.instruction.address.*;
import drlc.generate.drc1.instruction.address.offset.*;
import drlc.generate.drc1.instruction.hardware.InstructionOutput;
import drlc.generate.drc1.instruction.immediate.*;
import drlc.generate.drc1.instruction.jump.*;
import drlc.generate.drc1.instruction.pointer.*;
import drlc.generate.drc1.instruction.set.*;
import drlc.generate.drc1.instruction.subroutine.*;
import drlc.interpret.action.*;
import drlc.interpret.action.BinaryOpAction.BinaryOpType;
import drlc.interpret.action.UnaryOpAction.UnaryOpType;
import drlc.interpret.routine.*;
import drlc.interpret.type.*;

public class RedstoneRoutine {
	
	protected final RedstoneCode code;
	protected final Routine intermediateRoutine;
	protected final String name;
	protected final VariableReferenceInfo[] params;
	
	public final Map<Short, List<Instruction>> textSectionMap = new TreeMap<>();
	
	public final Map<String, Integer> dataIdMap;
	public int tempSize = 0;
	public final Map<String, Integer> tempIdMap = new LinkedHashMap<>();
	
	public final Map<Short, Short> sectionAddressMap = new HashMap<>();
	public final Map<Integer, Short> dataAddressMap;
	public final Map<Integer, Short> tempAddressMap = new HashMap<>();
	
	RedstoneRoutine(RedstoneCode code, Routine intermediateRoutine) {
		this.code = code;
		this.intermediateRoutine = intermediateRoutine;
		this.name = intermediateRoutine.name;
		if (isRootRoutine()) {
			RootRoutine rootRoutine = (RootRoutine) intermediateRoutine;
			params = generateRootParams(rootRoutine);
			dataIdMap = code.staticIdMap;
			dataAddressMap = code.staticAddressMap;
		}
		else {
			Subroutine subroutine = (Subroutine) intermediateRoutine;
			params = subroutine.params;
			dataIdMap = new LinkedHashMap<>();
			dataAddressMap = new HashMap<>();
		}
		mapParams();
	}
	
	public VariableReferenceInfo[] generateRootParams(RootRoutine rootRoutine) {
		VariableReferenceInfo[] array = new VariableReferenceInfo[rootRoutine.argc];
		for (int i = 0; i < rootRoutine.argc; i++) {
			array[i] = new VariableReferenceInfo(new Variable(getRootParam(i), 0, true));
		}
		return array;
	}
	
	public static final String ARGV_PARAM = "\\argv";
	
	public String getRootParam(int i) {
		return ARGV_PARAM.concat(Integer.toString(i));
	}
	
	public boolean isRootParam(String s) {
		return s.startsWith(ARGV_PARAM);
	}
	
	public int parseRootParam(String s) {
		return Integer.parseInt(s.substring(ARGV_PARAM.length()));
	}
	
	public void mapParams() {
		for (VariableReferenceInfo param : params) {
			int id = nextParamId();
			dataIdMap.put(param.variable.name, id);
		}
	}
	
	public void generateInstructions() {
		if (isRootRoutine()) {
			if (code.requiresStack) {
				List<Instruction> text = new ArrayList<>();
				textSectionMap.put((short) -1, text);
				text.add(new InstructionLoadBasePointer((short) (0xFF - params.length)));
				text.add(new InstructionLoadStackPointer((short) (0xFF - params.length)));
			}
		}
		else if (isRecursive()) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put((short) -1, text);
			text.add(new InstructionPushBasePointer());
			text.add(new InstructionMoveStackPointerToBasePointer());
			text.add(new InstructionSubtractFromStackPointer());
		}
		
		List<List<Action>> body = intermediateRoutine.getBodyActionLists();
		for (int i = 0; i < body.size(); i++) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put((short) i, text);
			
			List<Action> actions = body.get(i);
			for (int j = 0; j < actions.size(); j++) {
				Action action = actions.get(j);
				
				if (action instanceof AssignmentAction) {
					AssignmentAction aa = (AssignmentAction) action;
					load(text, aa.arg);
					store(text, aa.target, false);
				}
				
				else if (action instanceof BasicAction) {
					if (action instanceof HaltAction) {
						text.add(new InstructionHalt());
					}
					else {
						throw new IllegalArgumentException(String.format("Encountered unknown basic action %s!", action));
					}
				}
				
				else if (action instanceof BinaryOpAction) {
					BinaryOpAction boa = (BinaryOpAction) action;
					load(text, boa.arg1);
					binaryOp(text, boa.opType, boa.arg2);
					store(text, boa.target, false);
				}
				
				else if (action instanceof ConditionalJumpAction) {
					ConditionalJumpAction cja = (ConditionalJumpAction) action;
					conditionalJump(text, cja.target, cja.jumpCondition);
				}
				
				else if (action instanceof DeclarationAction) {
					DeclarationAction da = (DeclarationAction) action;
					load(text, Helper.immediateValueString(0));
					store(text, da.target, true);
				}
				
				else if (action instanceof InitialisationAction) {
					InitialisationAction ia = (InitialisationAction) action;
					load(text, ia.arg);
					store(text, ia.target, true);
				}
				
				else if (action instanceof JumpAction) {
					JumpAction ja = (JumpAction) action;
					jump(text, ja.target);
				}
				
				else if (action instanceof NoOpAction) {
					text.add(new InstructionNoOp());
				}
				
				else if (action instanceof PlaceholderAction) {
					throw new IllegalArgumentException(String.format("Placeholder action not correctly substituted!"));
				}
				
				else if (action instanceof ReturnAction) {
					if (isRootRoutine()) {
						text.add(new InstructionHalt());
					}
					else if (isRecursive()) {
						text.add(new InstructionJump((short) body.size()));
					}
					else {
						text.add(new InstructionReturnFromSubroutine());
					}
				}
				
				else if (action instanceof ReturnValueAction) {
					if (isRootRoutine()) {
						throw new IllegalArgumentException(String.format("Root routine can not return a value!"));
					}
					else {
						ReturnValueAction rva = (ReturnValueAction) action;
						load(text, rva.arg);
						if (isRecursive()) {
							text.add(new InstructionJump((short) body.size()));
						}
						else {
							text.add(new InstructionReturnFromSubroutine());
						}
					}
				}
				
				else if (action instanceof SubroutineCallAction) {
					if (action instanceof BuiltInFunctionCallAction) {
						builtInFunction(text, (BuiltInFunctionCallAction) action);
					}
					else if (action instanceof BuiltInMethodCallAction) {
						builtInMethod(text, (BuiltInMethodCallAction) action);
					}
					else {
						SubroutineCallAction sca = (SubroutineCallAction) action;
						RedstoneRoutine subroutine = code.routineMap.get(sca.name);
						
						if (subroutine.isRecursive()) {
							for (int k = sca.args.length - 1; k >= 0; k--) {
								load(text, sca.args[k]);
								text.add(new InstructionPush());
							}
						}
						else {
							for (int k = 0; k < sca.args.length; k++) {
								load(text, sca.args[k]);
								subroutine.store(text, subroutine.params[k].variable.name, false);
							}
						}
						
						Instruction ics = new InstructionCallSubroutine(sca.name);
						text.add(ics);
						text.add(ics.succeedingData());
						
						if (subroutine.isRecursive()) {
							text.add(new InstructionAddToStackPointer((short) sca.args.length));
						}
						
						if (sca instanceof FunctionCallAction) {
							FunctionCallAction fca = (FunctionCallAction) sca;
							store(text, fca.target, false);
						}
					}
				}
				
				else if (action instanceof DereferenceAction) {
					DereferenceAction da = (DereferenceAction) action;
					dereference(text, da.dereferenceLevel, da.arg);
					store(text, da.target, false);
				}
				
				else if (action instanceof UnaryOpAction) {
					UnaryOpAction uoa = (UnaryOpAction) action;
					unaryOp(text, uoa.opType, uoa.arg);
					store(text, uoa.target, false);
				}
				
				else {
					throw new IllegalArgumentException(String.format("Encountered unknown action %s!", action));
				}
			}
		}
		
		if (isRecursive()) {
			List<Instruction> text = new ArrayList<>();
			textSectionMap.put((short) body.size(), text);
			text.add(new InstructionAddToStackPointer());
			text.add(new InstructionPopBasePointer());
			text.add(new InstructionReturnFromSubroutine());
		}
	}
	
	public void regenerateDataIds() {
		dataIdMap.clear();
		tempSize = 0;
		tempIdMap.clear();
		mapParams();
		
		for (List<Instruction> section : textSectionMap.values()) {
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction instanceof IInstructionAddress) {
					IInstructionAddress instructionAddress = (IInstructionAddress) instruction;
					section.set(i, instructionAddress.getDataReplacement(this));
				}
			}
		}
		
		if (isRecursive()) {
			short stackSize = (short) (dataIdMap.size() + tempSize - params.length);
			if (stackSize < 0) {
				throw new IllegalArgumentException(String.format("Recursive subroutine %s has unexpected stack size %s!", name, stackSize));
			}
			
			// Post-optimization!
			
			boolean flag = true;
			while (flag) {
				flag = false;
				for (Entry<Short, List<Instruction>> entry : textSectionMap.entrySet()) {
					List<Instruction> section = entry.getValue();
					for (int i = 0; i < section.size(); i++) {
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
		if (isRecursive()) {
			int paramAddressOffset = 0;
			for (int id : dataIdMap.values()) {
				if (id < 0) {
					dataAddressMap.put(id, (short) (paramAddressOffset + 2));
					paramAddressOffset++;
				}
				else {
					dataAddressMap.put(id, (short) (-dataAddressOffset - 1));
					dataAddressOffset++;
				}
			}
			for (int i = 0; i < tempSize; i++) {
				tempAddressMap.put(i, (short) (-dataAddressOffset - 1));
				dataAddressOffset++;
			}
		}
		else {
			for (Entry<String, Integer> entry : dataIdMap.entrySet()) {
				if (isRootRoutine() && isRootParam(entry.getKey())) {
					dataAddressMap.put(entry.getValue(), (short) (0xFF - parseRootParam(entry.getKey())));
				}
				else {
					dataAddressMap.put(entry.getValue(), (short) (code.addressOffset + dataAddressOffset));
					dataAddressOffset++;
				}
			}
			for (int i = 0; i < tempSize; i++) {
				tempAddressMap.put(i, (short) (code.addressOffset + dataAddressOffset));
				dataAddressOffset++;
			}
			code.addressOffset += dataAddressOffset;
		}
	}
	
	public void finalizeInstructions() {
		for (Entry<Short, List<Instruction>> entry : textSectionMap.entrySet()) {
			short instructionAddress = sectionAddressMap.get(entry.getKey());
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); i++) {
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
					ics.returnAddress = (short) (code.textAddressMap.get(name) + instructionAddress + 2);
					ics.callAddress = code.textAddressMap.get(ics.subroutine);
					
					Instruction next = section.get(i + 1);
					if (!(next instanceof InstructionConstant)) {
						throw new IllegalArgumentException(String.format("Found unexpected subroutine call instruction %s not preceding constant as required!", instruction));
					}
					else {
						InstructionConstant ic = (InstructionConstant) next;
						ic.setValue(ics.callAddress);
					}
				}
				
				else if (instruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) instruction;
					ij.address = (short) (code.textAddressMap.get(name) + sectionAddressMap.get(ij.section));
				}
				
				++instructionAddress;
			}
		}
	}
	
	protected boolean isLeaf() {
		return intermediateRoutine.getType() == RoutineType.LEAF;
	}
	
	protected boolean isNesting() {
		return intermediateRoutine.getType() == RoutineType.NESTING;
	}
	
	protected boolean isRecursive() {
		return intermediateRoutine.getType() == RoutineType.RECURSIVE;
	}
	
	protected boolean isRootRoutine() {
		return intermediateRoutine instanceof RootRoutine;
	}
	
	protected int dataId(String key) {
		if (dataIdMap.containsKey(key)) {
			return dataIdMap.get(key);
		}
		else {
			int id = nextId(dataIdMap.values());
			dataIdMap.put(key, id);
			return id;
		}
	}
	
	protected int tempId(String key) {
		if (tempIdMap.containsKey(key)) {
			return tempIdMap.remove(key);
		}
		else {
			int id = nextId(tempIdMap.values());
			tempIdMap.put(key, id);
			tempSize = Math.max(tempSize, tempIdMap.size());
			return id;
		}
	}
	
	protected static int nextId(Collection<Integer> keys) {
		int i = 0;
		while (true) {
			if (!keys.contains(i)) {
				return i;
			}
			++i;
		}
	}
	
	protected int nextParamId() {
		if (isRecursive()) {
			int i = -1;
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
	
	protected boolean isStackData(DataInfo info) {
		return info.type != DataType.STATIC && isRecursive();
	}
	
	// Data Info
	
	public DataInfo dataInfo(String arg) {
		if (Helper.isRegId(arg)) {
			return new DataInfo(name, arg, DataType.TEMP, tempId(arg));
		}
		else if (code.staticIdMap.containsKey(arg)) {
			return new DataInfo(name, arg, DataType.STATIC, code.staticIdMap.get(arg));
		}
		else {
			return new DataInfo(name, arg, isRootRoutine() ? DataType.STATIC : DataType.DATA, dataId(arg));
		}
	}
	
	protected short getAddress(DataInfo info) {
		RedstoneRoutine routine = code.routineMap.get(info.routineName);
		switch (info.type) {
			case DATA:
				return routine.dataAddressMap.get(info.id);
			case TEMP:
				return routine.tempAddressMap.get(info.id);
			case STATIC:
				return code.staticAddressMap.get(info.id);
			default:
				throw new IllegalArgumentException(String.format("Encountered unknown address data type %s!", info.type));
		}
	}
	
	// Instructions
	
	protected void store(List<Instruction> text, String arg, boolean declarationOrInitialization) {
		if (arg.equals(Global.TRANSIENT)) {
			return;
		}
		else if (Helper.isImmediateValue(arg)) {
			throw new IllegalArgumentException(String.format("Attempted to add an immediate store instruction! %s", arg));
		}
		else {
			final int dereferenceLevel = Helper.getDereferenceLevel(arg);
			if (dereferenceLevel == 0 || declarationOrInitialization) {
				DataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionStoreOffset(argInfo));
				}
				else {
					text.add(new InstructionStore(argInfo));
				}
				
				if (declarationOrInitialization) {
					for (int i = 0; i < dereferenceLevel; i++) {
						if (isStackData(argInfo)) {
							text.add(new InstructionLoadImmediateAddressOffset(argInfo));
						}
						else {
							text.add(new InstructionLoadImmediateAddress(argInfo));
						}
						
						arg = Helper.singlyDereference(arg);
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
			else {
				arg = Helper.fullyDereference(arg);
				final DataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadBOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadB(argInfo));
				}
				
				for (int i = 0; i < dereferenceLevel - 1; i++) {
					text.add(new InstructionDereferenceB());
				}
				
				text.add(new InstructionStoreAToBAddress());
			}
		}
	}
	
	protected void load(List<Instruction> text, String arg) {
		if (Helper.isImmediateValue(arg)) {
			final short value = Helper.parseImmediateValue(arg).shortValue();
			if (RedstoneCode.isLongImmediate(value)) {
				Instruction illi = new InstructionLoadLongImmediate(value);
				text.add(illi);
				text.add(illi.succeedingData());
			}
			else {
				text.add(new InstructionLoadImmediate(value));
			}
		}
		else {
			final boolean hasAddressPrefix = Helper.hasAddressPrefix(arg);
			if (hasAddressPrefix) {
				final DataInfo argInfo = dataInfo(Helper.removeAddressPrefix(arg));
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadImmediateAddressOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadImmediateAddress(argInfo));
				}
			}
			else {
				final DataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadAOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadA(argInfo));
				}
			}
		}
	}
	
	protected void binaryOp(List<Instruction> text, BinaryOpType type, String arg) {
		if (Helper.isImmediateValue(arg)) {
			final short value = Helper.parseImmediateValue(arg).shortValue();
			if (RedstoneCode.isLongImmediate(value)) {
				Instruction li;
				switch (type) {
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
					case LEFT_SHIFT:
						text.add(new InstructionLeftShiftImmediate(RedstoneCode.lowBits(value)));
						break;
					case RIGHT_SHIFT:
						text.add(new InstructionRightShiftImmediate(RedstoneCode.lowBits(value)));
						break;
					case MULTIPLY:
						text.add(li = new InstructionMultiplyLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case EQUAL_TO:
						text.add(li = new InstructionXorLongImmediate(value));
						text.add(li.succeedingData());
						text.add(new InstructionSetIsZero());
						break;
					case DIVIDE:
						text.add(li = new InstructionDivideLongImmediate(value));
						text.add(li.succeedingData());
						break;
					case REMAINDER:
						text.add(li = new InstructionRemainderLongImmediate(value));
						text.add(li.succeedingData());
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
					default:
						throw new IllegalArgumentException(String.format("Attempted to add long immediate binary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
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
					case LEFT_SHIFT:
						text.add(new InstructionLeftShiftImmediate(value));
						break;
					case RIGHT_SHIFT:
						text.add(new InstructionRightShiftImmediate(value));
						break;
					case MULTIPLY:
						text.add(new InstructionMultiplyImmediate(value));
						break;
					case EQUAL_TO:
						text.add(new InstructionXorImmediate(value));
						text.add(new InstructionSetIsZero());
						break;
					case DIVIDE:
						text.add(new InstructionDivideImmediate(value));
						break;
					case REMAINDER:
						text.add(new InstructionRemainderImmediate(value));
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
					default:
						throw new IllegalArgumentException(String.format("Attempted to add immediate binary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
		else {
			final DataInfo argInfo = dataInfo(arg);
			if (isStackData(argInfo)) {
				switch (type) {
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
					case LEFT_SHIFT:
						text.add(new InstructionLeftShiftOffset(argInfo));
						break;
					case RIGHT_SHIFT:
						text.add(new InstructionRightShiftOffset(argInfo));
						break;
					case MULTIPLY:
						text.add(new InstructionMultiplyOffset(argInfo));
						break;
					case EQUAL_TO:
						text.add(new InstructionXorOffset(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case DIVIDE:
						text.add(new InstructionDivideOffset(argInfo));
						break;
					case REMAINDER:
						text.add(new InstructionRemainderOffset(argInfo));
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
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address offset binary op instruction of unknown type! %s %s", type, arg));
				}
			}
			else {
				switch (type) {
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
					case LEFT_SHIFT:
						text.add(new InstructionLeftShift(argInfo));
						break;
					case RIGHT_SHIFT:
						text.add(new InstructionRightShift(argInfo));
						break;
					case MULTIPLY:
						text.add(new InstructionMultiply(argInfo));
						break;
					case EQUAL_TO:
						text.add(new InstructionXor(argInfo));
						text.add(new InstructionSetIsZero());
						break;
					case DIVIDE:
						text.add(new InstructionDivide(argInfo));
						break;
					case REMAINDER:
						text.add(new InstructionRemainder(argInfo));
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
					default:
						throw new IllegalArgumentException(String.format("Attempted to add address binary op instruction of unknown type! %s %s", type, arg));
				}
			}
		}
	}
	
	protected void conditionalJump(List<Instruction> text, String section, boolean jumpCondition) {
		short sect = Helper.parseSectionId(section).shortValue();
		if (jumpCondition) {
			text.add(new InstructionConditionalJumpIfNotZero(sect));
		}
		else {
			text.add(new InstructionConditionalJumpIfZero(sect));
		}
	}
	
	protected void jump(List<Instruction> text, String section) {
		text.add(new InstructionJump(Helper.parseSectionId(section).shortValue()));
	}
	
	protected void dereference(List<Instruction> text, int dereferenceLevel, String arg) {
		if (Helper.isImmediateValue(arg)) {
			final short value = Helper.parseImmediateValue(arg).shortValue();
			if (RedstoneCode.isLongImmediate(value)) {
				text.add(new InstructionLoadImmediate(RedstoneCode.lowBits(value)));
			}
			else {
				text.add(new InstructionLoadImmediate(value));
			}
		}
		else {
			final DataInfo argInfo = dataInfo(arg);
			if (isStackData(argInfo)) {
				text.add(new InstructionLoadAOffset(argInfo));
			}
			else {
				text.add(new InstructionLoadA(argInfo));
			}
		}
		
		for (int i = 0; i < dereferenceLevel; i++) {
			text.add(new InstructionDereferenceA());
		}
	}
	
	protected void unaryOp(List<Instruction> text, UnaryOpType type, String arg) {
		if (Helper.isImmediateValue(arg)) {
			final short value = Helper.parseImmediateValue(arg).shortValue();
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
			final DataInfo argInfo = dataInfo(arg);
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
	
	protected void builtInFunction(List<Instruction> text, BuiltInFunctionCallAction action) {
		if (action.name.equals(Global.IN)) {
			throw new IllegalArgumentException(String.format("Built-in function action \"%s\" is not supported!", Global.IN));
		}
		else if (action.name.equals(Global.ARGV)) {
			String arg = action.args[0];
			if (Helper.isImmediateValue(arg)) {
				load(text, getRootParam(Helper.parseImmediateValue(arg).shortValue()));
			}
			else {
				final DataInfo argInfo = dataInfo(arg);
				if (isStackData(argInfo)) {
					text.add(new InstructionLoadAOffset(argInfo));
				}
				else {
					text.add(new InstructionLoadA(argInfo));
				}
				text.add(new InstructionSetNot());
				text.add(new InstructionDereferenceA());
				
			}
			store(text, action.target, false);
		}
		else {
			throw new IllegalArgumentException(String.format("Encountered unknown built-in function action %s!", action));
		}
	}
	
	protected void builtInMethod(List<Instruction> text, BuiltInMethodCallAction action) {
		if (action.name.equals(Global.OUT)) {
			load(text, action.args[0]);
			text.add(new InstructionOutput());
		}
		else {
			throw new IllegalArgumentException(String.format("Encountered unknown built-in method action %s!", action));
		}
	}
}
