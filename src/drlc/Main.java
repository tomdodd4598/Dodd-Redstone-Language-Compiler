package drlc;

import java.util.*;
import java.util.stream.Collectors;

import drlc.intermediate.ast.StartNode;
import drlc.intermediate.interpreter.*;
import drlc.lexer.Lexer;
import drlc.node.Start;
import drlc.parser.Parser;

public class Main {
	
	private static class Input {
		
		private final List<String> args;
		private final Set<String> options;
		
		private Input(String[] args) {
			Map<Boolean, List<String>> map = Arrays.stream(args).collect(Collectors.partitioningBy(x -> x.charAt(0) == '-'));
			this.args = map.get(false);
			this.options = map.get(true).stream().map(x -> Helpers.lowerCase(x).substring(1)).collect(Collectors.toSet());
		}
	}
	
	public static void main(String[] args) throws Exception {
		Input input = new Input(args);
		if (input.args.size() != 2) {
			StringBuilder sb = new StringBuilder();
			sb.append("Arguments: [-TARGETS...] OUTPUT INPUT\n");
			sb.append("Targets: ");
			sb.append(Generator.NAME_MAP.entrySet().stream().map(x -> String.format("-%s (%s)\n", x.getKey(), x.getValue())).collect(Collectors.joining(Helpers.charLine(' ', 9))));
			sb.append("Example: -s1 program.drs1 program.drl\n");
			err(sb.toString());
		}
		else {
			try {
				String outputFile = input.args.get(0), inputFile = input.args.get(1);
				if (input.options.contains("all")) {
					outputFile = (outputFile.contains(".") ? outputFile.substring(0, 1 + outputFile.lastIndexOf('.')) : (outputFile + ".")) + "dr";
					for (String target : Generator.NAME_MAP.keySet()) {
						generate(target, outputFile + target, inputFile);
					}
				}
				else {
					for (String target : input.options) {
						generate(target, outputFile, inputFile);
					}
				}
				System.out.print("Finished!\n");
			}
			catch (Exception e) {
				throw e;
			}
		}
	}
	
	private static boolean first = true;
	
	public static Generator generator;
	public static String source;
	
	private static void generate(String target, String outputFile, String inputFile) throws Exception {
		if (!Generator.CONSTRUCTOR_MAP.containsKey(target)) {
			err("ERROR: output target \"%s\" not found!", target);
		}
		
		generator = Generator.CONSTRUCTOR_MAP.get(target).apply(outputFile);
		
		if (first) {
			first = false;
		}
		else {
			System.out.print("\n");
		}
		
		System.out.printf("Compilation target: %s\n", Generator.NAME_MAP.get(target));
		
		long currentTime, previousTime = System.nanoTime();
		
		/* Form parse tree */
		source = Helpers.readFile(inputFile);
		Lexer lexer = Helpers.stringLexer(source);
		Parser parser = new Parser(lexer);
		Start parseTree = parser.parse();
		
		currentTime = System.nanoTime();
		System.out.printf("Parsing time: %.2f ms\n", (currentTime - previousTime) / 1E6);
		previousTime = currentTime;
		
		/* Form AST */
		ParseTreeInterpreter interpreter = new ParseTreeInterpreter();
		parseTree.apply(interpreter);
		StartNode ast = interpreter.ast;
		
		/* Run interpreters */
		ast.setScopes(null);
		ast.defineTypes(null);
		ast.checkTypes(null);
		ast.resolveExpressions(null);
		ast.generate(null);
		
		parseTree.apply(new FirstPassInterpreter(generator, program));
		parseTree.apply(new SecondPassInterpreter(generator, program));
		
		generator.interpretFinalize();
		
		currentTime = System.nanoTime();
		System.out.printf("Interpreting time: %.2f ms\n", (currentTime - previousTime) / 1E6);
		previousTime = currentTime;
		
		/* Generate code */
		generator.generate();
		
		currentTime = System.nanoTime();
		System.out.printf("Generating time: %.2f ms\n", (currentTime - previousTime) / 1E6);
	}
	
	private static void err(String string, Object... args) {
		System.err.print(String.format(string, args));
		System.exit(0);
	}
}
