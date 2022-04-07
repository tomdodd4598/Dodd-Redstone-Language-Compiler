package drlc.generate.drc1;

import java.util.Objects;

public class RedstoneAddressKey {
	
	private final String routineName;
	private final long id;
	
	public RedstoneAddressKey(String routineName, long id) {
		this.routineName = routineName;
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(routineName, id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RedstoneAddressKey) {
			RedstoneAddressKey other = (RedstoneAddressKey) obj;
			return routineName.equals(other.routineName) && id == other.id;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return Long.toString(id);
	}
}
