package drlc;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import org.apache.commons.text.translate.*;
import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.component.*;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.TypeInfo;

public class Helpers {
	
	private static final CharSequenceTranslator UNESCAPE_TRANSLATOR;
	
	private static class AsciiUnescaper extends CharSequenceTranslator {
		
		@Override
		public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
			if (input.charAt(index) == '\\' && index + 1 < input.length() && input.charAt(index + 1) == 'x') {
				if (index + 4 <= input.length()) {
					try {
						out.write(Integer.parseInt(input.subSequence(index + 2, index + 4).toString(), 16));
					}
					catch (final NumberFormatException e) {
						throw new IllegalArgumentException(String.format("Unexpectedly encountered invalid ASCII hex escape sequence \"%s\"!", input.subSequence(index, index + 4)), e);
					}
					return 4;
				}
				throw new IllegalArgumentException(String.format("Unexpectedly encountered invalid ASCII hex escape sequence!"));
			}
			return 0;
		}
	}
	
	static {
		Map<CharSequence, CharSequence> unescapeMap = new HashMap<>();
		unescapeMap.put("\\0", "\0");
		unescapeMap.put("\\t", "\t");
		unescapeMap.put("\\b", "\b");
		unescapeMap.put("\\n", "\n");
		unescapeMap.put("\\r", "\r");
		unescapeMap.put("\\f", "\f");
		unescapeMap.put("\\'", "'");
		unescapeMap.put("\\\"", "\"");
		unescapeMap.put("\\\\", "\\");
		
		UNESCAPE_TRANSLATOR = new AggregateTranslator(new LookupTranslator(Collections.unmodifiableMap(unescapeMap)), new AsciiUnescaper());
	}
	
	public static @NonNull Character unescapeChar(String str) {
		String unescape = unescapeString(str);
		if (unescape.length() != 1) {
			throw new IllegalArgumentException(String.format("Character value %s is invalid!", str));
		}
		return unescape.charAt(0);
	}
	
	public static @NonNull String unescapeString(String str) {
		String parsed = UNESCAPE_TRANSLATOR.translate(str.substring(1, str.length() - 1));
		if (parsed == null) {
			throw new RuntimeException(String.format("Failed to unescape string \"%s\"!", str));
		}
		return parsed;
	}
	
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
	
	public static <T> void addSubset(Set<T> subSet, Set<T> superSet, T... values) {
		Arrays.asList(values).stream().filter(superSet::contains).forEach(value -> subSet.add(value));
	}
	
	public static <K, V> void addSubmap(Map<K, V> subMap, Map<K, V> superMap, K... keys) {
		Arrays.asList(keys).stream().filter(superMap::containsKey).forEach(k -> subMap.put(k, superMap.get(k)));
	}
	
	public static int shortCompareUnsigned(short x, short y) {
		return Short.compare((short) (x + Short.MIN_VALUE), (short) (y + Short.MIN_VALUE));
	}
	
	public static short shortDivideUnsigned(short dividend, short divisor) {
		return (short) (Short.toUnsignedInt(dividend) / Short.toUnsignedInt(divisor));
	}
	
	public static short shortRemainderUnsigned(short dividend, short divisor) {
		return (short) (Short.toUnsignedInt(dividend) % Short.toUnsignedInt(divisor));
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
	
	public static String addDereferences(String s, int count) {
		return charLine(Global.DEREFERENCE, count).concat(s.trim());
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
	
	public static String removeAllDereferences(String s) {
		s = s.trim();
		String dereferenced = s.replace(Character.toString(Global.DEREFERENCE), "");
		if (!s.contains(dereferenced)) {
			throw new IllegalArgumentException(String.format("Attempted to fully dereference invalid variable expression \"%s\"!", s));
		}
		return dereferenced.trim();
	}
	
	public static boolean isDiscardParam(String discardParamString) {
		return discardParamString.startsWith(Global.DISCARD_PARAM_PREFIX);
	}
	
	public static DeclaratorInfo builtInParam(String name, TypeInfo typeInfo) {
		return new DeclaratorInfo(null, new Variable(Global.BUILT_IN_PARAM_PREFIX.concat(name), new VariableModifierInfo(false), typeInfo));
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
			typeInfos[i] = params[i].getTypeInfo();
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
	
	public static class Dummy {
		
		public static final Dummy INSTANCE = new Dummy();
	}
}
