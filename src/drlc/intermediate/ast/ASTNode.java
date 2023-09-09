package drlc.intermediate.ast;

import java.util.List;

import drlc.Helpers;
import drlc.Helpers.Pair;
import drlc.intermediate.action.Action;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class ASTNode {
	
	public Node[] parseNodes;
	
	public Scope scope;
	public Routine routine;
	public int sectionId;
	
	protected ASTNode(Node[] parseNodes) {
		this.parseNodes = parseNodes;
	}
	
	public abstract void setScopes(ASTNode parent);
	
	public abstract void defineTypes(ASTNode parent);
	
	public abstract void declareExpressions(ASTNode parent);
	
	public abstract void checkTypes(ASTNode parent);
	
	public abstract void foldConstants(ASTNode parent);
	
	public abstract void generateIntermediate(ASTNode parent);
	
	public List<Action> getSection() {
		return routine.getSection(sectionId);
	}
	
	public Pair<String, String> nodeInfo() {
		return Helpers.nodeInfo(parseNodes);
	}
	
	public RuntimeException error(String s, Object... args) {
		return Helpers.nodeError(parseNodes, s, args);
	}
	
	public RuntimeException castError(String descriptor, TypeInfo actualTypeInfo, TypeInfo expectedTypeInfo) {
		return error("Attempted to use expression of type \"%s\" as %s of incompatible type \"%s\"!", actualTypeInfo, descriptor, expectedTypeInfo);
	}
}
