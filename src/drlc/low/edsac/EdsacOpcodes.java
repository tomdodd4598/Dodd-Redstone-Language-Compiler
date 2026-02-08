package drlc.low.edsac;

public class EdsacOpcodes {
	
	public static final char ADD = 'A';
	public static final char SUBTRACT = 'S';
	public static final char LOAD_MULTIPLIER = 'H';
	public static final char ADD_MULTIPLICATION = 'V';
	public static final char SUBTRACT_MULTIPLICATION = 'N';
	public static final char STORE_AND_CLEAR = 'T';
	public static final char STORE = 'U';
	public static final char ADD_COLLATION = 'C';
	public static final char RIGHT_SHIFT = 'R';
	public static final char LEFT_SHIFT = 'L';
	public static final char JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO = 'E';
	public static final char JUMP_IF_LESS_THAN_ZERO = 'G';
	public static final char READ = 'I';
	public static final char PRINT = 'O';
	public static final char VERIFY = 'F';
	public static final char NO_OP = 'X';
	public static final char ROUND = 'Y';
	public static final char HALT = 'Z';
	
	public static final String SHORT = "F";
	public static final String LONG = "D";
	
	public static final String THETA = "@";
	public static final String PHI = "!";
	public static final String DELTA = "&";
	public static final String PI = "#";
	
	public static char get(byte code) {
		return switch (code) {
			case 0 -> 'P';
			case 1 -> 'Q';
			case 2 -> 'W';
			case 3 -> 'E';
			case 4 -> 'R';
			case 5 -> 'T';
			case 6 -> 'Y';
			case 7 -> 'U';
			case 8 -> 'I';
			case 9 -> 'O';
			case 10 -> 'J';
			case 11 -> '#';
			case 12 -> 'S';
			case 13 -> 'Z';
			case 14 -> 'K';
			case 15 -> '*';
			case 16 -> '.';
			case 17 -> 'F';
			case 18 -> '@';
			case 19 -> 'D';
			case 20 -> '!';
			case 21 -> 'H';
			case 22 -> 'N';
			case 23 -> 'M';
			case 24 -> '&';
			case 25 -> 'L';
			case 26 -> 'X';
			case 27 -> 'G';
			case 28 -> 'A';
			case 29 -> 'B';
			case 30 -> 'C';
			case 31 -> 'V';
			default -> throw new IllegalArgumentException(String.format("Found unexpected EDSAC character code %d!", code));
		};
	}
	
	public static char get(EdsacInt value) {
		return get(value.toCharCode());
	}
}
