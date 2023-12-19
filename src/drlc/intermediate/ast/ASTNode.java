package drlc.intermediate.ast;

import drlc.Helpers;
import drlc.Helpers.Pair;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class ASTNode<SCOPE extends Scope, ROUTINE extends Routine> {
	
	public Node[] parseNodes;
	
	public SCOPE scope;
	public ROUTINE routine;
	
	protected ASTNode(Node[] parseNodes) {
		this.parseNodes = parseNodes;
	}
	
	public abstract void setScopes(ASTNode<?, ?> parent);
	
	public abstract void defineTypes(ASTNode<?, ?> parent);
	
	public abstract void declareExpressions(ASTNode<?, ?> parent);
	
	public abstract void checkTypes(ASTNode<?, ?> parent);
	
	public abstract void foldConstants(ASTNode<?, ?> parent);
	
	public abstract void trackFunctions(ASTNode<?, ?> parent);
	
	public abstract void generateIntermediate(ASTNode<?, ?> parent);
	
	public Pair<String, String> nodeInfo() {
		return Helpers.nodeInfo(parseNodes);
	}
	
	protected RuntimeException error(String s, Object... args) {
		return Helpers.nodeError(parseNodes, s, args);
	}
	
	protected RuntimeException castError(String descriptor, TypeInfo actualTypeInfo, TypeInfo expectedTypeInfo) {
		return error("Attempted to use expression of type \"%s\" as %s of incompatible type \"%s\"!", actualTypeInfo, descriptor, expectedTypeInfo);
	}
}
