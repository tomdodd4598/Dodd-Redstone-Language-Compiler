package drlc;


import java.util.HashMap;
import java.util.Map;

import drlc.interpret.action.Action;
import drlc.interpret.action.HaltAction;
import drlc.interpret.action.PlaceholderAction;
import drlc.interpret.action.ReturnAction;

public final class Global {
	
	public static final Map<String, Integer> BUILT_IN_METHODS = new HashMap<>();
	public static final Map<String, Integer> BUILT_IN_FUNCTIONS = new HashMap<>();
	
	public static final String OUT = "out";
	
	static {
		BUILT_IN_METHODS.put(OUT, 1);
	}
	
	public static final String ROOT_ROUTINE = "@root";
	
	public static final String CONSTRUCTOR = "#0";
	public static final String DESTRUCTOR = "#d";
	
	public static final String SECTION = "#";
	public static final String REG = "%";
	public static final String TRANSIENT = "%t";
	public static final String IMMEDIATE = "$";
	
	public static final String NEW = "new";
	public static final String JUMP = "jmp";
	public static final String IF = "if";
	public static final String CONDITIONAL_JUMP = "cj";
	public static final String HARDWARE = "hardware";
	public static final String CALL = "call";
	public static final String FUN = "fun";
	public static final String RETURN = "ret";
	public static final String HALT = "halt";
	
	public static final String LEAF = "leaf";
	public static final String NESTING = "nesting";
	public static final String RECURSIVE = "recursive";
	
	public static final Action RETURN_FROM_SUBROUTINE = new ReturnAction();
	public static final Action HALT_ROUTINE = new HaltAction();
	
	public static final PlaceholderAction ITERATOR_BREAK_PLACEHOLDER = new PlaceholderAction(null, "break");
	
	public static final String ZERO_8 = "00000000";
}
