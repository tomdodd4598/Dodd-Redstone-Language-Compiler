package drlc.low.edsac;

public class EdsacOpcodes {
	
	public static final String ADD = "A";
	public static final String SUBTRACT = "S";
	public static final String LOAD_MULTIPLIER = "H";
	public static final String ADD_MULTIPLICATION = "V";
	public static final String SUBTRACT_MULTIPLICATION = "N";
	public static final String STORE_AND_CLEAR = "T";
	public static final String STORE = "U";
	public static final String ADD_COLLATION = "C";
	public static final String RIGHT_SHIFT = "R";
	public static final String LEFT_SHIFT = "L";
	public static final String JUMP_IF_POSITIVE = "E";
	public static final String JUMP_IF_NEGATIVE = "G";
	public static final String READ = "I";
	public static final String PRINT = "O";
	public static final String VERIFY = "F";
	public static final String NO_OP = "X";
	public static final String ROUND = "Y";
	public static final String HALT = "Z";
	
	public static final String SHORT = "F";
	public static final String LONG = "D";
	
	public static final String THETA = "@";
	public static final String PHI = "!";
	public static final String DELTA = "&";
	public static final String PI = "#";
	
	public static final String get(EdsacInt value) {
		int code = value.toChar();
		return switch (value.toChar()) {
			case 0 -> "P";
			case 1 -> "Q";
			case 2 -> "W";
			case 3 -> "E";
			case 4 -> "R";
			case 5 -> "T";
			case 6 -> "Y";
			case 7 -> "U";
			case 8 -> "I";
			case 9 -> "O";
			case 10 -> "J";
			case 11 -> "#";
			case 12 -> "S";
			case 13 -> "Z";
			case 14 -> "K";
			case 15 -> "*";
			case 16 -> ".";
			case 17 -> "F";
			case 18 -> "@";
			case 19 -> "D";
			case 20 -> "!";
			case 21 -> "H";
			case 22 -> "N";
			case 23 -> "M";
			case 24 -> "&";
			case 25 -> "L";
			case 26 -> "X";
			case 27 -> "G";
			case 28 -> "A";
			case 29 -> "B";
			case 30 -> "C";
			case 31 -> "V";
			default -> throw new IllegalArgumentException(String.format("Found unexpected EDSAC character code %d!", code));
		};
	}
}
