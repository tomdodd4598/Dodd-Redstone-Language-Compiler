package drlc.intermediate.component;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

@FunctionalInterface
public interface TypeInfoFunction {
	
	public @NonNull TypeInfo create(ASTNode<?> node, List<Boolean> referenceMutability);
}
