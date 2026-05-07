package drlc.intermediate.module;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

public class DirectTypeEntry implements TypeEntry {
	
	protected final @NonNull TypeInfo typeInfo;
	
	public DirectTypeEntry(@NonNull TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	@Override
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node) {
		return typeInfo;
	}
}
