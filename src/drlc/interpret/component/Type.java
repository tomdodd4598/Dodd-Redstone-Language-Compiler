package drlc.interpret.component;

public class Type {
	
	private final String name;
	public final int size;
	
	public Type(String name, int size) {
		this.name = name;
		this.size = size;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Type) {
			Type other = (Type) obj;
			return name.equals(other.name) && size == other.size;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
