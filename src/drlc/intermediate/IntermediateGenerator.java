package drlc.intermediate;

import java.util.List;

import drlc.*;
import drlc.intermediate.action.Action;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.routine.*;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInDirectives(ASTNode node) {
		directiveMap.put(Global.SETARGC, new Directive(1, Helpers.array(Helpers.builtInParam("x", intTypeInfo))) {});
	}
	
	@Override
	public void addBuiltInVariables(ASTNode node) {
		super.addBuiltInVariables(node);
		program.rootScope.addVariable(node, new Variable(Global.ARGC, VariableModifier.ROOT_PARAM, intTypeInfo), false);
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
		routine.params = new DeclaratorInfo[2];
		routine.params[0] = new DeclaratorInfo(null, new Variable(Global.ARGC, VariableModifier.ROOT_PARAM, intTypeInfo));
		routine.params[1] = new DeclaratorInfo(null, new Variable(Global.ARGV_PARAM, VariableModifier.ROOT_PARAM, charTypeInfo(2)));
	}
	
	@Override
	public void generate() {
		optimizeIntermediate();
		program.finalizeRoutines();
		
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		for (Routine routine : program.routineMap.values()) {
			if (!routine.isBuiltInFunctionRoutine()) {
				if (begin) {
					begin = false;
				}
				else {
					sb.append('\n');
				}
				sb.append(routine.getType()).append(' ').append(routine).append(":\n");
				List<List<Action>> list = routine.getBodyActionLists();
				for (int i = 0; i < list.size(); ++i) {
					sb.append(Helpers.sectionIdString(i)).append(":\n");
					for (Action action : list.get(i)) {
						sb.append('\t').append(action).append('\n');
					}
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
