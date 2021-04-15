package drlc.interpret.type;

public class Variable {
	
	public final String name;
	public boolean initialised = false;
	
	public Variable(String name, boolean initialised) {
		this.name = name;
		this.initialised = initialised;
	}
}
