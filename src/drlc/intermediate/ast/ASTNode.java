package drlc.intermediate.ast;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;

public abstract class ASTNode<SCOPE extends Scope> {
	
	public final Source source;
	
	@SuppressWarnings("null")
	public @NonNull SCOPE scope = null;
	@SuppressWarnings("null")
	public @NonNull Routine routine = null;
	
	protected ASTNode(Source source) {
		this.source = source;
	}
	
	public void traverse() {
		setScopes(null);
		defineTypes(null);
		declareExpressions(null);
		defineExpressions(null);
		checkTypes(null);
		foldConstants(null);
		trackFunctions(null);
		generateIntermediate(null);
	}
	
	public abstract void setScopes(ASTNode<?> parent);
	
	public abstract void defineTypes(ASTNode<?> parent);
	
	public abstract void declareExpressions(ASTNode<?> parent);
	
	public abstract void defineExpressions(ASTNode<?> parent);
	
	public abstract void checkTypes(ASTNode<?> parent);
	
	public abstract void foldConstants(ASTNode<?> parent);
	
	public abstract void trackFunctions(ASTNode<?> parent);
	
	public abstract void generateIntermediate(ASTNode<?> parent);
	
	protected RuntimeException error(String s, Object... args) {
		return Helpers.nodeError(this, s, args);
	}
	
	protected RuntimeException castError(String descriptor, TypeInfo actualTypeInfo, TypeInfo expectedTypeInfo) {
		return error("Attempted to use expression of type \"%s\" as %s of incompatible type \"%s\"!", actualTypeInfo, descriptor, expectedTypeInfo);
	}
}
