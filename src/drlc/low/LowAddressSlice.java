package drlc.low;

import java.util.Objects;

public class LowAddressSlice {
	
	public final int start;
	public final int size;
	
	public LowAddressSlice(int start, int size) {
		this.start = start;
		this.size = size;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(start, size);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LowAddressSlice) {
			LowAddressSlice other = (LowAddressSlice) obj;
			return start == other.start && size == other.size;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "[" + start + ", " + size + "]";
	}
}
