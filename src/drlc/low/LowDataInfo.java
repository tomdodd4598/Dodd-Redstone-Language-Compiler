package drlc.low;

import java.util.Objects;

import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;

public class LowDataInfo {
	
	public final Function function;
	public final DataId dataId;
	public final LowDataType type;
	public final LowDataSpan span;
	public final int extraOffset;
	
	public LowDataInfo(Function function, DataId dataId, LowDataType type, LowDataSpan span, int extraOffset) {
		this.function = function;
		this.dataId = dataId;
		this.type = type;
		this.span = span;
		this.extraOffset = extraOffset;
	}
	
	public boolean isTempData() {
		return type.equals(LowDataType.TEMP);
	}
	
	public boolean isStaticData() {
		return type.equals(LowDataType.STATIC);
	}
	
	public boolean isStackData() {
		return type.equals(LowDataType.STACK);
	}
	
	public LowDataInfo offset(int offset) {
		return new LowDataInfo(function, dataId, type, span, extraOffset + offset);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(function, dataId, type, span, extraOffset);
	}
	
	public boolean equalsOther(LowDataInfo other, boolean ignoreArgId) {
		return function.equals(other.function) && (ignoreArgId || dataId.equals(other.dataId)) && type.equals(other.type) && span.equals(other.span) && extraOffset == other.extraOffset;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LowDataInfo) {
			return equalsOther((LowDataInfo) obj, false);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return function + ", " + dataId + ", " + type + ", " + span + ", " + extraOffset;
	}
}
