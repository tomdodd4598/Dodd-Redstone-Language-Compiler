package drlc.intermediate.module;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.TypeDef;
import drlc.intermediate.component.type.TypeInfo;

public interface NominalTypeEntry extends TypeEntry {
	
	public @NonNull TypeDef getTypeDef();
	
	@Override
	public default @NonNull TypeInfo getTypeInfo(ASTNode<?> node) {
		return getTypeDef().getTypeInfo(node, new ArrayList<>());
	}
}
