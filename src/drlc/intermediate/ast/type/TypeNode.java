package drlc.intermediate.ast.type;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.scope.Scope;

public abstract class TypeNode extends ASTNode<Scope> {
	
	public boolean setTypeInfo = false;
	
	@SuppressWarnings("null")
	protected @NonNull TypeInfo typeInfo = null;
	
	protected TypeNode(Source source) {
		super(source);
	}
	
	public @NonNull TypeInfo getTypeInfo() {
		setTypeInfo();
		return typeInfo;
	}
	
	public void setTypeInfo() {
		if (!setTypeInfo) {
			setTypeInfoInternal();
		}
		setTypeInfo = true;
	}
	
	protected abstract void setTypeInfoInternal();
	
	public abstract void collectTypeDefs(Set<TypeDef> typeDefs);
}
