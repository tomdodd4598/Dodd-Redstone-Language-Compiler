package drlc.low.edsac;

import drlc.Helpers;

public class EdsacChar {
	
	public static final EdsacChar LETTER_SHIFT = EdsacChar.of('*');
	public static final EdsacChar FIGURE_SHIFT = EdsacChar.of('^');
	
	public final byte ascii, code;
	
	private EdsacChar(byte ascii, byte code) {
		this.ascii = ascii;
		this.code = code;
	}
	
	public static EdsacChar of(char value) {
		byte code = switch (value) {
			case 'P', 'p', '0' -> 0;
			case 'Q', 'q', '1' -> 1;
			case 'W', 'w', '2' -> 2;
			case 'E', 'e', '3' -> 3;
			case 'R', 'r', '4' -> 4;
			case 'T', 't', '5' -> 5;
			case 'Y', 'y', '6' -> 6;
			case 'U', 'u', '7' -> 7;
			case 'I', 'i', '8' -> 8;
			case 'O', 'o', '9' -> 9;
			case 'J', 'j' -> 10;
			case '^' -> 11;
			case 'S', 's', '"' -> 12;
			case 'Z', 'z', '+' -> 13;
			case 'K', 'k', '(' -> 14;
			case '*' -> 15;
			case '\0' -> 16;
			case 'F', 'f', '$' -> 17;
			case '\r' -> 18;
			case 'D', 'd', ';' -> 19;
			case ' ' -> 20;
			case 'H', 'h', '%' -> 21;
			case 'N', 'n', ',' -> 22;
			case 'M', 'm', '.' -> 23;
			case '\n' -> 24;
			case 'L', 'l', ')' -> 25;
			case 'X', 'x', '/' -> 26;
			case 'G', 'g', '#' -> 27;
			case 'A', 'a', '-' -> 28;
			case 'B', 'b', '?' -> 29;
			case 'C', 'c', ':' -> 30;
			case 'V', 'v', '=' -> 31;
			default -> throw new IllegalArgumentException(String.format("Character %s is not supported by EDSAC backend!", Helpers.charToString(value)));
		};
		return new EdsacChar((byte) value, code);
	}
	
	public boolean requiresLetterShift() {
		return switch (ascii) {
			case '^', '*', '\0', '\r', ' ', '\n' -> false;
			default -> !requiresFigureShift();
		};
	}
	
	public boolean requiresFigureShift() {
		return switch (ascii) {
			case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '"', '+', '(', '$', ';', '%', ',', '.', ')', '/', '#', '-', '?', ':', '=' -> true;
			default -> false;
		};
	}
	
	public EdsacInt toInt() {
		return EdsacInt.of(code << 12);
	}
}
