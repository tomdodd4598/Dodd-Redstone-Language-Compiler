package drlc.generate.drc1;

import java.util.Objects;

public class DataInfo {
	
	public final String routineName, argName;
	public final DataType type;
	public final int id;
	
	public DataInfo(String routineName, String argName, DataType type, int id) {
		this.routineName = routineName;
		this.argName = argName;
		this.type = type;
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(routineName, argName, type, id);
	}
	
	public boolean equals(DataInfo other, boolean ignoreArgName) {
		return routineName.equals(other.routineName) && (ignoreArgName || argName.equals(other.argName)) && type == other.type && id == other.id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataInfo)) {
			return false;
		}
		return equals((DataInfo) obj, false);
	}
	
	@Override
	public String toString() {
		return routineName.concat(", ").concat(argName).concat(", ").concat(type.toString()).concat(", ").concat(Integer.toString(id));
	}
	
	public static enum DataType {
		
		DATA,
		TEMP,
		STATIC;
		
		@Override
		public String toString() {
			switch (this) {
				case DATA:
					return "DATA";
				case TEMP:
					return "TEMP";
				case STATIC:
					return "STATIC";
				default:
					return null;
			}
		}
	}
}
