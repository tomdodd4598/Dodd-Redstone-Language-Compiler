package drlc;

import java.util.*;

import drlc.intermediate.action.*;
import drlc.intermediate.component.DataId;

public final class Global {
	
	public static final Set<String> DIRECTIVES = new HashSet<>();
	
	public static final String SETARGC = "setargc";
	
	static {
		DIRECTIVES.add(SETARGC);
	}
	
	public static final String VOID = "Void";
	public static final String BOOL = "Bool";
	public static final String INT = "Int";
	public static final String NAT = "Nat";
	public static final String CHAR = "Char";
	
	public static final String FN = "fn";
	public static final String VAR = "var";
	
	public static final String TYPE_ANNOTATION_PREFIX = ":";
	
	public static final String ARGC = "argc";
	
	public static final String INCHAR = "inchar";
	public static final String ININT = "inint";
	public static final String OUTCHAR = "outchar";
	public static final String OUTINT = "outint";
	public static final String ARGV = "argv";
	
	public static final String ROOT_ROUTINE = "\\root";
	
	public static final String CONSTRUCTOR = "{0}";
	public static final String DESTRUCTOR = "{d}";
	public static final String STATEMENT_LABEL_PREFIX = ":";
	
	public static final String DOUBLE_COLON = "::";
	
	public static final String SECTION_1 = "{";
	public static final String SECTION_2 = "}";
	public static final String REG = "%";
	public static final String TRANSIENT = "%t";
	public static final String IMMEDIATE = "$";
	public static final String DISCARD = "_";
	
	public static final String STATIC = "static";
	
	public static final String EQUALS = "=";
	
	public static final String IF = "if";
	public static final String WHILE = "while";
	
	public static final char DEREFERENCE = '*';
	public static final char ADDRESS_OF = '&';
	
	public static final String JUMP = "jmp";
	public static final String CONDITIONAL_JUMP = "cj";
	public static final String CONDITIONAL_NOT_JUMP = "cnj";
	
	public static final String BUILT_IN = "builtin";
	public static final String CALL = "call";
	public static final String RETURN = "ret";
	public static final String EXIT = "exit";
	
	public static final String LEAF = "leaf";
	public static final String NESTING = "nesting";
	public static final String STACK = "stack";
	
	public static final String BUILT_IN_PARAM_PREFIX = "\\";
	public static final String DISCARD_PARAM_PREFIX = "\\_";
	public static final String PARAM_SEPARATOR = ", ";
	public static final String ARG_SEPARATOR = ", ";
	
	public static final Action RETURN_FROM_FUNCTION = new ReturnAction();
	public static final Action EXIT_PROGRAM = new ExitAction();
	
	public static final Action ITERATION_CONTINUE_PLACEHOLDER = new PlaceholderAction(null, "continue");
	public static final Action ITERATION_CONDITIONAL_CONTINUE_PLACEHOLDER = new PlaceholderAction(null, "ccontinue");
	public static final Action ITERATION_CONDITIONAL_NOT_CONTINUE_PLACEHOLDER = new PlaceholderAction(null, "cncontinue");
	
	public static final Action ITERATION_BODY_JUMP_PLACEHOLDER = new PlaceholderAction(null, "body");
	public static final Action ITERATION_CONDITIONAL_BODY_JUMP_PLACEHOLDER = new PlaceholderAction(null, "cbody");
	public static final Action ITERATION_CONDITIONAL_NOT_BODY_JUMP_PLACEHOLDER = new PlaceholderAction(null, "cnbody");
	
	public static final Action ITERATION_BREAK_PLACEHOLDER = new PlaceholderAction(null, "break");
	public static final Action ITERATION_CONDITIONAL_BREAK_PLACEHOLDER = new PlaceholderAction(null, "cbreak");
	public static final Action ITERATION_CONDITIONAL_NOT_BREAK_PLACEHOLDER = new PlaceholderAction(null, "cnbreak");
	
	public static final DataId TRANSIENT_DATA_ID = new DataId(TRANSIENT, null);
	
	public static final String LOGICAL_RIGHT_SHIFT = "\\logical_right_shift";
	public static final String CIRCULAR_LEFT_SHIFT = "\\circular_left_shift";
	public static final String CIRCULAR_RIGHT_SHIFT = "\\circular_right_shift";
	
	public static final String ZERO_8 = "00000000";
}
