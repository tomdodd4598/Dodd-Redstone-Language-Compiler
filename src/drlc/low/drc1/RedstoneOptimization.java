package drlc.low.drc1;

import java.util.*;
import java.util.Map.Entry;

import drlc.Helpers.Pair;
import drlc.low.LowDataInfo;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.*;
import drlc.low.instruction.IInstructionLoad;
import drlc.low.instruction.address.*;

public class RedstoneOptimization {
	
	public static boolean removeNoOps(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				if (section.get(i) instanceof InstructionNoOp) {
					flag = true;
					section.remove(i);
				}
			}
		}
		return flag;
	}
	
	public static boolean removeDeadInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		Set<Integer> possibleDeadSections = new HashSet<>(), requiredSections = new HashSet<>();
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue(), previous = routine.sectionTextMap.get(entry.getKey() - 1);
			if (previous != null && !previous.isEmpty() && !section.isEmpty()) {
				Instruction previousInstruction = previous.get(previous.size() - 1);
				if (previousInstruction instanceof InstructionJump ij) {
					if (ij.isDefiniteJump()) {
						possibleDeadSections.add(entry.getKey());
					}
				}
			}
			
			for (Instruction instruction : section) {
				if (instruction instanceof InstructionJump ij) {
					if (ij.section != entry.getKey()) {
						requiredSections.add(ij.section);
					}
				}
			}
		}
		
		for (int s : possibleDeadSections) {
			if (!requiredSections.contains(s)) {
				flag = true;
				List<Instruction> section = routine.sectionTextMap.get(s);
				for (int i = 0; i < section.size(); ++i) {
					section.set(i, new InstructionNoOp());
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyImmediateInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionImmediate immediate) {
					Instruction replacement = immediate.getImmediateReplacement();
					if (replacement != null && !replacement.equals(instruction)) {
						flag = true;
						section.set(i, replacement);
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
		for (List<Instruction> section : routine.sectionTextMap.values()) {
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
						else if (instruction instanceof InstructionLoadImmediate load) {
							if (loadedImmediate != null && loadedImmediate.equals(load.getRegisterValue())) {
								flag = true;
								section.set(i, new InstructionNoOp());
								break;
							}
							else {
								loadedImmediate = load.getRegisterValue();
								loadedData.clear();
							}
						}
						else if (instruction instanceof IInstructionLoadAddress load) {
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
					if (instruction instanceof IInstructionStoreAddress store) {
						loadedData.add(store.getStoredData());
					}
					removableLoad = null;
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryStores(RedstoneRoutine routine) {
		boolean flag = false;
		Map<LowDataInfo, Pair<Instruction, Integer>> removableStoreMap = new HashMap<>();
		Set<LowDataInfo> requiredStoreData = new HashSet<>();
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			for (Instruction instruction : entry.getValue()) {
				if (instruction instanceof IInstructionStoreAddress store) {
					LowDataInfo data = store.getStoredData();
					if (data.span.size <= 1 && data.isStackData() && !requiredStoreData.contains(data)) {
						removableStoreMap.put(data, new Pair<>(instruction, entry.getKey()));
					}
				}
				else if (instruction instanceof IInstructionAddress instructionAddress) {
					if (removableStoreMap.containsKey(instructionAddress.getDataInfo())) {
						removableStoreMap.remove(instructionAddress.getDataInfo());
						requiredStoreData.add(instructionAddress.getDataInfo());
					}
				}
			}
		}
		
		for (Pair<Instruction, Integer> removableStore : removableStoreMap.values()) {
			flag = true;
			replaceWithNoOp(routine.sectionTextMap.get(removableStore.right), removableStore.left);
		}
		
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			IInstructionStoreAddress removableStore = null;
			int removableStoreIndex = 0;
			Set<LowDataInfo> loadedData = new HashSet<>();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction.isCurrentRegisterValueModified()) {
					loadedData.clear();
					if (instruction instanceof IInstructionLoad) {
						if (instruction instanceof IInstructionLoadAddress load) {
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
					if (instruction instanceof IInstructionStoreAddress store) {
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
					if (section.get(i) instanceof InstructionJump ij) {
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
				if ((necessaryRoutineEndStore == null && entry.getKey() == endSection - 1) || (necessaryRoutineEndStore != null && !necessaryRoutineEndStore)) {
					flag = true;
					replaceWithNoOp(section, (Instruction) removableStore);
					break;
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnusedTemporaryData(RedstoneRoutine routine) {
		boolean flag = false;
		Map<LowDataInfo, boolean[]> loadStoreMap = new HashMap<>();
		Map<LowDataInfo, Set<int[]>> sectionIndexMap = new HashMap<>();
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				if (section.get(i) instanceof IInstructionAddress instructionAddress) {
					LowDataInfo info = instructionAddress.getDataInfo();
					if (info.span.size <= 1 && info.isTempData()) {
						if (!loadStoreMap.containsKey(info)) {
							loadStoreMap.put(info, new boolean[] {false, false});
						}
						boolean[] loadStore = loadStoreMap.get(info);
						loadStore[0] |= instructionAddress.isDataFromMemory();
						loadStore[1] |= instructionAddress.isDataToMemory();
						if (!sectionIndexMap.containsKey(info)) {
							sectionIndexMap.put(info, new HashSet<>());
						}
						sectionIndexMap.get(info).add(new int[] {entry.getKey(), i});
					}
				}
			}
		}
		
		Set<Integer> sectionSet = new HashSet<>();
		for (Entry<LowDataInfo, boolean[]> entry : loadStoreMap.entrySet()) {
			boolean[] loadStore = entry.getValue();
			if (!loadStore[0] || !loadStore[1]) {
				Set<int[]> set = sectionIndexMap.get(entry.getKey());
				for (int[] fullIndex : set) {
					int sectionIndex = fullIndex[0];
					if (!sectionSet.contains(sectionIndex)) {
						flag = true;
						routine.sectionTextMap.get(sectionIndex).set(fullIndex[1], new InstructionNoOp());
						sectionSet.add(sectionIndex);
					}
					break;
				}
			}
		}
		
		return flag;
	}
	
	public static boolean removeUnnecessaryJumps(RedstoneRoutine routine) {
		boolean flag = false;
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				if (section.get(i) instanceof InstructionJump ij) {
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
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			for (int i = 1; i < section.size(); ++i) {
				if (section.get(i) instanceof InstructionConditionalJump icj) {
					Instruction replacement = icj.getReplacementConditionalJump(section.get(i - 1));
					if (replacement != null) {
						flag = true;
						section.set(i - 1, new InstructionNoOp());
						section.set(i, replacement);
					}
				}
			}
		}
		return flag;
	}
	
	/** Ignores code sectioning! */
	public static boolean compressSuccessiveInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); ++i) {
				flag |= compressWithNextInstruction(routine.sectionTextMap, entry.getKey(), i, false);
			}
		}
		return flag;
	}
	
	/** Ignores code sectioning! */
	public static boolean compressWithNextInstruction(Map<Integer, List<Instruction>> sectionMap, int sectionIndex, int instructionIndex, boolean ignoreSections) {
		if (ignoreSections && instructionIndex == sectionMap.get(sectionIndex).size() - 1 && sectionIndex < sectionMap.size() - 1 && !sectionMap.get(sectionIndex + 1).isEmpty()) {
			if (compressWithNextInstructionInternal(sectionMap, sectionIndex, instructionIndex, sectionIndex + 1, 0)) {
				return true;
			}
		}
		else if (instructionIndex < sectionMap.get(sectionIndex).size() - 1) {
			if (compressWithNextInstructionInternal(sectionMap, sectionIndex, instructionIndex, sectionIndex, instructionIndex + 1)) {
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
