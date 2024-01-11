package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;

public class CharTypeInfo extends BasicTypeInfo {
	
	public CharTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability) {
		super(node, referenceMutability, Global.CHAR, 1);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new CharTypeInfo(node, referenceMutability);
	}
}
