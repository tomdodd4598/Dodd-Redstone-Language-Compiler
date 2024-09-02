package drlc.low;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import drlc.Helpers.Pair;
import drlc.Main;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.data.DataId.LowDataId;
import drlc.intermediate.routine.Routine;
import drlc.low.instruction.LowInstruction;

public abstract class LowCode<CODE extends LowCode<CODE, ROUTINE, INSTRUCTION>, ROUTINE extends LowRoutine<CODE, ROUTINE, INSTRUCTION>, INSTRUCTION extends LowInstruction> {
	
	public final Map<Function, ROUTINE> routineMap = new LinkedHashMap<>();
	
	public final Map<LowDataId, Pair<DataId, LowDataSpan>> rootSpanMap = new LinkedHashMap<>();
	
	public final Map<LowDataSpan, LowAddressSlice> rootAddressMap = new LinkedHashMap<>();
	
	public final Map<Function, Integer> textAddressMap = new LinkedHashMap<>();
	
	public final Map<LowDataInfo, INSTRUCTION> staticDataMap = new LinkedHashMap<>();
	
	protected LowCode() {}
	
	protected abstract ROUTINE createRoutine(Routine intermediateRoutine);
	
	public boolean routineExists(Function function) {
		return routineMap.containsKey(function);
	}
	
	public ROUTINE getRoutine(Function function) {
		return routineMap.get(function);
	}
	
	public ROUTINE addRoutine(Function function, Routine intermediateRoutine) {
		ROUTINE routine = function.builtIn ? getBuiltInRoutine(function.name, intermediateRoutine) : createRoutine(intermediateRoutine);
		routineMap.put(function, routine);
		return routine;
	}
	
	protected void addRoutines() {
		Map<Boolean, List<Entry<Function, Routine>>> partition = Main.rootScope.routineMap.entrySet().stream().collect(Collectors.partitioningBy(e -> e.getKey().builtIn));
		for (boolean builtIn : new boolean[] {false, true}) {
			partition.get(builtIn).forEach(e -> addRoutine(e.getKey(), e.getValue()));
		}
		
		for (Function function : Main.rootScope.routineMap.keySet()) {
			if (!routineMap.containsKey(function)) {
				throw new IllegalArgumentException(String.format("Unexpectedly encountered unimplemented routine \"%s\"!", function));
			}
		}
	}
	
	public abstract boolean generate();
	
	protected abstract ROUTINE getBuiltInRoutine(String name, Routine intermediateRoutine);
	
	protected abstract void optimize();
}
