package drlc;

import java.util.*;

import drlc.interpret.action.*;

public final class Global {
	
	public static final Map<String, Integer> BUILT_IN_METHODS = new HashMap<>();
	public static final Map<String, Integer> BUILT_IN_FUNCTIONS = new HashMap<>();
	
	public static final String SET_ARGC = "set_argc";
	public static final String OUT = "out";
	
	public static final String IN = "in";
	public static final String ARGC = "argc";
	public static final String ARGV = "argv";
	
	static {
		BUILT_IN_METHODS.put(SET_ARGC, 1);
		BUILT_IN_METHODS.put(OUT, 1);
		
		BUILT_IN_FUNCTIONS.put(IN, 0);
		BUILT_IN_FUNCTIONS.put(ARGC, 0);
		BUILT_IN_FUNCTIONS.put(ARGV, 1);
	}
	
	public static final String ROOT_ROUTINE = "\\root";
	
	public static final String CONSTRUCTOR = "{0}";
	public static final String DESTRUCTOR = "{d}";
	
	public static final String SECTION_1 = "{";
	public static final String SECTION_2 = "}";
	public static final String REG = "%";
	public static final String TRANSIENT = "%t";
	public static final String IMMEDIATE = "$";
	
	public static final char ADDRESS_OF = '@';
	public static final char DEREFERENCE = '#';
	
	public static final String IF = "if";
	public static final String ELSIF = "elsif";
	public static final String WHILE = "while";
	
	public static final String INT = "int";
	
	public static final String JUMP = "jmp";
	public static final String CONDITIONAL_JUMP = "cj";
	public static final String CONDITIONAL_NOT_JUMP = "cnj";
	
	public static final String HARDWARE = "hardware";
	public static final String FUN = "fun";
	public static final String RETURN = "ret";
	public static final String HALT = "halt";
	
	public static final String LEAF = "leaf";
	public static final String NESTING = "nesting";
	public static final String RECURSIVE = "recursive";
	
	public static final Action RETURN_FROM_SUBROUTINE = new ReturnAction();
	public static final Action HALT_ROUTINE = new HaltAction();
	
	public static final Action ITERATION_CONTINUE_PLACEHOLDER = new PlaceholderAction(null, "continue");
	public static final Action ITERATION_CONDITIONAL_CONTINUE_PLACEHOLDER = new PlaceholderAction(null, "ccontinue");
	public static final Action ITERATION_CONDITIONAL_NOT_CONTINUE_PLACEHOLDER = new PlaceholderAction(null, "cncontinue");
	
	public static final Action ITERATION_BODY_JUMP_PLACEHOLDER = new PlaceholderAction(null, "body");
	public static final Action ITERATION_CONDITIONAL_BODY_JUMP_PLACEHOLDER = new PlaceholderAction(null, "cbody");
	public static final Action ITERATION_CONDITIONAL_NOT_BODY_JUMP_PLACEHOLDER = new PlaceholderAction(null, "cnbody");
	
	public static final Action ITERATION_BREAK_PLACEHOLDER = new PlaceholderAction(null, "break");
	public static final Action ITERATION_CONDITIONAL_BREAK_PLACEHOLDER = new PlaceholderAction(null, "cbreak");
	public static final Action ITERATION_CONDITIONAL_NOT_BREAK_PLACEHOLDER = new PlaceholderAction(null, "cnbreak");
	
	public static final String ZERO_8 = "00000000";
}
