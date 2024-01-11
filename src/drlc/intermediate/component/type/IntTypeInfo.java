package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;

public class IntTypeInfo extends BasicTypeInfo {
	
	public IntTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability) {
		super(node, referenceMutability, Global.INT, Main.generator.getWordSize());
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new IntTypeInfo(node, referenceMutability);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
