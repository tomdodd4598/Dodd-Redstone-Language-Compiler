package drlc.generate.drc1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import drlc.Helper.Pair;
import drlc.generate.drc1.instruction.IInstructionLoad;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.InstructionConstant;
import drlc.generate.drc1.instruction.InstructionNoOp;
import drlc.generate.drc1.instruction.address.IInstructionAddress;
import drlc.generate.drc1.instruction.address.IInstructionLoadAddress;
import drlc.generate.drc1.instruction.address.IInstructionStoreAddress;
import drlc.generate.drc1.instruction.immediate.IInstructionImmediate;
import drlc.generate.drc1.instruction.immediate.IInstructionLoadImmediate;
import drlc.generate.drc1.instruction.immediate.InstructionALUImmediate;
import drlc.generate.drc1.instruction.immediate.InstructionALULongImmediate;
import drlc.generate.drc1.instruction.jump.InstructionConditionalJump;
import drlc.generate.drc1.instruction.jump.InstructionJump;
import drlc.generate.drc1.instruction.set.InstructionSet;

public class RedstoneOptimization {
	
	public static boolean removeNoOps(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); i++) {
				if (section.get(i) instanceof InstructionNoOp) {
					flag = true;
					section.remove(i);
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryImmediates(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction instanceof IInstructionImmediate) {
					IInstructionImmediate immediate = (IInstructionImmediate) instruction;
					if (immediate.isUnnecessaryImmediate()) {
						flag = true;
						section.remove(i);
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryConstants(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction.precedesData()) {
					if (i == section.size() - 1 || !(section.get(i + 1) instanceof InstructionConstant)) {
						throw new IllegalArgumentException(String.format("Found unexpected instruction %s not preceding constant as required!", instruction));
					}
				}
				else {
					if (i < section.size() - 1 && section.get(i + 1) instanceof InstructionConstant) {
						flag = true;
						section.remove(i + 1);
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryLoads(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			Instruction removableLoad = null;
			Short loadedImmediate = null;
			final Set<DataInfo> loadedData = new HashSet<>();
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction.isRegisterModified()) {
					if (instruction instanceof IInstructionLoad) {
						if (removableLoad != null) {
							flag = true;
							section.remove(removableLoad);
							break;
						}
						else if (instruction instanceof IInstructionLoadImmediate) {
							IInstructionLoadImmediate load = (IInstructionLoadImmediate) instruction;
							if (loadedImmediate != null && loadedImmediate.equals(load.getLoadedValue())) {
								flag = true;
								section.remove(i);
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
								section.remove(i);
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
				else if (instruction.isRegisterExported()) {
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
	
	static class InstructionSectionPair extends Pair<Instruction, Short> {
		public InstructionSectionPair(Instruction instruction, short section) {
			super(instruction, section);
		}
	}
	
	public static boolean removeUnnecessaryStores(RedstoneRoutine routine) {
		boolean flag = false;
		if (routine.isRecursive()) {
			Map<DataInfo, InstructionSectionPair> removableStoreMap = new HashMap<>();
			Set<DataInfo> requiredStoreData = new HashSet<>();
			for (Entry<Short, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
				List<Instruction> section = entry.getValue();
				for (int i = 0; i < section.size(); i++) {
					Instruction instruction = section.get(i);
					if (instruction instanceof IInstructionStoreAddress) {
						IInstructionStoreAddress store = (IInstructionStoreAddress) instruction;
						if (!requiredStoreData.contains(store.getStoredData())) {
							removableStoreMap.put(store.getStoredData(), new InstructionSectionPair(instruction, entry.getKey()));
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
				routine.textSectionMap.get(removableStore.right).remove(removableStore.left);
			}
		}
		
		for (Entry<Short, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			IInstructionStoreAddress removableStore = null;
			int removableStoreIndex = 0;
			final Set<DataInfo> loadedData = new HashSet<>();
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction.isRegisterModified()) {
					loadedData.clear();
					if (instruction instanceof IInstructionLoad) {
						if (instruction instanceof IInstructionLoadAddress) {
							IInstructionLoadAddress load = (IInstructionLoadAddress) instruction;
							if (removableStore != null && !loadedData.contains(removableStore.getStoredData()) && removableStore.getStoredData().equals(load.getLoadedData(), true)) {
								removableStore = null;
							}
							loadedData.add(load.getLoadedData());
						}
					}
					else {
						removableStore = null;
					}
				}
				else if (instruction.isRegisterExported()) {
					if (instruction instanceof IInstructionStoreAddress) {
						IInstructionStoreAddress store = (IInstructionStoreAddress) instruction;
						if (removableStore != null && removableStore.getStoredData().equals(store.getStoredData(), true)) {
							flag = true;
							section.remove(removableStore);
							break;
						}
						else if (loadedData.contains(store.getStoredData())) {
							flag = true;
							section.remove(i);
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
			
			if (!flag && removableStore != null && routine.isRecursive()) {
				Boolean necessaryRoutineEndStore = null;
				int endSection = routine.intermediateRoutine.getBodyActionLists().size();
				for (int i = 0; i < section.size(); i++) {
					Instruction instruction = section.get(i);
					if (necessaryRoutineEndStore == null) {
						if (instruction instanceof InstructionJump) {
							InstructionJump ij = (InstructionJump) instruction;
							if (ij instanceof InstructionConditionalJump) {
								if (ij.section != endSection && removableStoreIndex < i) {
									necessaryRoutineEndStore = true;
									break;
								}
							}
							else {
								if (ij.section == endSection) {
									necessaryRoutineEndStore = false;
									break;
								}
							}
						}
					}
				}
				if ((necessaryRoutineEndStore == null && entry.getKey() == endSection - 1) || !necessaryRoutineEndStore) {
					flag = true;
					section.remove(removableStore);
					break;
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryJumps(RedstoneRoutine routine) {
		boolean flag = false;
		for (Entry<Short, List<Instruction>> entry : routine.textSectionMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionJump) {
					InstructionJump ij = (InstructionJump) instruction;
					if (i == section.size() - 1 && ij.section == entry.getKey() + 1) {
						flag = true;
						section.remove(i);
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyConditionalJumps(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size() - 1; i++) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionSet) {
					Instruction next = section.get(i + 1);
					if (next instanceof InstructionConditionalJump) {
						InstructionConditionalJump icj = (InstructionConditionalJump) next;
						Instruction replacement = icj.getReplacementConditionalJump((InstructionSet) instruction);
						if (replacement != null) {
							flag = true;
							section.set(i + 1, replacement);
							section.remove(i);
						}
					}
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyALUImmediateInstructions(RedstoneRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.textSectionMap.values()) {
			for (int i = 0; i < section.size(); i++) {
				Instruction instruction = section.get(i);
				if (instruction instanceof InstructionALUImmediate) {
					InstructionALUImmediate aluImmediate = (InstructionALUImmediate) instruction;
					Instruction replacement = aluImmediate.getALUImmediateReplacement();
					if (replacement != null && !replacement.equals(aluImmediate)) {
						flag = true;
						section.set(i, replacement);
					}
				}
				else if (instruction instanceof InstructionALULongImmediate) {
					InstructionALULongImmediate aluImmediate = (InstructionALULongImmediate) instruction;
					Instruction replacement = aluImmediate.getALUImmediateReplacement();
					if (replacement != null && !replacement.equals(aluImmediate)) {
						flag = true;
						section.set(i, replacement);
					}
				}
			}
		}
		return flag;
	}
}
