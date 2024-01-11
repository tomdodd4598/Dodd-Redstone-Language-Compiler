package drlc.intermediate.component.type;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;

public class NatTypeInfo extends BasicTypeInfo {
	
	public NatTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability) {
		super(node, referenceMutability, Global.NAT, Main.generator.getWordSize());
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new NatTypeInfo(node, referenceMutability);
	}
	
	@Override
	public boolean isWord() {
		return !isAddress();
	}
}
