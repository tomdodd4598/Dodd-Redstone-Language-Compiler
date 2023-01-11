package drlc;

import java.io.*;
import java.lang.reflect.Constructor;

import drlc.intermediate.interpreter.*;
import drlc.lexer.Lexer;
import drlc.node.Start;
import drlc.parser.Parser;

public class Main {
	
	public static void main(String[] args) {
		if (args.length == 3) {
			run(args[0], args[1], args[2]);
		}
		else {
			StringBuilder builder = new StringBuilder(), types = new StringBuilder();
			builder.append("PARAMETERS: type output input\n");
			for (String s : Generator.CLASS_MAP.keySet()) {
				types.append(", " + s);
			}
			builder.append("TYPES: ").append(types.substring(2));
			builder.append("\nEXAMPLE: s1 program.drs1 program.drl\n");
			err(builder.toString());
		}
	}
	
	private static void run(String type, String output, String input) {
		try {
			/* Find generator */
			type = trim(type);
			if (type.equalsIgnoreCase("all")) {
				for (String t : Generator.CLASS_MAP.keySet()) {
					String o = (output.contains(".") ? output.substring(0, output.lastIndexOf('.') + 1) : output.concat(".")).concat("dr").concat(t);
					generate(t, o, input);
				}
			}
			else {
				generate(type, output, input);
			}
			
			System.out.print("Finished!\n");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean first = true;
	
	private static void generate(String type, String output, String input) {
		try {
			Generator generator = getGenerator(type, output);
			if (generator == null) {
				err("ERROR: chosen output type not found!");
			}
			
			if (first) {
				first = false;
			}
			else {
				System.out.print("\n");
			}
			
			System.out.printf("Compilation target: %s\n", Generator.NAME_MAP.get(type));
			
			long currentTime, previousTime = System.nanoTime();
			
			/* Form our AST */
			PushbackReader reader = new PushbackReader(new FileReader(input), 8192);
			Start ast = new Parser(new Lexer(reader)).parse();
			reader.close();
			
			currentTime = System.nanoTime();
			System.out.printf("Parsing time: %.2f ms\n", (currentTime - previousTime) / 1E6);
			previousTime = currentTime;
			
			/* Run interpreters */
			generator.astInit();
			ast.apply(new FirstPassInterpreter(generator));
			ast.apply(new SecondPassInterpreter(generator));
			generator.astFinalize();
			
			currentTime = System.nanoTime();
			System.out.printf("Interpreting time: %.2f ms\n", (currentTime - previousTime) / 1E6);
			previousTime = currentTime;
			
			/* Generate code */
			generator.generate();
			
			currentTime = System.nanoTime();
			System.out.printf("Generating time: %.2f ms\n", (currentTime - previousTime) / 1E6);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Generator getGenerator(String type, String output) throws Exception {
		return newGenerator(Generator.CLASS_MAP.get(type), output);
	}
	
	private static <T> T newGenerator(Class<T> clazz, String output) throws Exception {
		if (clazz == null) {
			return null;
		}
		else {
			Constructor<T> constructor = clazz.getConstructor(String.class);
			return constructor.newInstance(output);
		}
	}
	
	private static String trim(String arg) {
		return Helpers.lowerCase(arg.replaceAll("-|_", ""));
	}
	
	private static void err(String string) {
		System.err.print(string);
		System.exit(1);
	}
}
