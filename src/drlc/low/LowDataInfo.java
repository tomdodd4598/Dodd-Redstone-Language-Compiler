package drlc.low;

import java.util.Objects;

import drlc.intermediate.component.data.DataId;

public class LowDataInfo {
	
	public final DataId dataId;
	public final int offset;
	public final LowDataSpan span;
	public final LowDataType type;
	public final LowRoutine<?, ?, ?> routine;
	
	public LowDataInfo(LowCode<?, ?, ?> code, DataId dataId, int offset, LowDataSpan span, LowDataType type) {
		this.dataId = dataId;
		this.offset = offset;
		this.span = span;
		this.type = type;
		routine = code.getRoutine(span.function);
	}
	
	public LowDataInfo offsetBy(int offset) {
		return new LowDataInfo(routine.code, dataId, this.offset + offset, span, type);
	}
	
	public LowDataInfo getRegeneratedDataInfo() {
		return routine.getDataInfo(dataId, offset);
	}
	
	public boolean isTempData() {
		return routine.isTempData(dataId);
	}
	
	public boolean isStackData() {
		return routine.isStackData(dataId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(dataId, offset, span, type);
	}
	
	public boolean equalsOther(LowDataInfo other, boolean ignoreDataId) {
		return (ignoreDataId || dataId.equals(other.dataId)) && offset == other.offset && span.equals(other.span) && type.equals(other.type);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LowDataInfo other) {
			return equalsOther(other, false);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return dataId + ", " + offset + ", " + span + ", " + type;
	}
}
