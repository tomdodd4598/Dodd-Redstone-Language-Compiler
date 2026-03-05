package drlc.low.edsac;

import java.util.*;
import java.util.Map.Entry;

import drlc.Helpers.Pair;
import drlc.low.LowDataInfo;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class EdsacOptimization {
	
	public static boolean removeNoOps(EdsacRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			for (int i = section.size() - 1; i >= 0; --i) {
				if (section.get(i) instanceof InstructionNoOp) {
					flag = true;
					section.remove(i);
				}
			}
		}
		return flag;
	}
	
	private static int getControlFlowBarrierIndex(List<Instruction> section, int index) {
		if (index < 0 || index >= section.size()) {
			return -1;
		}
		Instruction instruction = section.get(index);
		if (instruction instanceof InstructionHalt) {
			return index;
		}
		int next = index + 1;
		if (next < section.size()) {
			Instruction nextInstruction = section.get(next);
			if (instruction instanceof InstructionJumpIfLessThanZero jumpIfLessThanZero && nextInstruction instanceof InstructionJumpIfMoreThanOrEqualToZero jumpIfMoreThanOrEqualToZero && jumpIfLessThanZero.section == jumpIfMoreThanOrEqualToZero.section) {
				return next;
			}
			if (instruction instanceof InstructionJumpIfMoreThanOrEqualToZero jumpIfMoreThanOrEqualToZero && nextInstruction instanceof InstructionJumpIfLessThanZero jumpIfLessThanZero && jumpIfMoreThanOrEqualToZero.section == jumpIfLessThanZero.section) {
				return next;
			}
		}
		return -1;
	}
	
	private static boolean isDefiniteSubroutineEnd(List<Instruction> section, int index) {
		int barrierIndex = getControlFlowBarrierIndex(section, index);
		if (barrierIndex < 0) {
			return false;
		}
		Instruction instruction = section.get(barrierIndex);
		return instruction instanceof InstructionHalt || (instruction instanceof InstructionJump jump && jump.section < 0);
	}
	
	public static boolean removeDeadInstructions(EdsacRoutine routine) {
		boolean flag = false;
		Set<Integer> potentialDeadSections = new LinkedHashSet<>(), requiredSections = new LinkedHashSet<>(), protectedSections = new HashSet<>();
		
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (Instruction instruction : section) {
				if (instruction.isProtected()) {
					protectedSections.add(entry.getKey());
				}
				if (instruction instanceof InstructionJump jump && jump.section != entry.getKey()) {
					requiredSections.add(jump.section);
				}
			}
			
			List<Instruction> previous = routine.sectionTextMap.get(entry.getKey() - 1);
			if (previous != null && !previous.isEmpty() && !section.isEmpty()) {
				int previousLast = previous.size() - 1;
				boolean previousHasDefiniteEnd = isDefiniteSubroutineEnd(previous, previousLast);
				if (!previousHasDefiniteEnd) {
					int previousSecondLast = previousLast - 1;
					previousHasDefiniteEnd = previousSecondLast >= 0 && getControlFlowBarrierIndex(previous, previousSecondLast) == previousLast && isDefiniteSubroutineEnd(previous, previousSecondLast);
				}
				if (previousHasDefiniteEnd) {
					potentialDeadSections.add(entry.getKey());
				}
			}
		}
		
		for (int s : potentialDeadSections) {
			if (s < 0 || protectedSections.contains(s) || requiredSections.contains(s)) {
				continue;
			}
			List<Instruction> section = routine.sectionTextMap.get(s);
			for (int i = 0; i < section.size(); ++i) {
				section.set(i, new InstructionNoOp());
				flag = true;
			}
		}
		
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			int sectionKey = entry.getKey();
			if (sectionKey < 0) {
				continue;
			}
			List<Instruction> section = entry.getValue();
			int lastProtected = -1;
			for (int i = section.size() - 1; i >= 0; --i) {
				if (section.get(i).isProtected()) {
					lastProtected = i;
					break;
				}
			}
			int unconditionalIndex = -1;
			for (int i = 0; i < section.size(); ++i) {
				int endIndex = getControlFlowBarrierIndex(section, i);
				if (endIndex >= 0) {
					unconditionalIndex = endIndex;
					break;
				}
			}
			if (unconditionalIndex >= 0 && (lastProtected < 0 || unconditionalIndex >= lastProtected)) {
				for (int i = unconditionalIndex + 1; i < section.size(); ++i) {
					section.set(i, new InstructionNoOp());
					flag = true;
				}
			}
		}
		
		return flag;
	}
	
	public static boolean simplifyImmediateInstructions(EdsacRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				Instruction current = section.get(i);
				Instruction simplified = current.getReplacement(routine);
				if (simplified != null) {
					section.set(i, simplified);
					flag = true;
				}
			}
		}
		return flag;
	}
	
	private static Pair<Integer, Pair<LowDataInfo, Boolean>> clearLoadMatch(List<Instruction> section, int index) {
		if (index < 0 || index >= section.size() || !(section.get(index) instanceof InstructionClearAccumulator)) {
			return null;
		}
		int loadIndex = index + 1;
		if (loadIndex >= section.size()) {
			return null;
		}
		Instruction load = section.get(loadIndex);
		LowDataInfo loadData = load.getReadDataInfo();
		if (loadData == null) {
			return null;
		}
		Boolean loadNegated = null;
		if (load instanceof InstructionAdd) {
			loadNegated = false;
		}
		else if (load instanceof InstructionSubtract) {
			loadNegated = true;
		}
		if (loadNegated == null) {
			return null;
		}
		return new Pair<>(loadIndex, new Pair<>(loadData, loadNegated));
	}
	
	public static boolean removeUnnecessaryLoads(EdsacRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			int[] lastLoadIndices = null;
			boolean accumulatorUsed = false;
			Integer lastClearIndex = null;
			
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (getControlFlowBarrierIndex(section, i) > i) {
					lastLoadIndices = null;
					accumulatorUsed = false;
					lastClearIndex = null;
				}
				if (instruction instanceof InstructionClearAccumulator) {
					if (!accumulatorUsed && lastLoadIndices != null) {
						for (int idx : lastLoadIndices) {
							section.set(idx, new InstructionNoOp());
						}
						flag = true;
					}
					if (!accumulatorUsed && lastLoadIndices == null && lastClearIndex != null) {
						section.set(lastClearIndex, new InstructionNoOp());
						flag = true;
					}
					lastLoadIndices = null;
					accumulatorUsed = false;
					lastClearIndex = i;
				}
				if (instruction.isLoadStoreBarrier()) {
					lastLoadIndices = null;
					accumulatorUsed = false;
					lastClearIndex = null;
					continue;
				}
				
				Pair<Integer, Pair<LowDataInfo, Boolean>> loadMatch = clearLoadMatch(section, i);
				if (loadMatch != null) {
					int loadIndex = loadMatch.left;
					if (lastClearIndex != null && lastClearIndex == i) {
						lastClearIndex = null;
					}
					boolean deadLoad = false;
					for (int nextIndex = loadIndex + 1; nextIndex < section.size(); ++nextIndex) {
						Instruction next = section.get(nextIndex);
						if (getControlFlowBarrierIndex(section, nextIndex) >= 0) {
							break;
						}
						if (next instanceof InstructionClearAccumulator) {
							deadLoad = true;
							break;
						}
						if (next.isLoadStoreBarrier() || next.isAccumulatorUsed() || (next.isAccumulatorModified() && !(next instanceof InstructionClearAccumulator))) {
							break;
						}
					}
					if (deadLoad) {
						section.set(i, new InstructionNoOp());
						section.set(loadIndex, new InstructionNoOp());
						flag = true;
						i = loadIndex;
						continue;
					}
					if (!accumulatorUsed && lastLoadIndices != null) {
						for (int idx : lastLoadIndices) {
							section.set(idx, new InstructionNoOp());
						}
						flag = true;
					}
					lastLoadIndices = new int[] {i, loadIndex};
					accumulatorUsed = false;
					i = loadIndex;
					continue;
				}
				
				if (instruction.isAccumulatorModified() || instruction.isAccumulatorUsed()) {
					accumulatorUsed = true;
					lastLoadIndices = null;
					lastClearIndex = null;
				}
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryStores(EdsacRoutine routine) {
		boolean flag = false;
		
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			int removableStoreIndex = -1;
			LowDataInfo removableStoreData = null;
			Set<LowDataInfo> loadedData = new HashSet<>();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction.isLoadStoreBarrier() || instruction.isUnknownMemoryAccess()) {
					removableStoreIndex = -1;
					removableStoreData = null;
					loadedData.clear();
					continue;
				}
				
				Pair<Integer, Pair<LowDataInfo, Boolean>> loadMatch = clearLoadMatch(section, i);
				if (loadMatch != null) {
					int loadIndex = loadMatch.left;
					Pair<LowDataInfo, Boolean> loadInfo = loadMatch.right;
					removableStoreIndex = -1;
					removableStoreData = null;
					loadedData.clear();
					if (!loadInfo.right) {
						loadedData.add(loadInfo.left);
					}
					i = loadIndex;
					continue;
				}
				
				LowDataInfo writtenData = instruction.getWriteDataInfo();
				if (writtenData != null) {
					if (removableStoreIndex >= 0 && removableStoreData != null && removableStoreData.equalsOther(writtenData, true)) {
						Instruction removableStoreInstruction = section.get(removableStoreIndex);
						if (!(removableStoreInstruction instanceof InstructionClearAccumulator)) {
							if (removableStoreInstruction.isAccumulatorCleared()) {
								section.set(removableStoreIndex, new InstructionClearAccumulator());
							}
							else {
								section.set(removableStoreIndex, new InstructionNoOp());
							}
							flag = true;
						}
						removableStoreIndex = i;
						removableStoreData = writtenData;
						loadedData.add(writtenData);
					}
					else if (loadedData.contains(writtenData)) {
						if (!(instruction instanceof InstructionClearAccumulator)) {
							if (instruction.isAccumulatorCleared()) {
								section.set(i, new InstructionClearAccumulator());
							}
							else {
								section.set(i, new InstructionNoOp());
							}
							flag = true;
						}
					}
					else {
						removableStoreIndex = i;
						removableStoreData = writtenData;
						loadedData.add(writtenData);
					}
					if (instruction.isAccumulatorCleared()) {
						loadedData.clear();
					}
					continue;
				}
				
				if (instruction.isAccumulatorModified()) {
					removableStoreIndex = -1;
					removableStoreData = null;
					loadedData.clear();
				}
				else if (instruction.isAccumulatorUsed()) {
					if (removableStoreIndex >= 0 && removableStoreData != null && !loadedData.contains(removableStoreData)) {
						removableStoreIndex = -1;
						removableStoreData = null;
					}
				}
			}
		}
		
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			Map<LowDataInfo, Integer> lastPrivateStoreMap = new HashMap<>();
			for (int i = 0; i < section.size(); ++i) {
				Instruction instruction = section.get(i);
				if (instruction.isLoadStoreBarrier() || instruction.isUnknownMemoryAccess()) {
					lastPrivateStoreMap.clear();
					continue;
				}
				LowDataInfo readData = instruction.getReadDataInfo();
				if (readData != null && routine.isPrivateDataInfo(readData) && !readData.isTempData()) {
					lastPrivateStoreMap.remove(readData);
				}
				LowDataInfo writtenData = instruction.getWriteDataInfo();
				if (writtenData != null && routine.isPrivateDataInfo(writtenData) && !writtenData.isTempData()) {
					Integer lastPrivateStoreIndex = lastPrivateStoreMap.get(writtenData);
					if (lastPrivateStoreIndex != null) {
						Instruction lastPrivateStoreInstruction = section.get(lastPrivateStoreIndex);
						if (!(lastPrivateStoreInstruction instanceof InstructionClearAccumulator)) {
							if (lastPrivateStoreInstruction.isAccumulatorCleared()) {
								section.set(lastPrivateStoreIndex, new InstructionClearAccumulator());
							}
							else {
								section.set(lastPrivateStoreIndex, new InstructionNoOp());
							}
							flag = true;
						}
					}
					lastPrivateStoreMap.put(writtenData, i);
				}
			}
		}
		
		return flag;
	}
	
	public static boolean removeUnusedTemporaryData(EdsacRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			int regionStart = 0;
			for (int i = 0; i <= section.size(); ++i) {
				boolean fence = i >= section.size();
				if (!fence) {
					Instruction instruction = section.get(i);
					fence = instruction.isLoadStoreBarrier() || instruction.isUnknownMemoryAccess();
				}
				if (!fence) {
					continue;
				}
				if (regionStart < i) {
					boolean isEndingRegion = i < section.size() && isDefiniteSubroutineEnd(section, i);
					if (!isEndingRegion) {
						int last = i - 1;
						if (last >= regionStart) {
							isEndingRegion = isDefiniteSubroutineEnd(section, last);
							if (!isEndingRegion) {
								int secondLast = last - 1;
								isEndingRegion = secondLast >= regionStart && getControlFlowBarrierIndex(section, secondLast) == last && isDefiniteSubroutineEnd(section, secondLast);
							}
						}
					}
					
					Map<LowDataInfo, Integer> lastUnreadWriteMap = new LinkedHashMap<>();
					for (int j = regionStart; j < i; ++j) {
						Instruction instruction = section.get(j);
						LowDataInfo readData = instruction.getReadDataInfo();
						if (readData != null && readData.span.size <= 1 && readData.isTempData()) {
							lastUnreadWriteMap.remove(readData);
						}
						
						LowDataInfo writtenData = instruction.getWriteDataInfo();
						if (writtenData != null && writtenData.span.size <= 1 && writtenData.isTempData()) {
							Integer previousWriteIndex = lastUnreadWriteMap.put(writtenData, j);
							if (previousWriteIndex != null) {
								Instruction previousWriteInstruction = section.get(previousWriteIndex);
								if (!(previousWriteInstruction instanceof InstructionClearAccumulator)) {
									if (previousWriteInstruction.isAccumulatorCleared()) {
										section.set(previousWriteIndex, new InstructionClearAccumulator());
									}
									else {
										section.set(previousWriteIndex, new InstructionNoOp());
									}
									flag = true;
								}
							}
						}
					}
					
					if (isEndingRegion) {
						for (int writeIndex : lastUnreadWriteMap.values()) {
							Instruction writeInstruction = section.get(writeIndex);
							if (!(writeInstruction instanceof InstructionClearAccumulator)) {
								if (writeInstruction.isAccumulatorCleared()) {
									section.set(writeIndex, new InstructionClearAccumulator());
								}
								else {
									section.set(writeIndex, new InstructionNoOp());
								}
								flag = true;
							}
						}
					}
				}
				regionStart = i + 1;
			}
		}
		return flag;
	}
	
	public static boolean removeUnnecessaryJumps(EdsacRoutine routine) {
		boolean flag = false;
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			int last = section.size() - 1;
			int secondLast = last - 1;
			if (secondLast >= 0) {
				boolean pairToNextSection = getControlFlowBarrierIndex(section, secondLast) == last && section.get(last) instanceof InstructionJump jump && jump.section == entry.getKey() + 1;
				if (pairToNextSection) {
					section.set(secondLast, new InstructionNoOp());
					section.set(last, new InstructionNoOp());
					flag = true;
				}
			}
		}
		return flag;
	}
	
	public static boolean simplifyConditionalJumps(EdsacRoutine routine) {
		boolean flag = false;
		for (List<Instruction> section : routine.sectionTextMap.values()) {
			for (int i = 0; i < section.size(); ++i) {
				Instruction current = section.get(i);
				if (!(current instanceof InstructionJumpIfLessThanZero) && !(current instanceof InstructionJumpIfMoreThanOrEqualToZero)) {
					continue;
				}
				Instruction previous = i <= 0 ? null : section.get(i - 1);
				Instruction previousPrevious = i <= 1 ? null : section.get(i - 2);
				
				Integer accumulatorSign = null;
				if (previous != null) {
					if (previous instanceof InstructionClearAccumulator) {
						accumulatorSign = 0;
					}
					else if (previousPrevious != null && previousPrevious instanceof InstructionClearAccumulator) {
						LowDataInfo loadedInfo = previous.getReadDataInfo();
						if (loadedInfo != null && (previous instanceof InstructionAdd || previous instanceof InstructionSubtract)) {
							Long value = routine.scalarValue(loadedInfo);
							if (value != null) {
								EdsacInt accumulatorValue = EdsacInt.of(value);
								if (previous instanceof InstructionSubtract) {
									accumulatorValue = accumulatorValue.minus();
								}
								accumulatorSign = Long.signum(accumulatorValue.toSigned());
							}
						}
					}
				}
				
				if (accumulatorSign == null) {
					continue;
				}
				
				boolean jumpTaken = current instanceof InstructionJumpIfLessThanZero ? accumulatorSign < 0 : accumulatorSign >= 0;
				if (!jumpTaken) {
					section.set(i, new InstructionNoOp());
					flag = true;
				}
			}
		}
		return flag;
	}
	
	public static boolean compressSuccessiveInstructions(EdsacRoutine routine) {
		boolean flag = false;
		for (Entry<Integer, List<Instruction>> entry : routine.sectionTextMap.entrySet()) {
			List<Instruction> section = entry.getValue();
			for (int i = 0; i < section.size(); ++i) {
				Instruction current = section.get(i);
				if (current.isProtected()) {
					continue;
				}
				int nextIndex = i + 1;
				if (nextIndex >= section.size()) {
					break;
				}
				Instruction next = section.get(nextIndex);
				if (next.isProtected()) {
					continue;
				}
				if (next instanceof InstructionClearAccumulator && current.isAccumulatorCleared()) {
					section.set(nextIndex, new InstructionNoOp());
					flag = true;
					continue;
				}
				Instruction replacement = current.getCompressedWithNextInstruction(routine, next, true);
				if (replacement != null) {
					section.set(i, replacement);
					section.set(nextIndex, new InstructionNoOp());
					flag = true;
				}
			}
		}
		return flag;
	}
}
