package drlc.interpret;

import java.util.*;

import drlc.*;
import drlc.generate.Generator;
import drlc.interpret.component.*;
import drlc.interpret.component.info.EvaluationInfo;
import drlc.interpret.component.info.expression.RvalueExpressionInfo;
import drlc.interpret.component.info.type.*;
import drlc.node.Node;

public class Evaluator {
	
	public static EvaluationInfo tryEvaluate(Node node, Generator generator, Scope scope, String expression) {
		try {
			return evaluate(node, generator, scope, expression);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/** Thanks to Boann on stackoverflow for the original! https://stackoverflow.com/a/26227947 */
	public static EvaluationInfo evaluate(Node node, Generator generator, Scope scope, String expression) {
		return new CharReader(expression) {
			
			long parseInteger(String s, int radix) {
				return Long.parseLong(s.trim(), radix);
			}
			
			EvaluationInfo parseExpression() {
				nextChar();
				EvaluationInfo x = parseExpression0();
				if (pos < expression.length()) {
					throw unexpectedCharactor(node, ch);
				}
				else {
					return x;
				}
			}
			
			EvaluationInfo parseExpression0() {
				EvaluationInfo x = parseExpression1();
				while (true) {
					if (eat('&')) {
						if (!eat('&')) {
							throw expectedCharactor(node, '&', ch);
						}
						else {
							x.binaryOp(node, BinaryOpType.LOGICAL_AND, parseExpression1());
						}
					}
					else if (eat('|')) {
						if (!eat('|')) {
							throw expectedCharactor(node, '|', ch);
						}
						else {
							x.binaryOp(node, BinaryOpType.LOGICAL_OR, parseExpression1());
						}
					}
					else if (eat('^')) {
						if (!eat('^')) {
							throw expectedCharactor(node, '^', ch);
						}
						else {
							x.binaryOp(node, BinaryOpType.LOGICAL_XOR, parseExpression1());
						}
					}
					else {
						return x;
					}
				}
			}
			
			EvaluationInfo parseExpression1() {
				EvaluationInfo x = parseExpression2();
				while (true) {
					if (eat('=')) {
						if (!eat('=')) {
							throw expectedCharactor(node, '=', ch);
						}
						else {
							x.binaryOp(node, BinaryOpType.EQUAL_TO, parseExpression2());
						}
					}
					else if (eat('!')) {
						if (!eat('=')) {
							throw expectedCharactor(node, '=', ch);
						}
						else {
							x.binaryOp(node, BinaryOpType.NOT_EQUAL_TO, parseExpression2());
						}
					}
					else {
						return x;
					}
				}
			}
			
			EvaluationInfo parseExpression2() {
				EvaluationInfo x = parseExpression3();
				while (true) {
					if (eat('<')) {
						if (eat('=')) {
							x.binaryOp(node, BinaryOpType.LESS_OR_EQUAL, parseExpression3());
						}
						else if (!eat('<')) {
							x.binaryOp(node, BinaryOpType.LESS_THAN, parseExpression3());
						}
						else {
							throw unexpectedCharactor(node, '<');
						}
					}
					else if (eat('>')) {
						if (eat('=')) {
							x.binaryOp(node, BinaryOpType.MORE_OR_EQUAL, parseExpression3());
						}
						else if (!eat('>')) {
							x.binaryOp(node, BinaryOpType.MORE_THAN, parseExpression3());
						}
						else {
							throw unexpectedCharactor(node, '>');
						}
					}
					else {
						return x;
					}
				}
			}
			
			EvaluationInfo parseExpression3() {
				EvaluationInfo x = parseExpression4();
				while (true) {
					if (eat('+')) {
						x.binaryOp(node, BinaryOpType.PLUS, parseExpression4());
					}
					else if (eat('&')) {
						if (!eat('&')) {
							x.binaryOp(node, BinaryOpType.AND, parseExpression4());
						}
						else {
							backtrack();
							backtrack();
							return x;
						}
					}
					else if (eat('|')) {
						if (!eat('|')) {
							x.binaryOp(node, BinaryOpType.OR, parseExpression4());
						}
						else {
							backtrack();
							backtrack();
							return x;
						}
					}
					else if (eat('^')) {
						if (!eat('^')) {
							x.binaryOp(node, BinaryOpType.XOR, parseExpression4());
						}
						else {
							backtrack();
							backtrack();
							return x;
						}
					}
					else if (eat('-')) {
						x.binaryOp(node, BinaryOpType.MINUS, parseExpression4());
					}
					else {
						return x;
					}
				}
			}
			
			EvaluationInfo parseExpression4() {
				EvaluationInfo x = parseExpression5();
				while (true) {
					if (eat('<')) {
						if (eat('<')) {
							if (eat('/')) {
								x.binaryOp(node, BinaryOpType.CIRCULAR_LEFT_SHIFT, parseExpression5());
							}
							else {
								x.binaryOp(node, BinaryOpType.ARITHMETIC_LEFT_SHIFT, parseExpression5());
							}
						}
						else {
							backtrack();
							return x;
						}
					}
					else if (eat('>')) {
						if (eat('>')) {
							if (eat('>')) {
								x.binaryOp(node, BinaryOpType.LOGICAL_RIGHT_SHIFT, parseExpression5());
							}
							else if (eat('/')) {
								x.binaryOp(node, BinaryOpType.CIRCULAR_RIGHT_SHIFT, parseExpression5());
							}
							else {
								x.binaryOp(node, BinaryOpType.ARITHMETIC_RIGHT_SHIFT, parseExpression5());
							}
						}
						else {
							backtrack();
							return x;
						}
					}
					else {
						return x;
					}
				}
			}
			
			EvaluationInfo parseExpression5() {
				EvaluationInfo x = parseExpression6();
				while (true) {
					if (eat('*')) {
						x.binaryOp(node, BinaryOpType.MULTIPLY, parseExpression6());
					}
					else if (eat('/')) {
						x.binaryOp(node, BinaryOpType.DIVIDE, parseExpression6());
					}
					else if (eat('%')) {
						x.binaryOp(node, BinaryOpType.REMAINDER, parseExpression6());
					}
					else {
						return x;
					}
				}
			}
			
			EvaluationInfo parseExpression6() {
				if (eat('+')) {
					return parseExpression6().unaryOp(node, UnaryOpType.PLUS);
				}
				else if (eat('-')) {
					return parseExpression6().unaryOp(node, UnaryOpType.MINUS);
				}
				else if (eat('~')) {
					return parseExpression6().unaryOp(node, UnaryOpType.COMPLEMENT);
				}
				else if (eat('?')) {
					return parseExpression6().unaryOp(node, UnaryOpType.TO_BOOL);
				}
				else if (eat('!')) {
					return parseExpression6().unaryOp(node, UnaryOpType.NOT);
				}
				else {
					return parseExpression7();
				}
			}
			
			EvaluationInfo parseExpression7() {
				return parseExpression8();
			}
			
			EvaluationInfo parseExpression8() {
				EvaluationInfo x;
				int startPos = this.pos;
				if (eat(Helpers::isDigit)) { // integer
					int radix = 10;
					if (eat('b') || eat('B')) {
						radix = 2;
						startPos = this.pos;
					}
					else if (eat('o') || eat('O')) {
						radix = 8;
						startPos = this.pos;
					}
					else if (eat('x') || eat('X')) {
						radix = 16;
						startPos = this.pos;
					}
					while (Helpers.isHexDigit(ch)) {
						eat(Helpers::isHexDigit);
					}
					long value = parseInteger(substr(startPos, this.pos), radix);
					x = new EvaluationInfo(node, new RvalueExpressionInfo(generator, Global.INT_TYPE_INFO), value);
				}
				else if (eat('\'', false)) { // character
					List<Long> list = new ArrayList<>();
					long value, mult = 1;
					while (!eat('\'', false)) {
						value = ch;
						if (eat('\\', false)) {
							if (eat('x', false)) {
								startPos = this.pos;
								if (eat(Helpers::isHexDigit, false) && Helpers.isHexDigit(ch)) {
									value = parseInteger(substr(startPos, startPos + 2), 16);
								}
								else {
									String invalid = "0x".concat(substr(startPos, startPos + 2));
									throw new IllegalArgumentException(String.format("Evaluator encountered invalid escape sequence \"%s\"! %s", invalid, node));
								}
							}
							else {
								value = ESCAPE_CHARACTER_MAP.getOrDefault(ch, ch);
							}
						}
						list.add(value);
						eat(c -> true, false);
					}
					
					value = 0;
					int size = list.size(), max = Math.min(size, 8);
					for (int i = size - 1; i >= size - max; --i) {
						value += mult * list.get(i);
						mult <<= 8;
					}
					x = new EvaluationInfo(node, new RvalueExpressionInfo(generator, Global.INT_TYPE_INFO), value);
				}
				else if (eat(Helpers::isLetter)) { // constant
					while (Helpers.isValidChar(ch)) {
						eat(Helpers::isValidChar);
					}
					
					String substring = substr(startPos, this.pos);
					
					if (substring.equals(Global.SIZEOF)) { // sizeof
						if (!eat('(')) {
							throw expectedCharactor(node, '(', ch);
						}
						eat();
						
						int referenceLevel = 0;
						while (eat(Global.ADDRESS_OF)) {
							++referenceLevel;
						}
						
						startPos = this.pos;
						if (!eat(Helpers::isLetter)) {
							throw unexpectedCharactor(node, ch);
						}
						while (Helpers.isValidChar(ch)) {
							eat(Helpers::isValidChar);
						}
						x = getTypeSize(node, generator, scope, substr(startPos, this.pos), referenceLevel);
						
						if (!eat(')')) {
							throw expectedCharactor(node, ')', ch);
						}
					}
					else {
						x = getConstant(node, generator, scope, substring);
					}
				}
				else if (eat('(')) { // parentheses
					x = parseExpression0();
					if (!eat(')')) {
						throw expectedCharactor(node, ')', ch);
					}
				}
				else {
					throw unexpectedCharactor(node, ch);
				}
				return x;
			}
		}.parseExpression();
	}
	
	public static final Map<Integer, Integer> ESCAPE_CHARACTER_MAP = new HashMap<>();
	
	private static void putEscapeChar(char raw, char escape) {
		ESCAPE_CHARACTER_MAP.put(Integer.valueOf(raw), Integer.valueOf(escape));
	}
	
	static {
		putEscapeChar('0', '\0');
		putEscapeChar('t', '\t');
		putEscapeChar('b', '\b');
		putEscapeChar('n', '\n');
		putEscapeChar('r', '\r');
		putEscapeChar('f', '\f');
		putEscapeChar('\'', '\'');
		putEscapeChar('\"', '\"');
		putEscapeChar('\\', '\\');
	}
	
	public static EvaluationInfo getConstant(Node node, Generator generator, Scope scope, String str) {
		if (scope == null) {
			throw new IllegalArgumentException(String.format("Could not parse \"%s\"! %s", str, node));
		}
		else {
			Constant constant = scope.getConstant(node, str);
			return new EvaluationInfo(node, new RvalueExpressionInfo(generator, constant.typeInfo), constant.value);
		}
	}
	
	public static EvaluationInfo getTypeSize(Node node, Generator generator, Scope scope, String str, int referenceLevel) {
		if (scope == null) {
			throw new IllegalArgumentException(String.format("Could not parse \"%s%s\"! %s", Helpers.charLine(Global.ADDRESS_OF, referenceLevel), str, node));
		}
		else {
			TypeInfo typeInfo = new BasicTypeInfo(node, scope.getType(node, str), referenceLevel);
			return new EvaluationInfo(node, new RvalueExpressionInfo(generator, Global.INT_TYPE_INFO), typeInfo.getSize(node, generator));
		}
	}
	
	private static IllegalArgumentException expectedCharactor(Node node, char expected, int ch) {
		return new IllegalArgumentException(String.format("Evaluator expected '%c' but encountered '%c'! %s", expected, (char) ch, node));
	}
	
	private static IllegalArgumentException unexpectedCharactor(Node node, int ch) {
		return new IllegalArgumentException(String.format("Evaluator encountered unexpected '%c' character! %s", (char) ch, node));
	}
}
