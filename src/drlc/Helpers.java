package drlc;

import java.util.*;
import java.util.function.Function;

import drlc.interpret.component.*;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.type.TypeInfo;

public class Helpers {
	
	public static boolean isEndOfLine(char c) {
		return c == 10 || c == 13;
	}
	
	public static boolean isWhitespace(char c) {
		return c == ' ' || c == 9 || isEndOfLine(c);
	}
	
	public static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	public static boolean isValidChar(char c) {
		return isDigit(c) || isLetter(c) || c == '_';
	}
	
	public static boolean isEndOfLine(Integer c) {
		return isEndOfLine((char) c.intValue());
	}
	
	public static boolean isWhitespace(Integer c) {
		return isWhitespace((char) c.intValue());
	}
	
	public static boolean isHexDigit(char c) {
		return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}
	
	public static boolean isDigit(Integer c) {
		return isDigit((char) c.intValue());
	}
	
	public static boolean isLetter(Integer c) {
		return isLetter((char) c.intValue());
	}
	
	public static boolean isValidChar(Integer c) {
		return isValidChar((char) c.intValue());
	}
	
	public static boolean isHexDigit(Integer c) {
		return isHexDigit((char) c.intValue());
	}
	
	public static String lowerCase(String s) {
		return s.toLowerCase(Locale.ROOT);
	}
	
	public static String removeWhitespace(String s) {
		return s.replaceAll("\\s+", "");
	}
	
	public static int countSubstrings(String s, String substring, int fromIndex, int toIndex) {
		if (substring.isEmpty()) {
			return -1;
		}
		int count = 0;
		
		while (fromIndex != -1 && fromIndex < toIndex) {
			fromIndex = s.indexOf(substring, fromIndex);
			if (fromIndex != -1) {
				++count;
				fromIndex += substring.length();
			}
		}
		return count;
	}
	
	public static String removeParentheses(String s) {
		s = removeWhitespace(s);
		if (s.startsWith("(") && s.endsWith(")")) {
			return removeParentheses(s.substring(1, s.length() - 1));
		}
		else {
			return s;
		}
	}
	
	public static boolean isConstantName(String s) {
		return Character.isLetter(s.charAt(0));
	}
	
	public static boolean isVariableName(String s) {
		return Character.isLetter(s.charAt(0));
	}
	
	public static <T> void subSet(Set<T> subSet, Set<T> superSet, T... values) {
		Arrays.asList(values).stream().filter(superSet::contains).forEach(value -> subSet.add(value));
	}
	
	public static <K, V> void subMap(Map<K, V> subMap, Map<K, V> superMap, K... keys) {
		Arrays.asList(keys).stream().filter(superMap::containsKey).forEach(k -> subMap.put(k, superMap.get(k)));
	}
	
	public static String immediateValueString(Long immediateValue) {
		return Global.IMMEDIATE.concat(immediateValue.toString());
	}
	
	public static String immediateValueString(String immediateValueText) {
		return Global.IMMEDIATE.concat(immediateValueText);
	}
	
	public static boolean isImmediateValue(String immediateValueString) {
		return immediateValueString.startsWith(Global.IMMEDIATE);
	}
	
	public static Long parseImmediateValue(String immediateValueString) {
		if (isImmediateValue(immediateValueString)) {
			return Long.parseLong(immediateValueString.substring(Global.IMMEDIATE.length()));
		}
		else {
			return null;
		}
	}
	
	public static DataId immediateDataId(Long immediateValue) {
		return new DataId(Helpers.immediateValueString(immediateValue), null);
	}
	
	public static String regIdString(long regId) {
		return Global.REG.concat(Long.toString(regId));
	}
	
	public static boolean isRegId(String regIdString) {
		return regIdString.startsWith(Global.REG);
	}
	
	public static Long parseRegId(String regIdString) {
		if (isRegId(regIdString)) {
			return Long.parseLong(regIdString.substring(Global.REG.length()));
		}
		else {
			return null;
		}
	}
	
	public static DataId regDataId(long regId) {
		return new DataId(regIdString(regId), null);
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
	
	public static String statementLabelString(String labelName) {
		return Global.STATEMENT_LABEL_PREFIX.concat(labelName);
	}
	
	public static boolean isStatementLabel(String statementLabelString) {
		return statementLabelString.startsWith(Global.STATEMENT_LABEL_PREFIX);
	}
	
	public static String parseLabelName(String statementLabelString) {
		if (isStatementLabel(statementLabelString)) {
			return statementLabelString.substring(Global.STATEMENT_LABEL_PREFIX.length());
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
	
	public static String addAddressPrefix(String s) {
		return Character.toString(Global.ADDRESS_OF).concat(s.trim());
	}
	
	public static String removeAddressPrefix(String s) {
		s = s.trim();
		String addressOf = Character.toString(Global.ADDRESS_OF);
		if (s.startsWith(addressOf)) {
			return s.substring(addressOf.length()).trim();
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to remove the \"address of\" prefix of invalid variable expression \"%s\"!", s));
		}
	}
	
	public static int getDereferenceCount(String s) {
		return (int) s.trim().chars().filter(ch -> ch == Global.DEREFERENCE).count();
	}
	
	public static String removeAllDereferences(String s) {
		s = s.trim();
		String dereferenced = s.replace(Character.toString(Global.DEREFERENCE), "");
		if (!s.contains(dereferenced)) {
			throw new IllegalArgumentException(String.format("Attempted to fully dereference invalid variable expression \"%s\"!", s));
		}
		return dereferenced.trim();
	}
	
	public static String removeDereference(String s) {
		s = s.trim();
		String dereference = Character.toString(Global.DEREFERENCE);
		if (s.startsWith(dereference)) {
			return s.substring(dereference.length()).trim();
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to singly dereference invalid variable expression \"%s\"!", s));
		}
	}
	
	public static String addDereferences(String s, int count) {
		return charLine(Global.DEREFERENCE, count).concat(s.trim());
	}
	
	public static boolean isDiscardParam(String discardParamString) {
		return discardParamString.startsWith(Global.DISCARD_PARAM_PREFIX);
	}
	
	public static DeclaratorInfo builtInParam(String name, TypeInfo typeInfo) {
		return new DeclaratorInfo(null, new Variable(Global.BUILT_IN_PARAM_PREFIX.concat(name), new VariableModifierInfo(true, false, false), typeInfo), 0);
	}
	
	public static DeclaratorInfo[] params(DeclaratorInfo... params) {
		return params;
	}
	
	private static <T> void appendParamsInternal(StringBuilder builder, Function<T, String> toString, String separator, T[] params) {
		int l = params.length;
		builder.append('(');
		if (l > 0) {
			for (int i = 0; i < l - 1; ++i) {
				builder.append(toString.apply(params[i])).append(separator);
			}
			builder.append(toString.apply(params[l - 1]));
		}
		builder.append(')');
	}
	
	public static <T> void appendParams(StringBuilder builder, DeclaratorInfo[] params) {
		appendParamsInternal(builder, param -> param.toDeclarationString(), Global.PARAM_SEPARATOR, params);
	}
	
	public static <T> void appendArgs(StringBuilder builder, DataId[] args) {
		appendParamsInternal(builder, arg -> arg.raw, Global.ARG_SEPARATOR, args);
	}
	
	public static TypeInfo[] paramTypeInfoArray(DeclaratorInfo[] params) {
		TypeInfo[] typeInfos = new TypeInfo[params.length];
		for (int i = 0; i < params.length; ++i) {
			typeInfos[i] = params[i].typeInfo;
		}
		return typeInfos;
	}
	
	public static String charLine(char c, int length) {
		char[] charArray = new char[length];
		Arrays.fill(charArray, c);
		return new String(charArray);
	}
	
	public static String toBinary(int value, int length) {
		if ((length & 31) != 0) {
			value &= ((1 << length) - 1);
		}
		return String.format("%" + length + "s", Integer.toBinaryString(value)).replace(' ', '0');
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
