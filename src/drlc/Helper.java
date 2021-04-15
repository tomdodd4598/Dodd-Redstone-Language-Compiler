package drlc;

import java.util.Locale;

public class Helper {
	
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isLetterOrDigit(char c) {
		return isLetter(c) || isDigit(c);
	}
	
	public static String immediateValueString(Integer immediateValue) {
		return Global.IMMEDIATE.concat(immediateValue.toString());
	}
	
	public static String immediateValueString(String immediateValueText) {
		return Global.IMMEDIATE.concat(immediateValueText);
	}
	
	public static boolean isImmediateValue(String immediateValueString) {
		return immediateValueString.startsWith(Global.IMMEDIATE);
	}
	
	public static Integer parseImmediateValue(String immediateValueString) {
		if (isImmediateValue(immediateValueString)) {
			return Integer.parseInt(immediateValueString.substring(Global.IMMEDIATE.length()));
		}
		else {
			return null;
		}
	}
	
	public static String regIdString(int regId) {
		return Global.REG.concat(Integer.toString(regId));
	}
	
	public static boolean isRegId(String regIdString) {
		return regIdString.startsWith(Global.REG);
	}
	
	public static Integer parseRegId(String regIdString) {
		if (isRegId(regIdString)) {
			return Integer.parseInt(regIdString.substring(Global.REG.length()));
		}
		else {
			return null;
		}
	}
	
	public static String sectionIdString(int sectionId) {
		return Global.SECTION.concat(Integer.toString(sectionId));
	}
	
	public static boolean isSectionId(String sectionIdString) {
		return sectionIdString.startsWith(Global.SECTION);
	}
	
	public static Integer parseSectionId(String sectionIdString) {
		if (isSectionId(sectionIdString)) {
			return Integer.parseInt(sectionIdString.substring(Global.SECTION.length()));
		}
		else {
			return null;
		}
	}
	
	public static String toBinary(int value, int length) {
		return String.format("%" + length + "s", Integer.toBinaryString(((1 << length) - 1) & value)).replace(' ', '0');
	}
	
	public static String toHex(int value) {
		if (value < 0) {
			return "-0x".concat(Integer.toHexString(-value).toUpperCase(Locale.ROOT));
		}
		else {
			return "0x".concat(Integer.toHexString(value).toUpperCase(Locale.ROOT));
		}
	}
	
	public static String toHex(int value, int length) {
		if (value < 0) {
			return "-0x".concat(String.format("%" + length + "s", Integer.toHexString(-value)).replace(' ', '0').toUpperCase(Locale.ROOT));
		}
		else {
			return "0x".concat(String.format("%" + length + "s", Integer.toHexString(value)).replace(' ', '0').toUpperCase(Locale.ROOT));
		}
	}
	
	public static class Pair<L, R> {
		
		public final L left;
		public final R right;
		
		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}
	}
}
