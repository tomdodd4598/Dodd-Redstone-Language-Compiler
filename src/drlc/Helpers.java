package drlc;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.*;

import org.apache.commons.text.translate.*;
import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.lexer.Lexer;
import drlc.node.Node;
import drlc.node.Token;

public class Helpers {
	
	public static @NonNull String readFile(String fileName) throws RuntimeException {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)), Charset.defaultCharset());
		}
		catch (Exception e) {
			throw new RuntimeException(String.format("Failed to read file \"%s\"!", fileName));
		}
	}
	
	public static void writeFile(String fileName, String contents) throws RuntimeException {
		try (PrintWriter out = new PrintWriter(fileName)) {
			out.print(contents);
		}
		catch (Exception e) {
			throw new RuntimeException(String.format("Failed to write file \"%s\"!", fileName));
		}
	}
	
	public static Lexer stringLexer(String str) {
		return new Lexer(new PushbackReader(new StringReader(str), 16384));
	}
	
	private static final Map<String, String> ESCAPE_MAP = new HashMap<>();
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
		ESCAPE_MAP.put("\0", "\\0");
		ESCAPE_MAP.put("\t", "\\t");
		ESCAPE_MAP.put("\b", "\\b");
		ESCAPE_MAP.put("\n", "\\n");
		ESCAPE_MAP.put("\r", "\\r");
		ESCAPE_MAP.put("\f", "\\f");
		ESCAPE_MAP.put("'", "\\'");
		ESCAPE_MAP.put("\"", "\\\"");
		ESCAPE_MAP.put("\\", "\\\\");
		
		Map<CharSequence, CharSequence> unescapeMap = new HashMap<>();
		for (Entry<String, String> entry : ESCAPE_MAP.entrySet()) {
			unescapeMap.put(entry.getValue(), entry.getKey());
		}
		
		UNESCAPE_TRANSLATOR = new AggregateTranslator(new LookupTranslator(unescapeMap), new AsciiUnescaper());
	}
	
	public static String unescapeString(String str) {
		String translated = UNESCAPE_TRANSLATOR.translate(str.substring(1, str.length() - 1));
		if (translated == null) {
			throw new RuntimeException(String.format("Failed to unescape string %s!", str));
		}
		return translated;
	}
	
	public static char unescapeChar(String str) {
		String translated = unescapeString(str);
		if (translated.length() != 1) {
			throw new RuntimeException(String.format("Failed to unescape char %s!", str));
		}
		return translated.charAt(0);
	}
	
	public static String charToString(char c) {
		String str = Character.toString(c);
		if (ESCAPE_MAP.containsKey(str)) {
			str = ESCAPE_MAP.get(str);
		}
		else if (Character.isISOControl(c)) {
			str = "\\" + Integer.toHexString(c);
		}
		return "\'" + str + "\'";
	}
	
	public static @NonNull BigInteger parseBigInt(String str) {
		if (str.endsWith("I") || str.endsWith("i") || str.endsWith("N") || str.endsWith("n")) {
			str = str.substring(0, str.length() - 1);
		}
		str = str.replaceAll("_", "");
		
		String prefix = str.length() > 2 ? lowerCase(str.substring(0, 2)) : null;
		
		if (prefix != null && prefix.equals("0b")) {
			return new BigInteger(str.substring(2), 2);
		}
		else if (prefix != null && prefix.equals("0o")) {
			return new BigInteger(str.substring(2), 8);
		}
		else if (prefix != null && prefix.equals("0x")) {
			return new BigInteger(str.substring(2), 16);
		}
		else {
			return new BigInteger(str);
		}
	}
	
	public static long parseInt(String str) {
		return parseBigInt(str).longValue();
	}
	
	public static String substring(String s, int minLine, int minPos, int maxLine, int maxPos) {
		String[] lines = s.split("\\R", -1);
		if (minLine == maxLine) {
			return lines[minLine].substring(minPos, maxPos);
		}
		else {
			StringBuilder sb = new StringBuilder(lines[minLine].substring(minPos));
			for (int i = 1 + minLine; i < maxLine; ++i) {
				sb.append(lines[i]);
			}
			return sb.append(lines[maxLine].substring(0, maxPos)).toString();
		}
	}
	
	private static Stream<Token> allTokens(Node parseNode) {
		if (parseNode instanceof Token) {
			return Stream.of((Token) parseNode);
		}
		else {
			return Arrays.stream(parseNode.getClass().getDeclaredMethods()).filter(x -> x.getParameterCount() == 0 && Node.class.isAssignableFrom(x.getReturnType())).map(x -> {
				try {
					return x.invoke(parseNode);
				}
				catch (Exception e) {
					return null;
				}
			}).filter(x -> x instanceof Node).flatMap(x -> allTokens((Node) x));
		}
	}
	
	public static Pair<String, String> nodeInfo(Node[] parseNodes) {
		MinMax<Token> mm = new MinMax<>((x, y) -> {
			int lineCompare = Integer.compare(x.getLine(), y.getLine());
			return lineCompare != 0 ? lineCompare : Integer.compare(x.getPos(), y.getPos());
		});
		
		for (Node parseNode : parseNodes) {
			allTokens(parseNode).forEach(x -> mm.update(x));
		}
		
		Token min = mm.min, max = mm.max;
		int minLine = min.getLine(), minPos = min.getPos(), maxLine = max.getLine(), maxPos = max.getPos() + max.getText().length();
		
		String range = String.format("(%s:%s -> %s:%s)", minLine, minPos, maxLine, 1 + maxPos);
		String source = substring(Main.source, minLine - 1, minPos - 1, maxLine - 1, maxPos);
		
		return new Pair<>(range, source);
	}
	
	public static RuntimeException nodeError(Node[] parseNodes, String s, Object... args) {
		StringBuilder sb = new StringBuilder(String.format(s, args));
		if (parseNodes != null && parseNodes.length > 0) {
			Pair<String, String> info = nodeInfo(parseNodes);
			sb.append("\n -> ").append(info.left).append("\n\n").append(info.right).append("\n\n");
		}
		return new IllegalArgumentException(sb.toString());
	}
	
	public static RuntimeException nodeError(ASTNode<?, ?> node, String s, Object... args) {
		return nodeError(node == null ? null : node.parseNodes, s, args);
	}
	
	public static RuntimeException error(String s, Object... args) {
		return new IllegalArgumentException(String.format(s, args));
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
	
	public static boolean isConstantName(String s) {
		return Character.isLetter(s.charAt(0));
	}
	
	public static boolean isVariableName(String s) {
		return Character.isLetter(s.charAt(0));
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
	
	public static String addAddressPrefix(String s) {
		return Global.ADDRESS_OF + s.trim();
	}
	
	public static String addDereferences(String s, int count) {
		return charLine(Global.DEREFERENCE, count) + s.trim();
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
	
	public static DeclaratorInfo builtInParam(@NonNull String name, @NonNull TypeInfo typeInfo) {
		return new DeclaratorInfo(null, new Variable(Global.BUILT_IN_PARAM_PREFIX + name, VariableModifier.DEFAULT_PARAM, typeInfo));
	}
	
	public static <T> String collectionString(Collection<T> collection, String delimiter, String prefix, String suffix) {
		return collection.stream().map(String::valueOf).collect(Collectors.joining(delimiter, prefix, suffix));
	}
	
	public static <T> String listString(Collection<T> collection) {
		return collectionString(collection, Global.LIST_SEPARATOR, Global.LIST_START, Global.LIST_END);
	}
	
	public static <T> String arrayString(Collection<T> collection) {
		return collectionString(collection, Global.LIST_SEPARATOR, Global.ARRAY_START, Global.ARRAY_END);
	}
	
	public static List<TypeInfo> paramTypeInfos(List<DeclaratorInfo> params) {
		return Helpers.map(params, DeclaratorInfo::getTypeInfo);
	}
	
	public static String charLine(char c, int length) {
		char[] charArray = new char[length];
		Arrays.fill(charArray, c);
		return new String(charArray);
	}
	
	public static <T> boolean allEqual(Collection<T> collection) {
		T object = null;
		Iterator<T> iter = collection.iterator();
		while (iter.hasNext()) {
			T next = iter.next();
			if (object == null) {
				object = next;
			}
			else if (!object.equals(next)) {
				return false;
			}
		}
		return true;
	}
	
	public static <T> T[] array(T... objects) {
		return objects;
	}
	
	public static <T> @NonNull List<T> list(T... objects) {
		return new ArrayList<>(Arrays.asList(objects));
	}
	
	public static <T> @NonNull Set<T> set(T... objects) {
		return new HashSet<>(Arrays.asList(objects));
	}
	
	public static <T, U> List<U> map(List<T> list, Function<? super T, ? extends U> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	public static <T, U> Set<U> map(Set<T> set, Function<? super T, ? extends U> function) {
		return set.stream().map(function).collect(Collectors.toSet());
	}
	
	public static String toBinary(long value, int length) {
		if ((length & 31) != 0) {
			value &= ((1 << length) - 1);
		}
		return String.format("%" + length + "s", Long.toBinaryString(value)).replace(' ', '0');
	}
	
	public static String toHex(long value) {
		return (value < 0 ? "-0x" : "0x") + Long.toHexString(Math.abs(value)).toUpperCase(Locale.ROOT);
	}
	
	public static String toHex(long value, int length) {
		return (value < 0 ? "-0x" : "0x") + String.format("%" + length + "s", Long.toHexString(Math.abs(value))).replace(' ', '0').toUpperCase(Locale.ROOT);
	}
	
	public static @Nullable TypeInfo getCommonTypeInfo(List<TypeInfo> types) {
		if (types.isEmpty()) {
			return Main.generator.wildcardPtrTypeInfo;
		}
		else {
			int count = types.size();
			List<List<@NonNull TypeInfo>> typeLists = new ArrayList<>();
			int[] ends = new int[count];
			for (int i = 0; i < count; ++i) {
				List<@NonNull TypeInfo> list = getSuperTypeList(types.get(i));
				typeLists.add(list);
				ends[i] = list.size() - 1;
			}
			return getCommonTypeInfoInternal(typeLists, ends);
		}
	}
	
	private static List<@NonNull TypeInfo> getSuperTypeList(TypeInfo type) {
		List<@NonNull TypeInfo> list = new ArrayList<>();
		while (type != null) {
			list.add(type);
			type = type.getSuperType();
		}
		return list;
	}
	
	private static @Nullable TypeInfo getCommonTypeInfoInternal(List<List<@NonNull TypeInfo>> typeLists, int[] ends) {
		int count = typeLists.size();
		int[] indices = new int[count];
		List<TypeInfo> firstTypeList = typeLists.get(0);
		outer: while (true) {
			boolean success = true;
			TypeInfo firstType = firstTypeList.get(indices[0]);
			for (int i = 1; i < count; ++i) {
				if (!firstType.equals(typeLists.get(i).get(indices[i]))) {
					success = false;
					break;
				}
			}
			if (success) {
				return firstType;
			}
			else {
				for (int i = 0; i < count; ++i) {
					if (indices[i] == ends[i]) {
						indices[i] = 0;
					}
					else {
						++indices[i];
						continue outer;
					}
				}
				break;
			}
		}
		return null;
	}
	
	public static class Pair<L, R> {
		
		public final L left;
		public final R right;
		
		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}
	}
	
	public static class MinMax<T> {
		
		public T min = null, max = null;
		public final Comparator<T> comparator;
		
		public MinMax(Comparator<T> comparator) {
			this.comparator = comparator;
		}
		
		public void update(T value) {
			if (min == null || comparator.compare(min, value) > 0) {
				min = value;
			}
			if (max == null || comparator.compare(max, value) < 0) {
				max = value;
			}
		}
	}
}
