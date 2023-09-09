package drlc.low.drc1;

import java.util.Objects;

import drlc.intermediate.component.data.DataId;

public class RedstoneDataInfo {
	
	public final String routineName;
	public final DataId argId;
	public final RedstoneDataType type;
	public final RedstoneAddressKey key;
	
	public RedstoneDataInfo(String routineName, DataId argId, RedstoneDataType type, long id) {
		this.routineName = routineName;
		this.argId = argId;
		this.type = type;
		this.key = new RedstoneAddressKey(routineName, id);
	}
	
	public boolean isTempData() {
		return type.equals(RedstoneDataType.TEMP);
	}
	
	public boolean isStaticData() {
		return type.equals(RedstoneDataType.STATIC);
	}
	
	public boolean isStackData() {
		return type.equals(RedstoneDataType.STACK);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(routineName, argId, type, key);
	}
	
	public boolean equalsOther(RedstoneDataInfo other, boolean ignoreArgId) {
		return routineName.equals(other.routineName) && (ignoreArgId || argId.equals(other.argId)) && type.equals(other.type) && key.equals(other.key);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RedstoneDataInfo) {
			return equalsOther((RedstoneDataInfo) obj, false);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return routineName + ", " + argId + ", " + type + ", " + key;
	}
}
