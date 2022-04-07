package drlc;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.*;

public class Preprocessor {
	
	private final String input;
	private final String output;
	
	private String definition, replacement;
	
	public Preprocessor(String input) {
		this.input = input;
		this.output = input.concat("p");
	}
	
	public String output() {
		try {
			String str = new String(Files.readAllBytes(Paths.get(input)), Charset.defaultCharset());
			
			boolean flag = true;
			ReadInfo info = null;
			StringBuilder builder;
			
			while (flag) {
				flag = false;
				
				while ((info = new PreprocessorReader(str).readLineComment()) != null) {
					flag = true;
					builder = new StringBuilder(str);
					builder.replace(info.start, info.end, "");
					str = builder.toString();
				}
				
				while ((info = new PreprocessorReader(str).readBlockComment()) != null) {
					flag = true;
					builder = new StringBuilder(str);
					builder.replace(info.start, info.end, "");
					str = builder.toString();
				}
				
				if ((info = new PreprocessorReader(str).readDef()) != null) {
					flag = true;
					builder = new StringBuilder(str);
					builder.replace(info.start, info.end, "");
					str = builder.toString().replace(definition, replacement);
				}
			}
			
			/*final String doubleLineSeparator = System.lineSeparator() + System.lineSeparator();
			final String tripleLineSeparator = doubleLineSeparator + System.lineSeparator();
			
			while (str.contains(tripleLineSeparator)) {
				str = str.replace(tripleLineSeparator, doubleLineSeparator);
			}*/
			
			PrintWriter out = new PrintWriter(output);
			out.print(str.trim());
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	private class PreprocessorReader extends CharReader {
		
		public PreprocessorReader(String expression) {
			super(expression);
		}
		
		ReadInfo readLineComment() {
			if ((pos = expression.indexOf("//")) < 0) {
				return null;
			}
			
			setChar();
			int directiveStart = pos;
			while (!Helpers.isEndOfLine(ch) && ch >= 0) {
				eat(c -> !Helpers.isEndOfLine(c), false);
			}
			eat();
			
			return new ReadInfo(directiveStart, pos);
		}
		
		ReadInfo readBlockComment() {
			if ((pos = expression.indexOf("/*")) < 0) {
				return null;
			}
			if (expression.indexOf("*/", pos) < 0) {
				return null;
			}
			int directiveStart = pos;
			
			setChar();
			boolean flag = false;
			while (ch >= 0) {
				if (eat('*') && eat('/', false)) {
					flag = true;
					break;
				}
				else {
					eat(c -> true);
				}
			}
			
			return flag ? new ReadInfo(directiveStart, pos) : null;
		}
		
		String nextName() {
			eat();
			if (!Helpers.isValidChar(ch)) {
				return null;
			}
			
			int nameStart = pos;
			while (Helpers.isValidChar(ch)) {
				eat(Helpers::isValidChar);
			}
			
			return substr(nameStart, pos);
		}
		
		ReadInfo readDef() {
			definition = replacement = null;
			
			if ((pos = expression.indexOf(Global.DEF)) < 0) {
				return null;
			}
			if (expression.indexOf(Global.ENDDEF, pos) < 0) {
				return null;
			}
			
			int defEnd = pos + Global.DEF.length();
			backtrack();
			int directiveStart = pos;
			if (ch != Global.DIRECTIVE_PREFIX) {
				return null;
			}
			
			pos = defEnd;
			setChar();
			if ((definition = nextName()) == null) {
				return null;
			}
			
			eat();
			int valStart = pos, lastPrefix, nesting = 0;
			while (true) {
				if ((pos = expression.indexOf(Global.DIRECTIVE_PREFIX, pos)) < 0) {
					return null;
				}
				lastPrefix = pos;
				++pos;
				setChar();
				
				String innerDirective = nextName();
				if (innerDirective == null) {
					return null;
				}
				
				if (innerDirective.equals(Global.DEF)) {
					++nesting;
				}
				else if (innerDirective.equals(Global.ENDDEF)) {
					if (nesting == 0) {
						break;
					}
					else {
						--nesting;
					}
				}
			}
			
			int directiveEnd = pos, valEnd;
			pos = lastPrefix;
			backtrack();
			valEnd = pos + 1;
			
			pos = directiveEnd;
			eat();
			
			replacement = substr(valStart, valEnd);
			return new ReadInfo(directiveStart, pos);
		}
	}
	
	private static class ReadInfo {
		
		final int start, end;
		
		ReadInfo(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}
