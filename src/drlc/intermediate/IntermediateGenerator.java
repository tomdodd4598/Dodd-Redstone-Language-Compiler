package drlc.intermediate;

import java.util.List;
import java.util.Map.Entry;

import drlc.*;
import drlc.intermediate.action.Action;
import drlc.intermediate.component.DeclaratorInfo;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInDirectives() {
		super.addBuiltInDirectives();
	}
	
	@Override
	public void addBuiltInConstants() {
		super.addBuiltInConstants();
	}
	
	@Override
	public void addBuiltInVariables() {
		super.addBuiltInVariables();
		
		// Main.rootScope.addVariable(null, Helpers.rootVariable(Global.ARGC, intTypeInfo), false);
		// Main.rootScope.addVariable(null, Helpers.rootVariable(Global.ARGV, charTypeInfo(true, true)), false);
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
	public void generate() {
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		for (Routine routine : Main.rootScope.routineIterable(true)) {
			if (!routine.isBuiltInFunctionRoutine()) {
				if (begin) {
					begin = false;
				}
				else {
					sb.append('\n');
				}
				sb.append(routine.getType()).append(" ").append(routine).append(":\n");
				
				if (!routine.typedefMap.isEmpty()) {
					sb.append("{def}:\n");
					for (Entry<String, TypeInfo> entry : routine.typedefMap.entrySet()) {
						sb.append('\t').append(entry.getKey()).append(Global.TYPE_ANNOTATION_PREFIX).append(" ").append(entry.getValue().routineString()).append('\n');
					}
				}
				
				if (!routine.declaratorList.isEmpty()) {
					sb.append("{dec}:\n");
					for (DeclaratorInfo info : routine.declaratorList) {
						sb.append('\t').append(info.routineString()).append('\n');
					}
				}
				
				List<List<Action>> list = routine.getBodyActionLists();
				for (int i = 0; i < list.size(); ++i) {
					sb.append(Global.BRACE_START).append(i).append(Global.BRACE_END).append(":\n");
					for (Action action : list.get(i)) {
						sb.append('\t').append(action).append('\n');
					}
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
