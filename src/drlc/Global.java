package drlc;

import org.eclipse.jdt.annotation.NonNull;

public final class Global {
	
	public static final @NonNull String BOOL = "Bool";
	public static final @NonNull String INT = "Int";
	public static final @NonNull String NAT = "Nat";
	public static final @NonNull String CHAR = "Char";
	
	public static final @NonNull String VOID = "Void";
	
	public static final @NonNull String BOOLS = "Bools";
	public static final @NonNull String INTS = "Ints";
	public static final @NonNull String NATS = "Nats";
	public static final @NonNull String CHARS = "Chars";
	
	public static final @NonNull String FN = "fn";
	public static final @NonNull String CONST = "const";
	public static final @NonNull String LET = "let";
	
	public static final @NonNull String STATIC = "static";
	public static final @NonNull String MUT = "mut";
	
	public static final @NonNull String ARGC = "argc";
	public static final @NonNull String ARGV = "argv";
	
	public static final @NonNull String PTR = "ptr";
	public static final @NonNull String LEN = "len";
	
	public static final @NonNull String READ_BOOL = "readBool";
	public static final @NonNull String READ_INT = "readInt";
	public static final @NonNull String READ_NAT = "readNat";
	public static final @NonNull String READ_CHAR = "readChar";
	
	public static final @NonNull String PRINT_BOOL = "printBool";
	public static final @NonNull String PRINT_INT = "printInt";
	public static final @NonNull String PRINT_NAT = "printNat";
	public static final @NonNull String PRINT_CHAR = "printChar";
	
	public static final @NonNull String ROOT = "root";
	public static final @NonNull String SELF = "self";
	public static final @NonNull String SUPER = "super";
	public static final @NonNull String MAIN = "main";
	
	public static final @NonNull String BRACE_START = "{";
	public static final @NonNull String BRACE_END = "}";
	public static final @NonNull String POINTY_START = "<";
	public static final @NonNull String POINTY_END = ">";
	
	public static final @NonNull String REG = "%";
	public static final @NonNull String TRANSIENT = "%t";
	public static final @NonNull String IMMEDIATE = "$";
	
	public static final @NonNull String EQUALS = "=";
	
	public static final @NonNull String IF = "if";
	public static final @NonNull String UNLESS = "unless";
	public static final @NonNull String WHILE = "while";
	public static final @NonNull String UNTIL = "until";
	
	public static final @NonNull String DEREFERENCE = "*";
	public static final @NonNull String ADDRESS_OF = "&";
	
	public static final @NonNull String JUMP = "jmp";
	public static final @NonNull String CONDITIONAL_JUMP = "cj";
	public static final @NonNull String CONDITIONAL_NOT_JUMP = "cnj";
	
	public static final @NonNull String BUILT_IN = "builtin";
	public static final @NonNull String CALL = "call";
	public static final @NonNull String RETURN = "ret";
	public static final @NonNull String EXIT = "exit";
	
	public static final @NonNull String LEAF = "leaf";
	public static final @NonNull String NESTING = "nesting";
	public static final @NonNull String STACK = "stack";
	
	public static final @NonNull String LIST_SEPARATOR = ", ";
	public static final @NonNull String LIST_START = "(";
	public static final @NonNull String LIST_END = ")";
	public static final @NonNull String TUPLE_SINGLE_END = ",)";
	
	public static final @NonNull String ARRAY_TYPE_DELIMITER = ";";
	public static final @NonNull String ARRAY_START = "[";
	public static final @NonNull String ARRAY_END = "]";
	
	public static final @NonNull String TYPE_ANNOTATION_PREFIX = ":";
	
	public static final @NonNull String FULL_STOP = ".";
	public static final @NonNull String ARROW = "->";
	
	public static final @NonNull String PATH_SEPARATOR = "::";
	public static final @NonNull String WILDCARD_PATH = "*";
	
	public static final @NonNull String LOGICAL_RIGHT_SHIFT = "\\logical_right_shift";
	public static final @NonNull String CIRCULAR_LEFT_SHIFT = "\\circular_left_shift";
	public static final @NonNull String CIRCULAR_RIGHT_SHIFT = "\\circular_right_shift";
	
	public static final @NonNull String ZERO_8 = "00000000";
}
