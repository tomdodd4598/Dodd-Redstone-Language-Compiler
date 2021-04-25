package drlc;

import java.util.*;

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
	
	public static String removeWhitespace(String s) {
		return s.replaceAll("\\s+", "");
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
		return Global.SECTION_1.concat(Integer.toString(sectionId)).concat(Global.SECTION_2);
	}
	
	public static boolean isSectionId(String sectionIdString) {
		return sectionIdString.startsWith(Global.SECTION_1) && sectionIdString.endsWith(Global.SECTION_2);
	}
	
	public static Integer parseSectionId(String sectionIdString) {
		if (isSectionId(sectionIdString)) {
			return Integer.parseInt(sectionIdString.substring(Global.SECTION_1.length(), sectionIdString.length() - Global.SECTION_2.length()));
		}
		else {
			return null;
		}
	}
	
	public static boolean hasAddressPrefix(String s) {
		s = s.trim();
		if (s.startsWith(Character.toString(Global.ADDRESS_OF))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String removeAddressPrefix(String s) {
		s = s.trim();
		String addressOf = Character.toString(Global.ADDRESS_OF);
		if (s.startsWith(addressOf)) {
			return s.substring(addressOf.length()).trim();
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to remove the address prefix of invalid variable expression \"%s\"!", s));
		}
	}
	
	public static int getDereferenceLevel(String s) {
		s = s.trim();
		return (int) s.chars().filter(ch -> ch == Global.DEREFERENCE).count();
	}
	
	public static String fullyDereference(String s) {
		s = s.trim();
		String dereferenced = s.replace(Character.toString(Global.DEREFERENCE), "");
		if (!s.contains(dereferenced)) {
			throw new IllegalArgumentException(String.format("Attempted to fully dereference invalid variable expression \"%s\"!", s));
		}
		return dereferenced.trim();
	}
	
	public static String singlyDereference(String s) {
		s = s.trim();
		String dereference = Character.toString(Global.DEREFERENCE);
		if (s.startsWith(dereference)) {
			return s.substring(dereference.length()).trim();
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to singly dereference invalid variable expression \"%s\"!", s));
		}
	}
	
	public static String charLine(char c, int length) {
		char[] charArray = new char[length];
		Arrays.fill(charArray, c);
		return new String(charArray);
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
