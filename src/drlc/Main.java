package drlc;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import drlc.intermediate.ast.StartNode;
import drlc.intermediate.routine.RootRoutine;
import drlc.intermediate.scope.RootScope;
import drlc.lexer.*;
import drlc.node.Start;
import drlc.parser.*;

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
			sb.append(Generator.NAME_MAP.entrySet().stream().map(x -> String.format("-%s (%s)\n", x.getKey(), x.getValue())).collect(Collectors.joining("         ")));
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
	
	public static String source;
	
	public static Generator generator;
	
	public static RootScope rootScope;
	public static RootRoutine rootRoutine;
	
	private static boolean first = true;
	
	private static void generate(String target, String outputFile, String inputFile) throws Exception {
		if (!Generator.CONSTRUCTOR_MAP.containsKey(target)) {
			err("ERROR: output target \"%s\" not found!", target);
		}
		
		generator = Generator.CONSTRUCTOR_MAP.get(target).apply(outputFile);
		
		rootScope = new RootScope();
		
		generator.init();
		
		rootRoutine = new RootRoutine();
		rootScope.addRoutine(null, rootRoutine);
		
		if (first) {
			first = false;
		}
		else {
			System.out.print("\n");
		}
		
		System.out.printf("Compilation target: %s\n", Generator.NAME_MAP.get(target));
		
		long[] currentTime = {0}, previousTime = {System.nanoTime()};
		
		Consumer<String> printTime = x -> {
			currentTime[0] = System.nanoTime();
			System.out.printf("%s time: %.2f ms\n", x, (currentTime[0] - previousTime[0]) / 1E6);
			previousTime[0] = currentTime[0];
		};
		
		source = Helpers.readFile(inputFile);
		
		printTime.accept("Reading");
		
		/* Create parse tree */
		Lexer lexer = Helpers.stringLexer(source);
		Parser parser = new Parser(lexer);
		Start parseTree;
		
		try {
			parseTree = parser.parse();
		}
		catch (ParserException e) {
			throw Helpers.nodeError(e.getToken(), e.getMessage());
		}
		catch (LexerException e) {
			throw Helpers.nodeError(e.getToken(), e.getMessage());
		}
		
		printTime.accept("Parsing");
		
		/* Build AST */
		ParseVisitor astBuilder = new ParseVisitor();
		parseTree.apply(astBuilder);
		StartNode ast = astBuilder.ast;
		
		printTime.accept("Building");
		
		/* Traverse AST */
		ast.traverse();
		
		generator.generateRootRoutine();
		rootScope.flattenRoutines();
		generator.optimizeIntermediate();
		rootScope.finalizeRoutines();
		
		printTime.accept("Traversing");
		
		/* Generate code */
		generator.generate();
		
		printTime.accept("Generating");
	}
	
	private static void err(String string, Object... args) {
		System.err.print(String.format(string, args));
		System.exit(0);
	}
}
