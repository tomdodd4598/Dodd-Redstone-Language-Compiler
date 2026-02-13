package drlc.low.edsac;

import drlc.Helpers;

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
	
	public static byte getCode(char c) {
		return (byte) switch (c) {
			case 'P' -> 0;
			case 'Q' -> 1;
			case 'W' -> 2;
			case 'E' -> 3;
			case 'R' -> 4;
			case 'T' -> 5;
			case 'Y' -> 6;
			case 'U' -> 7;
			case 'I' -> 8;
			case 'O' -> 9;
			case 'J' -> 10;
			case '#' -> 11;
			case 'S' -> 12;
			case 'Z' -> 13;
			case 'K' -> 14;
			case '*' -> 15;
			case '.' -> 16;
			case 'F' -> 17;
			case '@' -> 18;
			case 'D' -> 19;
			case '!' -> 20;
			case 'H' -> 21;
			case 'N' -> 22;
			case 'M' -> 23;
			case '&' -> 24;
			case 'L' -> 25;
			case 'X' -> 26;
			case 'G' -> 27;
			case 'A' -> 28;
			case 'B' -> 29;
			case 'C' -> 30;
			case 'V' -> 31;
			default -> throw new IllegalArgumentException(String.format("Found unexpected EDSAC opcode character %s!", Helpers.charToString(c)));
		};
	}
	
	public static char getChar(byte code) {
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
			default -> throw new IllegalArgumentException(String.format("Found unexpected EDSAC opcode value %d!", code));
		};
	}
	
	public static char getChar(EdsacInt value) {
		return getChar(value.toCharCode());
	}
}
