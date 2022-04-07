package drlc;

import java.util.*;

import drlc.interpret.action.*;
import drlc.interpret.component.*;
import drlc.interpret.component.info.FunctionModifierInfo;
import drlc.interpret.component.info.type.*;

public final class Global {
	
	public static final Set<String> DIRECTIVES = new HashSet<>();
	public static final Map<String, Function> BUILT_IN_FUNCTIONS = new HashMap<>();
	
	public static final char DIRECTIVE_PREFIX = '#';
	
	public static final String DEF = "def";
	public static final String ENDDEF = "enddef";
	
	public static final String SETARGC = "setargc";
	
	public static final String VOID = "Void";
	public static final String INT = "Int";
	
	public static final String FUN = "fun";
	public static final String VAR = "var";
	
	public static final Type VOID_TYPE = new Type(VOID, 0);
	public static final Type INT_TYPE = new Type(INT, 1);
	public static Type fun_type;
	
	public static final TypeInfo VOID_TYPE_INFO = new BasicTypeInfo(null, VOID_TYPE, 0);
	public static final TypeInfo INT_TYPE_INFO = new BasicTypeInfo(null, INT_TYPE, 0);
	
	public static final String ARGC = "argc";
	
	public static final String INCHAR = "inchar";
	public static final String ININT = "inint";
	public static final String OUTCHAR = "outchar";
	public static final String OUTINT = "outint";
	public static final String ARGV = "argv";
	
	public static final String SIZEOF = "sizeof";
	
	static {
		DIRECTIVES.add(SETARGC);
		
		BUILT_IN_FUNCTIONS.put(INCHAR, new Function(null, INCHAR, true, new FunctionModifierInfo(false, false), INT_TYPE_INFO, Helpers.params(), true));
		BUILT_IN_FUNCTIONS.put(ININT, new Function(null, ININT, true, new FunctionModifierInfo(false, false), INT_TYPE_INFO, Helpers.params(), true));
		BUILT_IN_FUNCTIONS.put(OUTCHAR, new Function(null, OUTCHAR, true, new FunctionModifierInfo(false, false), VOID_TYPE_INFO, Helpers.params(Helpers.builtInParam("c", INT_TYPE_INFO)), true));
		BUILT_IN_FUNCTIONS.put(OUTINT, new Function(null, OUTINT, true, new FunctionModifierInfo(false, false), VOID_TYPE_INFO, Helpers.params(Helpers.builtInParam("x", INT_TYPE_INFO)), true));
		BUILT_IN_FUNCTIONS.put(ARGV, new Function(null, ARGV, true, new FunctionModifierInfo(false, false), INT_TYPE_INFO, Helpers.params(Helpers.builtInParam("index", INT_TYPE_INFO)), true));
	}
	
	public static final int ROOT_SCOPE_ID = 0;
	
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
	
	public static final String INIT = "init";
	public static final String STACK = "stack";
	public static final String STATIC = "static";
	
	public static final String EQUALS = "=";
	
	public static final String IF = "if";
	public static final String ELIF = "elif";
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
