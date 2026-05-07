package drlc.intermediate.module;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

public interface TypeEntry {
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node);
}
