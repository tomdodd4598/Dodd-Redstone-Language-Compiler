package drlc.interpret.type;

public class Variable {
	
	public final String name;
	public int baseReferenceLevel;
	public boolean initialised = false;
	
	public Variable(String name, int baseReferenceLevel, boolean initialised) {
		this.name = name;
		this.baseReferenceLevel = baseReferenceLevel;
		this.initialised = initialised;
	}
}
