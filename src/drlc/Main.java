package drlc;

import java.io.*;
import java.lang.reflect.Constructor;

import drlc.generate.Generator;
import drlc.interpret.Interpreter;
import drlc.lexer.Lexer;
import drlc.node.Start;
import drlc.parser.Parser;

public class Main {
	
	public static void main(String[] args) {
		if (args.length == 3) {
			run("", "", args[0], args[1], args[2]);
		}
		else if (args.length == 4) {
			run("", args[0], args[1], args[2], args[3]);
		}
		else if (args.length == 5) {
			run(args[0], args[1], args[2], args[3], args[4]);
		}
		else {
			StringBuilder builder = new StringBuilder(), types = new StringBuilder();
			builder.append("PARAMETERS: [\"dio\"] [\"dmo\"] type output input\n");
			builder.append("INFO: optionally disable intermediate and machine code optimization with \"dio\" and \"dmo\"\n");
			for (String s : Generator.CLASS_MAP.keySet()) {
				types.append(", " + s);
			}
			builder.append("TYPES: ").append(types.substring(2));
			builder.append("\nEXAMPLE: s1 code.drs1 code.drl\n");
			err(builder.toString());
		}
	}
	
	private static void run(String dio, String dmo, String type, String output, String input) {
		try {
			/* Optimization setting */
			boolean intermediateOptimization = !trim(dio).equals("dio") && !trim(dmo).equals("dio");
			if (!intermediateOptimization) {
				System.out.print("INFO: intermediate code optimization was disabled!\n");
			}
			
			boolean machineOptimization = !trim(dio).equals("dmo") && !trim(dmo).equals("dmo");
			if (!machineOptimization) {
				System.out.print("INFO: machine code optimization was disabled!\n");
			}
			
			/* Find generator */
			type = trim(type);
			if (type.equalsIgnoreCase("all")) {
				for (String t : Generator.CLASS_MAP.keySet()) {
					String o = (output.contains(".") ? output.substring(0, output.lastIndexOf('.') + 1) : output.concat(".")).concat("dr").concat(t);
					generate(t, intermediateOptimization, machineOptimization, o, input);
				}
			}
			else {
				generate(type, intermediateOptimization, machineOptimization, output, input);
			}
			
			System.out.print("Finished!\n");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean first = true;
	
	private static void generate(String type, boolean intermediateOptimization, boolean machineOptimization, String output, String input) {
		try {
			Generator generator = getGenerator(type, intermediateOptimization, machineOptimization, output);
			if (generator == null) {
				err("ERROR: chosen output type not found!");
			}
			
			if (first) {
				first = false;
			}
			else {
				System.out.print("\n");
			}
			
			System.out.print(String.format("Compilation target: %s\n", Generator.NAME_MAP.get(type)));
			
			long currentTime = System.nanoTime(), previousTime = currentTime;
			
			/* Form our AST */
			Start ast = new Parser(new Lexer(new PushbackReader(new FileReader(input), 8192))).parse();
			
			currentTime = System.nanoTime();
			System.out.print(String.format("Parsing time: %.2f ms\n", (currentTime - previousTime) / 1E6));
			previousTime = currentTime;
			
			/* Run interpreter */
			Interpreter interpreter = new Interpreter(generator);
			ast.apply(interpreter);
			
			currentTime = System.nanoTime();
			System.out.print(String.format("Interpreting time: %.2f ms\n", (currentTime - previousTime) / 1E6));
			previousTime = currentTime;
			
			/* Generate code */
			interpreter.generate();
			
			System.out.print(String.format("Generating time: %.2f ms\n", (System.nanoTime() - previousTime) / 1E6));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Generator getGenerator(String type, boolean intermediateOptimization, boolean machineOptimization, String output) throws Exception {
		return newGenerator(Generator.CLASS_MAP.get(type), intermediateOptimization, machineOptimization, output);
	}
	
	private static <T> T newGenerator(Class<T> clazz, boolean intermediateOptimization, boolean machineOptimization, String output) throws Exception {
		if (clazz == null) {
			return null;
		}
		else {
			Constructor<T> constructor = clazz.getConstructor(Boolean.class, Boolean.class, String.class);
			return constructor.newInstance(intermediateOptimization, machineOptimization, output);
		}
	}
	
	private static String trim(String arg) {
		return arg.replaceAll("-|_", "");
	}
	
	private static void err(String string) {
		System.err.print(string);
		System.exit(1);
	}
}
