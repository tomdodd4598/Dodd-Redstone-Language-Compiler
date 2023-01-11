package drlc.intermediate;

import java.io.PrintWriter;
import java.util.List;

import drlc.*;
import drlc.intermediate.action.Action;
import drlc.intermediate.component.Variable;
import drlc.intermediate.component.constant.Constant;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.*;
import drlc.node.Node;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInVariables(Node node) {
		super.addBuiltInVariables(node);
		program.rootScope.addVariable(node, new Variable(Global.ARGC, new VariableModifierInfo(false), intTypeInfo));
	}
	
	@Override
	public void handleDirectiveCall(Node node, String name, List<Constant> constantList) {}
	
	@Override
	public int getWordSize() {
		return 8;
	}
	
	@Override
	public int getAddressSize() {
		return getWordSize();
	}
	
	@Override
	public void generateRootParams(RootRoutine routine) {
		routine.params = new DeclaratorInfo[2];
		routine.params[0] = new DeclaratorInfo(null, new Variable(Global.ARGC, new VariableModifierInfo(false), intTypeInfo));
		TypeInfo argvTypeInfo = charTypeInfo(2);
		routine.params[1] = new DeclaratorInfo(null, new Variable("\\argv", new VariableModifierInfo(false), argvTypeInfo));
	}
	
	@Override
	public void generate() {
		optimizeIntermediate();
		program.finalizeRoutines();
		
		StringBuilder builder = new StringBuilder();
		for (Routine routine : program.routineMap.values()) {
			if (!routine.isBuiltInFunctionRoutine()) {
				builder.append('\n').append(routine.getType().toString()).append(' ').append(routine.toString()).append(":\n");
				List<List<Action>> list = routine.getBodyActionLists();
				for (int i = 0; i < list.size(); ++i) {
					builder.append(Helpers.sectionIdString(i)).append(":\n");
					for (Action action : list.get(i)) {
						builder.append('\t').append(action.toString()).append('\n');
					}
				}
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(outputFile);
			out.print(builder.substring(1));
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
