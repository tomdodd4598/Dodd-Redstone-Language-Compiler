package drlc;

import java.util.function.Predicate;

public class CharReader {
	
	public final String expression;
	public int pos = -1, ch;
	
	public CharReader(String expression) {
		this.expression = expression;
	}
	
	public String substr(int begin, int end) {
		return expression.substring(begin, Math.min(end, expression.length()));
	}
	
	public void setChar() {
		ch = (pos < expression.length()) ? expression.charAt(pos) : -1;
	}
	
	public void nextChar() {
		ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
	}
	
	public void backtrack() {
		backtrack(true);
	}
	
	public void backtrack(boolean passWhitespace) {
		do {
			ch = (--pos < expression.length()) ? expression.charAt(pos) : -1;
		}
		while (passWhitespace && Helpers.isWhitespace(ch));
	}
	
	public boolean eat() {
		return eat(c -> false, true);
	}
	
	public boolean eat(int charToEat) {
		return eat(charToEat, true);
	}
	
	public boolean eat(int charToEat, boolean eatWhitespace) {
		return eat(c -> c.equals(charToEat), eatWhitespace);
	}
	
	public boolean eat(Predicate<Integer> eatPredicate) {
		return eat(eatPredicate, true);
	}
	
	public boolean eat(Predicate<Integer> eatPredicate, boolean eatWhitespace) {
		while (eatWhitespace && Helpers.isWhitespace(ch)) {
			nextChar();
		}
		if (eatPredicate.test(ch)) {
			nextChar();
			return true;
		}
		else {
			return false;
		}
	}
}
