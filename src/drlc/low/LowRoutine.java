package drlc.low;

import java.util.*;
import java.util.Map.Entry;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.data.DataId.LowDataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.low.instruction.*;
import drlc.low.instruction.address.IInstructionAddress;

public abstract class LowRoutine<CODE extends LowCode<CODE, ROUTINE, INSTRUCTION>, ROUTINE extends LowRoutine<CODE, ROUTINE, INSTRUCTION>, INSTRUCTION extends LowInstruction> {
	
	public final CODE code;
	public final Routine intermediate;
	public final Function function;
	public final List<DeclaratorInfo> params;
	
	public final Map<Integer, List<INSTRUCTION>> sectionTextMap = new TreeMap<>();
	
	protected final Map<LowDataId, Pair<DataId, LowDataSpan>> localSpanMap;
	protected final Map<LowDataId, Pair<DataId, LowDataSpan>> tempSpanMap = new LinkedHashMap<>();
	
	public final Map<Integer, Integer> sectionAddressMap = new LinkedHashMap<>();
	
	public final Map<LowDataSpan, LowAddressSlice> localAddressMap;
	public final Map<LowDataSpan, LowAddressSlice> tempAddressMap = new LinkedHashMap<>();
	
	public boolean generated = false;
	
	protected LowRoutine(CODE code, Routine intermediate) {
		this.code = code;
		this.intermediate = intermediate;
		function = intermediate.function;
		params = new ArrayList<>(intermediate.getParams());
		
		TypeInfo returnType = function.returnTypeInfo;
		if (returnType.getSize() > 1) {
			params.add(function.scope.nextLocalDeclarator(intermediate, returnType.addressOf(null, true)));
		}
		
		if (isRootRoutine()) {
			localSpanMap = code.rootSpanMap;
			localAddressMap = code.rootAddressMap;
		}
		else {
			localSpanMap = new LinkedHashMap<>();
			localAddressMap = new LinkedHashMap<>();
		}
		
		generateParamDataInfo();
	}
	
	public void generateParamDataInfo() {
		for (DeclaratorInfo param : params) {
			DataId dataId = param.dataId();
			LowDataType dataType = getDataType(dataId);
			getDataSpanMap(dataType).put(dataId.low(), new Pair<>(dataId, nextParamSpan(dataId)));
		}
	}
	
	public void regenerateDataInfoInternal() {
		if (isRootRoutine()) {
			Map<LowDataInfo, INSTRUCTION> regeneratedMap = new LinkedHashMap<>();
			Iterator<Entry<LowDataInfo, INSTRUCTION>> iter = code.staticDataMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<LowDataInfo, INSTRUCTION> entry = iter.next();
				iter.remove();
				INSTRUCTION data = entry.getValue();
				if (data instanceof IInstructionAddressData iad) {
					iad.regenerateDataInfo();
				}
				regeneratedMap.put(entry.getKey().getRegeneratedDataInfo(), data);
			}
			code.staticDataMap.putAll(regeneratedMap);
		}
		
		for (List<INSTRUCTION> section : sectionTextMap.values()) {
			for (INSTRUCTION instruction : section) {
				if (instruction instanceof IInstructionAddress instructionAddress) {
					instructionAddress.regenerateDataInfo();
				}
			}
		}
	}
	
	public boolean isStackRoutine() {
		return intermediate.isStackRoutine();
	}
	
	public boolean isRootRoutine() {
		return intermediate.isRootRoutine();
	}
	
	public int getFinalTextSectionKey() {
		return intermediate.body.size();
	}
	
	// Data
	
	protected boolean isTempData(DataId dataId) {
		return dataId instanceof RegDataId;
	}
	
	protected boolean isStackData(DataId dataId) {
		if (dataId instanceof VariableDataId variableDataId && variableDataId.variable.modifier._static) {
			return false;
		}
		return isStackRoutine();
	}
	
	protected LowDataType getDataType(DataId dataId) {
		if (isTempData(dataId)) {
			return LowDataType.TEMP;
		}
		else if (isStackData(dataId)) {
			return LowDataType.STACK;
		}
		else {
			return LowDataType.STATIC;
		}
	}
	
	protected Map<LowDataId, Pair<DataId, LowDataSpan>> getDataSpanMap(LowDataType dataType) {
		return switch (dataType) {
			case TEMP -> tempSpanMap;
			case STACK -> localSpanMap;
			case STATIC -> code.rootSpanMap;
		};
	}
	
	protected Function getDataFunction(LowDataType dataType) {
		return switch (dataType) {
			case TEMP -> function;
			case STACK -> function;
			case STATIC -> Main.rootRoutine.function;
		};
	}
	
	protected LowDataInfo getDataInfo(DataId dataId, int offset) {
		LowDataType dataType = getDataType(dataId);
		Pair<DataId, LowDataSpan> pair = getSpanPair(getDataSpanMap(dataType), dataId);
		return new LowDataInfo(code, dataId.dereferenceLevel == 0 ? dataId : pair.left, offset, pair.right, dataType);
	}
	
	protected Pair<DataId, LowDataSpan> getSpanPair(Map<LowDataId, Pair<DataId, LowDataSpan>> spanMap, DataId dataId) {
		LowDataId low = dataId.low();
		if (spanMap.containsKey(low)) {
			return spanMap.get(low);
		}
		else {
			Pair<DataId, LowDataSpan> pair = new Pair<>(dataId, nextSpan(getDataType(dataId), dataId.typeInfo));
			spanMap.put(low, pair);
			return pair;
		}
	}
	
	protected LowDataSpan nextParamSpan(DataId dataId) {
		LowDataType dataType = getDataType(dataId);
		if (dataType.equals(LowDataType.STACK)) {
			int id = -1, size = dataId.typeInfo.getSize();
			outer: while (true) {
				LowDataSpan span = new LowDataSpan(function, id, size);
				for (Pair<DataId, LowDataSpan> pair : localSpanMap.values()) {
					if (pair.right.equals(span)) {
						--id;
						continue outer;
					}
				}
				return span;
			}
		}
		else {
			return nextSpan(dataType, dataId.typeInfo);
		}
	}
	
	protected LowDataSpan nextSpan(LowDataType dataType, TypeInfo typeInfo) {
		Map<LowDataId, Pair<DataId, LowDataSpan>> spanMap = getDataSpanMap(dataType);
		Function function = getDataFunction(dataType);
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
	
	protected static int spanMapSize(Map<LowDataId, Pair<DataId, LowDataSpan>> spanMap) {
		return Helpers.sumToInt(spanMap.keySet(), x -> x.internal.typeInfo.getSize());
	}
	
	protected int getAddress(LowDataInfo dataInfo) {
		return switch (dataInfo.type) {
			case TEMP -> dataAddress(dataInfo.routine.tempAddressMap, dataInfo);
			case STATIC -> dataAddress(code.rootAddressMap, dataInfo);
			case STACK -> dataAddress(dataInfo.routine.localAddressMap, dataInfo);
		};
	}
	
	protected static int dataAddress(Map<LowDataSpan, LowAddressSlice> addressMap, LowDataInfo dataInfo) {
		return addressMap.get(dataInfo.span).start + dataInfo.offset;
	}
}
