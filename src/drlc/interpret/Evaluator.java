package drlc.interpret;

import drlc.Helper;
import drlc.generate.Generator;
import drlc.interpret.scope.Scope;
import drlc.node.Node;

public class Evaluator {
	
	public static Integer tryEvaluate(Node node, Generator generator, Scope scope, String expression) {
		try {
			return evaluate(node, generator, scope, expression);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/** Thanks to Boann on stackoverflow for the original! https://stackoverflow.com/a/26227947 */
	public static int evaluate(Node node, Generator generator, Scope scope, String expression) {
		return new Object() {
			
			int pos = -1, ch;
			
			void nextChar() {
				ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
			}
			
			boolean eat(int charToEat) {
				while (ch == ' ' || ch == 9 || ch == 10 || ch == 13) {
					nextChar();
				}
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				else {
					return false;
				}
			}
			
			int parse() {
				nextChar();
				int x = parseExpression();
				if (pos < expression.length()) {
					throw new IllegalArgumentException(String.format("Evaluator encountered unexpected '%c' character! %s", (char) ch, node));
				}
				else {
					return x;
				}
			}
			
			int parseExpression() {
				int x = parseTerm();
				for (;;) {
					if (eat('+')) {
						x += parseTerm(); // plus
					}
					else if (eat('&')) {
						x &= parseTerm(); // and
					}
					else if (eat('|')) {
						x |= parseTerm(); // or
					}
					else if (eat('^')) {
						x ^= parseTerm(); // xor
					}
					else if (eat('-')) {
						x -= parseTerm(); // minus
					}
					else {
						return x;
					}
				}
			}
			
			int parseTerm() {
				int x = parseFactor();
				for (;;) {
					if (eat('<')) {
						if (eat('<')) {
							x <<= parseFactor(); // shift left
						}
						else if (eat('=')) {
							x = x <= parseFactor() ? 1 : 0; // less or equal
						}
						else {
							x = x < parseFactor() ? 1 : 0; // less than
						}
					}
					else if (eat('>')) {
						if (eat('>')) {
							x >>= parseFactor(); // shift right
						}
						else if (eat('=')) {
							x = x >= parseFactor() ? 1 : 0; // more or equal
						}
						else {
							x = x > parseFactor() ? 1 : 0; // more than
						}
					}
					else if (eat('*')) {
						x *= parseFactor(); // multiply
					}
					else if (eat('=')) {
						if (!eat('=')) {
							throw new IllegalArgumentException(String.format("Evaluator expected '=' but encountered '%c'! %s", (char) ch, node));
						}
						else {
							x = x == parseFactor() ? 1 : 0; // equal to
						}
					}
					else if (eat('/')) {
						x /= parseFactor(); // divide
					}
					else if (eat('%')) {
						x %= parseFactor(); // modulo
					}
					else if (eat('!')) {
						if (!eat('=')) {
							throw new IllegalArgumentException(String.format("Evaluator expected '=' but encountered '%c'! %s", (char) ch, node));
						}
						else {
							x = x != parseFactor() ? 1 : 0; // not equal to
						}
					}
					else {
						return x;
					}
				}
			}
			
			int parseFactor() {
				if (eat('+')) {
					return parseFactor(); // unary plus
				}
				if (eat('-')) {
					return -parseFactor(); // unary minus
				}
				if (eat('~')) {
					return generator.inverse(parseFactor()); // unary complement
				}
				if (eat('?')) {
					return parseFactor() == 0 ? 0 : 1; // unary to bool
				}
				if (eat('!')) {
					return parseFactor() == 0 ? 1 : 0; // unary not
				}
				
				int x, startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				}
				else if (Helper.isDigit((char) ch)) { // numbers
					while (Helper.isDigit((char) ch)) {
						nextChar();
					}
					x = Integer.parseInt(expression.substring(startPos, this.pos));
				}
				else if (ch == '_' || Helper.isLetter((char) ch)) { // constants
					while (ch == '_' || Helper.isLetterOrDigit((char) ch)) {
						nextChar();
					}
					x = scope.getConstant(node, expression.substring(startPos, this.pos)).value;
				}
				else {
					throw new IllegalArgumentException(String.format("Evaluator encountered unexpected '%c' character! %s", (char) ch, node));
				}
				
				return x;
			}
		}.parse();
	}
}
