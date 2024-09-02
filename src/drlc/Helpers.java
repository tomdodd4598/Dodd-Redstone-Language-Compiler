package drlc;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.*;

import org.apache.commons.text.translate.*;
import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;
import drlc.lexer.*;
import drlc.node.*;
import drlc.node.Node;
import drlc.parser.*;

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
	
	public static StartNode getAST(String fileName) throws IOException {
		String contents = Helpers.readFile(fileName);
		Lexer lexer = Helpers.stringLexer(contents);
		
		/* Create parse tree */
		Parser parser = new Parser(lexer);
		Start parseTree;
		
		try {
			parseTree = parser.parse();
		}
		catch (ParserException e) {
			throw Helpers.sourceError(new Source(fileName, contents, e.getToken()), e.getMessage());
		}
		catch (LexerException e) {
			throw Helpers.sourceError(new Source(fileName, contents, e.getToken()), e.getMessage());
		}
		
		/* Build AST */
		Visitor visitor = new Visitor(fileName, contents);
		parseTree.apply(visitor);
		
		return visitor.ast;
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
		else if (c > 0x7F || Character.isISOControl(c)) {
			str = "\\x" + upperCase(String.format("%2s", Integer.toHexString(c)).replace(' ', '0'));
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
	
	public static String substring(String[] lines, int minLine, int minPos, int maxLine, int maxPos) {
		if (minLine == maxLine) {
			return lines[minLine].substring(minPos, maxPos);
		}
		else {
			StringBuilder sb = new StringBuilder(lines[minLine].substring(minPos)).append("\n");
			for (int i = 1 + minLine; i < maxLine; ++i) {
				sb.append(lines[i]).append("\n");
			}
			return sb.append(lines[maxLine].substring(0, maxPos)).toString();
		}
	}
	
	private static Stream<Token> allTokens(Node parseNode) {
		if (parseNode instanceof Token token) {
			return Stream.of(token);
		}
		else {
			return Arrays.stream(parseNode.getClass().getDeclaredMethods()).filter(x -> {
				if (x.getParameterCount() > 0) {
					return false;
				}
				else {
					Class<?> returnType = x.getReturnType();
					return Node.class.isAssignableFrom(returnType) || LinkedList.class.isAssignableFrom(returnType);
				}
			}).map(x -> {
				try {
					return x.invoke(parseNode);
				}
				catch (Exception e) {
					return null;
				}
			}).filter(x -> x instanceof Node || x instanceof LinkedList).flatMap(x -> {
				if (x instanceof Node node) {
					return allTokens(node);
				}
				else {
					return ((LinkedList<?>) x).stream().filter(y -> y instanceof Node).flatMap(y -> allTokens((Node) y));
				}
			});
		}
	}
	
	public static Pair<String, String> sourceInfo(Source source) {
		MinMax<Token> mm = new MinMax<>((x, y) -> {
			int lineCompare = Integer.compare(x.getLine(), y.getLine());
			return lineCompare != 0 ? lineCompare : Integer.compare(x.getPos(), y.getPos());
		});
		
		for (Node parseNode : source.parseNodes) {
			allTokens(parseNode).forEach(x -> mm.update(x));
		}
		
		Token min = mm.min, max = mm.max;
		int minLine = min.getLine(), minPos = min.getPos(), maxLine = max.getLine(), maxPos = max.getPos() + max.getText().length();
		
		String range = String.format("%s: %s:%s -> %s:%s", source.fileName, minLine, minPos, maxLine, maxPos);
		String contents = substring(source.contents.split("\\R", -1), minLine - 1, minPos - 1, maxLine - 1, maxPos - 1);
		return new Pair<>(range, contents);
	}
	
	public static RuntimeException sourceError(Source source, String s, Object... args) {
		StringBuilder sb = new StringBuilder(String.format(s, args));
		if (source != null && source.parseNodes.length > 0) {
			Pair<String, String> info = sourceInfo(source);
			sb.append("\n\n").append(info.left).append("\n\n").append(info.right).append("\n");
		}
		return new IllegalArgumentException(sb.toString());
	}
	
	public static RuntimeException nodeError(ASTNode<?> node, String s, Object... args) {
		return sourceError(node == null ? null : node.source, s, args);
	}
	
	public static RuntimeException error(String s, Object... args) {
		return new IllegalArgumentException(String.format(s, args));
	}
	
	public static String lowerCase(String s) {
		return s.toLowerCase(Locale.ROOT);
	}
	
	public static String upperCase(String s) {
		return s.toUpperCase(Locale.ROOT);
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
	
	public static @NonNull String scopeStringPrefix(@Nullable Scope scope) {
		return scope == null || scope.equals(Main.rootScope) ? "" : Global.POINTY_START + scope.globalId + Global.POINTY_END + " ";
	}
	
	public static String dereferenceString(int count) {
		return String.join("", Collections.nCopies(count, Global.DEREFERENCE));
	}
	
	public static @NonNull DeclaratorInfo builtInDeclarator(@NonNull String name, @NonNull TypeInfo typeInfo) {
		return new DeclaratorInfo(new Variable(name, VariableModifier.BUILT_IN, typeInfo));
	}
	
	public static @NonNull Function builtInFunction(@NonNull String name, @NonNull TypeInfo returnTypeInfo, DeclaratorInfo... params) {
		return new Function(null, name, true, returnTypeInfo, Arrays.asList(params), false, true);
	}
	
	public static <T> String collectionString(Collection<T> collection, String delimiter, String prefix, String suffix) {
		return collection.stream().map(String::valueOf).collect(Collectors.joining(delimiter, prefix, suffix));
	}
	
	public static <T> String listString(Collection<T> collection) {
		return collectionString(collection, Global.LIST_SEPARATOR, Global.LIST_START, Global.LIST_END);
	}
	
	public static <T> String captureString(Collection<T> collection) {
		return collectionString(collection, Global.LIST_SEPARATOR, Global.BRACE_START, Global.BRACE_END);
	}
	
	public static <T> String arrayString(Collection<T> collection) {
		return collectionString(collection, Global.LIST_SEPARATOR, Global.ARRAY_START, Global.ARRAY_END);
	}
	
	public static <T> String tupleString(Collection<T> collection) {
		return collection.size() == 1 ? Global.LIST_START + collection.iterator().next() + Global.TUPLE_SINGLE_END : listString(collection);
	}
	
	public static <T> String structString(String typeDef, Collection<T> collection) {
		return typeDef + " " + collectionString(collection, Global.LIST_SEPARATOR, Global.BRACE_START, Global.BRACE_END);
	}
	
	public static <T> T[] array(T... objects) {
		return objects;
	}
	
	public static <T> List<T> arrayList(T... objects) {
		List<T> out = new ArrayList<>();
		for (T object : objects) {
			out.add(object);
		}
		return out;
	}
	
	public static <T> int sumToInt(Collection<T> collection, java.util.function.ToIntFunction<? super T> function) {
		return collection.stream().mapToInt(function).sum();
	}
	
	public static <T, U> List<U> map(List<T> list, java.util.function.Function<? super T, ? extends U> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	public static <T, U> Set<U> map(Set<T> set, java.util.function.Function<? super T, ? extends U> function) {
		return set.stream().map(function).collect(Collectors.toSet());
	}
	
	public static String toBinary(long value, int length) {
		if ((length & 31) != 0) {
			value &= ((1 << length) - 1);
		}
		return String.format("%" + length + "s", Long.toBinaryString(value)).replace(' ', '0');
	}
	
	public static String toHex(long value) {
		return (value < 0 ? "-0x" : "0x") + upperCase(Long.toHexString(Math.abs(value)));
	}
	
	public static String toHex(long value, int length) {
		return (value < 0 ? "-0x" : "0x") + upperCase(String.format("%" + length + "s", Long.toHexString(Math.abs(value))).replace(' ', '0'));
	}
	
	public static @Nullable TypeInfo getCommonTypeInfo(ASTNode<?> node, List<TypeInfo> typeInfos) {
		if (typeInfos.isEmpty()) {
			return null;
		}
		
		TypeInfo firstTypeInfo = typeInfos.get(0);
		int firstReferenceLevel = firstTypeInfo.getReferenceLevel();
		if (!typeInfos.stream().allMatch(x -> x.equalsOther(firstTypeInfo, true)) || !typeInfos.stream().allMatch(x -> x.getReferenceLevel() == firstReferenceLevel)) {
			return null;
		}
		
		List<Boolean> commonReferenceMutability = IntStream.range(0, firstReferenceLevel).mapToObj(x -> typeInfos.stream().allMatch(y -> y.referenceMutability.get(x))).collect(Collectors.toList());
		return firstTypeInfo.copy(node, commonReferenceMutability);
	}
	
	public static class Pair<L, R> {
		
		public final L left;
		public final R right;
		
		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(left, right);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Pair<?, ?> other) {
				return Objects.equals(left, other.left) && Objects.equals(right, other.right);
			}
			else {
				return false;
			}
		}
		
		@Override
		public String toString() {
			return "(" + left + ", " + right + ")";
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
