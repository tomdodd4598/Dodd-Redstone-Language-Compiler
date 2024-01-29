package drlc.intermediate.ast.type;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Path;
import drlc.intermediate.component.type.TypeDef;

public class NominalTypeNode extends TypeNode {
	
	public final @NonNull Path path;
	
	public NominalTypeNode(Source source, @NonNull Path path) {
		super(source);
		this.path = path;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		setTypeInfo();
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = scope.pathGet(this, path, (x, name) -> x.getTypeInfo(this, name, false));
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {
		scope.pathAction(this, path, (x, name) -> x.collectTypeDefs(this, name, typeDefs));
	}
}
