package drlc.intermediate.ast.pattern;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;

public class BindingPatternNode extends PatternNode {
	
	public final @NonNull DeclaratorNode declaratorNode;
	
	public BindingPatternNode(Source source, @NonNull DeclaratorNode declaratorNode) {
		super(source);
		this.declaratorNode = declaratorNode;
	}
	
	@Override
	protected void collectDeclaratorNodes(List<DeclaratorNode> declaratorNodes) {
		declaratorNodes.add(declaratorNode);
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		declaratorNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		declaratorNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		declaratorNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		declaratorNode.inferredTypeInfo = getTypeInfo();
		declaratorNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		declaratorNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		declaratorNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		declaratorNode.generateIntermediate(this);
		
		if (dataId != null) {
			addBindingAssignmentAction(declaratorNode.declaratorInfo.dataId(), dataId);
		}
	}
}
