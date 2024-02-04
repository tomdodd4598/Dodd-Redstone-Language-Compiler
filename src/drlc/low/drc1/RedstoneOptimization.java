package drlc.low.drc1;

import java.util.*;
import java.util.Map.Entry;

import drlc.Helpers.Pair;
import drlc.low.LowDataInfo;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.address.*;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.*;

public class RedstoneOptimization {
	
	public static boolean removeNoOps(RedstoneRoutine routine) {
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
	
	public static boolean checkConstants(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction.precedesData()) {
					if (i == section.size() - 1 || !(section.get(i + 1) instanceof InstructionConstant)) {
						throw new IllegalArgumentException(String.format("Found unexpected instruction %s not preceding constant as required!", instruction));
					}
				}
				else {
					if (i < section.size() - 1 && section.get(i + 1) instanceof InstructionConstant) {
						flag = true;
						section.set(i + 1, new InstructionNoOp());
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean removeDeadInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		Set<Integer> possibleDeadSections = new HashSet<>(), requiredSections = new HashSet<>();
		for (Entry<Integer, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue(), previous = routine.textSectionMap.get(entry.getKey() - 1);
			if (previous != null && !previous.isEmpty() && !section.isEmpty()) {
				Instruction previousInstruction = previous.get(previous.size() - 1);
				if (previousInstruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) previousInstruction;
					if (ij.isDefiniteJump()) {
						possibleDeadSections.add(entry.getKey());
					}
				}
			}
			
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) instruction;
					if (ij.section != entry.getKey()) {
						requiredSections.add(ij.section);
					}
				}
			}
		}
		
		for (int s : possibleDeadSections) {
			if (!requiredSections.contains(s)) {
				flag = true;
				List<Instruction> section = routine.textSectionMap.get(s);
				for (int i = 0; i < section.size(); ++i) {
					section.set(i, new InstructionNoOp());
				}
			}
		}
		return flag;
	}
	
	/** A null succeedingData means do not replace succeeding instruction! */
	public static class ImmediateReplacementInfo {
		
		public final Instruction instruction, succeedingData;
		
		public ImmediateReplacementInfo(Instruction instruction, Instruction succeedingData) {
			this.instruction = instruction;
			this.succeedingData = succeedingData;
		}
	}
	
	public static boolean simplifyImmediateInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof IInstructionImmediate) {
					ImmediateReplacementInfo info = ((IInstructionImmediate) instruction).getImmediateReplacementInfo();
					if (info != null && !info.instruction.equals(instruction)) {
						flag = true;
						section.set(i, info.instruction);
						if (info.succeedingData != null) {
							section.set(i + 1, info.succeedingData);
						}
					}
				}
			}
		}
		return flag;
	}
	
	private static void replaceWithNoOp(List<Instruction> section, Instruction instruction) {
		section.set(section.indexOf(instruction), new InstructionNoOp());
	}
	
	public static boolean removeUnnecessaryLoads(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			Instruction removableLoad = null;
			Short loadedImmediate = null;
			Set<LowDataInfo> loadedData = new HashSet<>();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction.isCurrentRegisterValueModified()) {
					if (instruction instanceof IInstructionLoad) {
						if (removableLoad != null) {
							flag = true;
							replaceWithNoOp(section, removableLoad);
							break;
						}
						else if (instruction instanceof IInstructionLoadImmediate) {
							IInstructionLoadImmediate load = (IInstructionLoadImmediate) instruction;
							if (loadedImmediate != null && loadedImmediate.equals(load.getLoadedValue())) {
								flag = true;
								section.set(i, new InstructionNoOp());
								break;
							}
							else {
								loadedImmediate = load.getLoadedValue();
								loadedData.clear();
							}
						}
						else if (instruction instanceof IInstructionLoadAddress) {
							IInstructionLoadAddress load = (IInstructionLoadAddress) instruction;
							if (loadedData.contains(load.getLoadedData())) {
								flag = true;
								section.set(i, new InstructionNoOp());
								break;
							}
							else {
								loadedImmediate = null;
								loadedData.clear();
								loadedData.add(load.getLoadedData());
							}
						}
						removableLoad = instruction;
					}
					else {
						removableLoad = null;
						loadedImmediate = null;
						loadedData.clear();
					}
				}
				else if (instruction.isCurrentRegisterValueUsed()) {
					if (instruction instanceof IInstructionStoreAddress) {
						IInstructionStoreAddress store = (IInstructionStoreAddress) instruction;
						loadedData.add(store.getStoredData());
					}
					removableLoad = null;
				}
			}
		}
		return flag;
	}
	
	private static class InstructionSectionPair extends Pair<Instruction, Integer> {
		
		public InstructionSectionPair(Instruction instruction, int section) {
			super(instruction, section);
		}
	}
	
	public static boolean removeUnnecessaryStores(RedstoneRoutine routine) {
		boolean flag = false;
		Map<LowDataInfo, InstructionSectionPair> removableStoreMap = new HashMap<>();
		Set<LowDataInfo> requiredStoreData = new HashSet<>();
		for (Entry<Integer, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof IInstructionStoreAddress) {
					IInstructionStoreAddress store = (IInstructionStoreAddress) instruction;
					LowDataInfo data = store.getStoredData();
					if (data.isStackData() && !requiredStoreData.contains(data)) {
						removableStoreMap.put(data, new InstructionSectionPair(instruction, entry.getKey()));
					}
				}
				else if (instruction instanceof IInstructionAddress) {
					IInstructionAddress instructionAddress = (IInstructionAddress) instruction;
					if (removableStoreMap.containsKey(instructionAddress.getDataInfo())) {
						removableStoreMap.remove(instructionAddress.getDataInfo());
						requiredStoreData.add(instructionAddress.getDataInfo());
					}
				}
			}
		}
		
		for (InstructionSectionPair removableStore : removableStoreMap.values()) {
			flag = true;
			replaceWithNoOp(routine.textSectionMap.get(removableStore.right), removableStore.left);
		}
		
		for (Entry<Integer, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			IInstructionStoreAddress removableStore = null;
			int removableStoreIndex = 0;
			Set<LowDataInfo> loadedData = new HashSet<>();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction.isCurrentRegisterValueModified()) {
					loadedData.clear();
					if (instruction instanceof IInstructionLoad) {
						if (instruction instanceof IInstructionLoadAddress) {
							IInstructionLoadAddress load = (IInstructionLoadAddress) instruction;
							if (removableStore != null && !loadedData.contains(removableStore.getStoredData()) && removableStore.getStoredData().equalsOther(load.getLoadedData(), true)) {
								removableStore = null;
							}
							loadedData.add(load.getLoadedData());
						}
					}
					else {
						removableStore = null;
					}
				}
				else if (instruction.isCurrentRegisterValueUsed()) {
					if (instruction instanceof IInstructionStoreAddress) {
						IInstructionStoreAddress store = (IInstructionStoreAddress) instruction;
						if (removableStore != null && removableStore.getStoredData().equalsOther(store.getStoredData(), true)) {
							flag = true;
							replaceWithNoOp(section, (Instruction) removableStore);
							break;
						}
						else if (loadedData.contains(store.getStoredData())) {
							flag = true;
							section.set(i, new InstructionNoOp());
							break;
						}
						else {
							removableStore = store;
							removableStoreIndex = i;
							loadedData.add(store.getStoredData());
						}
					}
					else {
						if (removableStore != null && !loadedData.contains(removableStore.getStoredData())) {
							removableStore = null;
						}
					}
				}
			}
			
			if (!flag && removableStore != null && routine.isStackRoutine() && removableStore.getStoredData().isStackData()) {
				Boolean necessaryRoutineEndStore = null;
				int endSection = routine.getFinalTextSectionKey();
				for (int i = 0; i < section.size(); ++i) {
					Instruction instruction = section.get(i);
					if (necessaryRoutineEndStore == null) {
						if (instruction instanceof InstructionJump) {
							InstructionJump ij = (InstructionJump) instruction;
							if (ij.isDefiniteJump()) {
								if (ij.section == endSection) {
									necessaryRoutineEndStore = false;
									break;
								}
							}
							else {
								if (ij.section != endSection && removableStoreIndex < i) {
									necessaryRoutineEndStore = true;
									break;
								}
							}
						}
					}
				}
				if ((necessaryRoutineEndStore == null && entry.getKey() == endSection - 1) || !necessaryRoutineEndStore) {
					flag = true;
					replaceWithNoOp(section, (Instruction) removableStore);
					break;
				}
			}
		}
		return flag;
	}
	
	private static class LoadStoreInfo {
		
		@SuppressWarnings("unused")
		public boolean used = false, stored = false;
	}
	
	private static class SectionIndexPair extends Pair<Integer, Integer> {
		
		public SectionIndexPair(int section, int index) {
			super(section, index);
		}
	}
	
	public static boolean removeUnusedTemporaryData(RedstoneRoutine routine) {
		boolean flag = false;
		Map<LowDataInfo, LoadStoreInfo> loadStoreMap = new HashMap<>();
		Map<LowDataInfo, Set<SectionIndexPair>> sectionIndexMap = new HashMap<>();
		for (Entry<Integer, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof IInstructionAddress) {
					IInstructionAddress isa = (IInstructionAddress) instruction;
					LowDataInfo info = isa.getDataInfo();
					if (info.isTempData()) {
						if (!loadStoreMap.containsKey(info)) {
							loadStoreMap.put(info, new LoadStoreInfo());
						}
						if (!sectionIndexMap.containsKey(info)) {
							sectionIndexMap.put(info, new HashSet<>());
						}
						sectionIndexMap.get(info).add(new SectionIndexPair(entry.getKey(), i));
						
						loadStoreMap.get(info).used = isa.isDataFromMemory();
						loadStoreMap.get(info).stored = isa.isDataToMemory();
					}
				}
			}
		}
		
		Set<Integer> sectionSet = new HashSet<>();
		for (Entry<LowDataInfo, LoadStoreInfo> entry : loadStoreMap.entrySet()) {
			if (!entry.getValue().used) {
				Set<SectionIndexPair> set = sectionIndexMap.get(entry.getKey());
				for (SectionIndexPair pair : set) {
					if (!sectionSet.contains(pair.left)) {
						flag = true;
						routine.textSectionMap.get(pair.left).set(pair.right, new InstructionNoOp());
						sectionSet.add(pair.left);
					}
					break;
				}
			}
		}
		
		return flag;
	}
	
	public static boolean removeUnnecessaryJumps(RedstoneRoutine routine) {
		boolean flag = false;
		for (Entry<Integer, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) instruction;
					if (i == section.size() - 1 && ij.section == entry.getKey() + 1) {
						flag = true;
						section.set(i, new InstructionNoOp());
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyConditionalJumps(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size() - 1; ++i) {
				Instruction instruction = section.get(i), next = section.get(i + 1);
				if (next instanceof InstructionConditionalJump) {
					InstructionConditionalJump icj = (InstructionConditionalJump) next;
					Instruction replacement = icj.getReplacementConditionalJump(instruction);
					if (replacement != null) {
						flag = true;
						section.set(i + 1, replacement);
						section.set(i, new InstructionNoOp());
					}
				}
			}
		}
		return flag;
	}
	
	/** Ignores code sectioning! */
	public static boolean compressSuccessiveInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		for (Entry<Integer, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); ++i) {
				flag |= compressWithNextInstruction(routine.textSectionMap, entry.getKey(), i, false);
			}
		}
		return flag;
	}
	
	/** Ignores code sectioning! */
	public static boolean compressWithNextInstruction(Map<Integer, List<Instruction>> sectionMap, int s, int i, boolean ignoreSections) {
		if (ignoreSections && i == sectionMap.get(s).size() - 1 && s < sectionMap.size() - 1 && !sectionMap.get(s + 1).isEmpty()) {
			if (compressWithNextInstructionInternal(sectionMap, s, i, (short) (s + 1), 0)) {
				return true;
			}
		}
		else if (i < sectionMap.get(s).size() - 1) {
			if (compressWithNextInstructionInternal(sectionMap, s, i, s, i + 1)) {
				return true;
			}
		}
		return false;
	}
	
	/** Ignores code sectioning! */
	private static boolean compressWithNextInstructionInternal(Map<Integer, List<Instruction>> sectionMap, int s, int i, int t, int j) {
		Instruction replacement = sectionMap.get(s).get(i).getCompressedWithNextInstruction(sectionMap.get(t).get(j), s == t);
		if (replacement != null) {
			sectionMap.get(s).set(i, replacement);
			sectionMap.get(t).set(j, new InstructionNoOp());
			return true;
		}
		else {
			return false;
		}
	}
}
