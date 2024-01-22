package drlc;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.component.Function;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.RootScope;

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
	
	public static void main(String[] args) throws IOException {
		Input input = new Input(args);
		if (input.args.size() != 2) {
			StringBuilder sb = new StringBuilder();
			sb.append("Arguments: [-TARGETS...] OUTPUT INPUT\n");
			sb.append("Targets: ");
			sb.append(Generator.NAME_MAP.entrySet().stream().map(x -> String.format("-%s (%s)\n", x.getKey(), x.getValue())).collect(Collectors.joining("         ")));
			sb.append("Example: -s1 program.drs1 program.drl\n");
			throw Helpers.error(sb.toString());
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
			catch (IOException e) {
				throw e;
			}
		}
	}
	
	public static Generator generator;
	
	public static String rootFile;
	
	@SuppressWarnings("null")
	public static @NonNull RootScope rootScope;
	
	@SuppressWarnings("null")
	public static @NonNull Routine rootRoutine;
	
	private static boolean first = true;
	
	private static void generate(String target, String outputFile, String inputFile) throws IOException {
		if (!Generator.CONSTRUCTOR_MAP.containsKey(target)) {
			throw Helpers.error("Output target \"%s\" not found!", target);
		}
		
		generator = Generator.CONSTRUCTOR_MAP.get(target).apply(outputFile);
		
		rootFile = inputFile;
		
		rootScope = new RootScope(null);
		
		generator.init();
		
		Function rootFunction = new Function(null, Global.ROOT, false, generator.intTypeInfo, new ArrayList<>(), false, true);
		rootFunction.setRequired(true);
		rootScope.addFunction(null, rootFunction);
		
		rootRoutine = new Routine(rootFunction);
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
		
		Helpers.getAST(inputFile).traverse();
		
		printTime.accept("Frontend");
		
		generator.generateRootRoutine();
		rootScope.flattenRoutines();
		generator.optimizeIntermediate();
		rootScope.finalizeRoutines();
		
		/* Generate code */
		generator.generate();
		
		printTime.accept("Backend");
	}
}
