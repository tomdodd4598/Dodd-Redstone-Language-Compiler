package drlc.intermediate;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.action.Action;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInConstants() {
		super.addBuiltInConstants();
	}
	
	@Override
	public void addBuiltInVariables() {
		super.addBuiltInVariables();
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
	public void addressToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void boolToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void intToAddressCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void natToAddressCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void wordToCharCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addBinaryOpAction(node, intTypeInfo, BinaryOpType.AND, intTypeInfo, target, arg, intValue(255).dataId());
	}
	
	@Override
	public void charToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void generate() {
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		for (Routine routine : Main.rootScope.getRoutines()) {
			if (!routine.isBuiltInFunctionRoutine()) {
				if (begin) {
					begin = false;
				}
				else {
					sb.append('\n');
				}
				sb.append(routine.getType()).append(" ").append(routine).append(":\n");
				
				if (!routine.typeDefMap.isEmpty()) {
					sb.append("{def}:\n");
					for (Entry<String, TypeInfo> entry : routine.typeDefMap.entrySet()) {
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
