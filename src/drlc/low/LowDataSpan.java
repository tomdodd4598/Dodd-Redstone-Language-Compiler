package drlc.low;

import java.util.Objects;

import drlc.intermediate.component.Function;

public class LowDataSpan {
	
	public final Function function;
	public final int id;
	public final int size;
	
	public LowDataSpan(Function function, int id, int size) {
		this.function = function;
		this.id = id;
		this.size = size;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(function, id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LowDataSpan) {
			LowDataSpan other = (LowDataSpan) obj;
			return function.equals(other.function) && id == other.id;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return id + "[" + size + "]";
	}
}
