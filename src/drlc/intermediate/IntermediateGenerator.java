package drlc.intermediate;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.Action;
import drlc.intermediate.component.*;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.routine.*;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInDirectives() {
		directiveMap.put(Global.SETARGC, new Directive(1, Helpers.array(Helpers.builtInParam("x", intTypeInfo))) {
			
			@Override
			public void run(@NonNull Value[] values) {}
		});
	}
	
	@Override
	public void addBuiltInVariables() {
		super.addBuiltInVariables();
		Main.rootScope.addVariable(null, new Variable(Global.ARGC, VariableModifier.ROOT_PARAM, intTypeInfo), false);
	}
	
	@Override
	public int getWordSize() {
		return 8;
	}
	
	@Override
	public int getFunctionSize() {
		return getWordSize();
	}
	
	@Override
	public int getAddressSize() {
		return getWordSize();
	}
	
	@Override
	public void generateRootParams(RootRoutine routine) {
		routine.params = new ArrayList<>();
		routine.params.add(new DeclaratorInfo(null, new Variable(Global.ARGC, VariableModifier.ROOT_PARAM, intTypeInfo)));
		routine.params.add(new DeclaratorInfo(null, new Variable(Global.ARGV, VariableModifier.ROOT_PARAM, charTypeInfo(2))));
	}
	
	@Override
	public void generate() {
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		for (Routine routine : Main.program.routineMap.values()) {
			if (!routine.isBuiltInFunctionRoutine()) {
				if (begin) {
					begin = false;
				}
				else {
					sb.append('\n');
				}
				sb.append(routine.getType()).append(' ').append(routine).append(":\n{decl}:\n");
				for (DeclaratorInfo info : routine.declarations) {
					sb.append('\t').append(info).append('\n');
				}
				List<List<Action>> list = routine.getBodyActionLists();
				for (int i = 0; i < list.size(); ++i) {
					sb.append(Global.SECTION_ID_START).append(i).append(Global.SECTION_ID_END).append(":\n");
					for (Action action : list.get(i)) {
						sb.append('\t').append(action).append('\n');
					}
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
