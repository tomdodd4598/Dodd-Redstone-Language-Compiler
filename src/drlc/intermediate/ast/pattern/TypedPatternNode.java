package drlc.intermediate.ast.pattern;

import java.util.List;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;

public class TypedPatternNode extends PatternNode {
	
	public final @NonNull PatternNode patternNode;
	public final @NonNull TypeNode typeNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public TypedPatternNode(Source source, @NonNull PatternNode patternNode, @NonNull TypeNode typeNode) {
		super(source);
		this.patternNode = patternNode;
		this.typeNode = typeNode;
	}
	
	@Override
	public boolean canDeclareExcludingInitializer() {
		return patternNode instanceof BindingPatternNode;
	}
	
	@Override
	public @Nullable TypeInfo getExplicitTypeInfo() {
		return typeNode.getTypeInfo();
	}
	
	@Override
	protected void collectDeclaratorNodes(List<DeclaratorNode> declaratorNodes) {
		patternNode.collectDeclaratorNodes(declaratorNodes);
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
		patternNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		typeNode.defineTypes(this);
		patternNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
		patternNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		typeNode.defineExpressions(this);
		
		@NonNull TypeInfo inputTypeInfo = getTypeInfo();
		typeInfo = typeNode.getTypeInfo();
		if (!inputTypeInfo.canImplicitCastTo(typeInfo)) {
			throw castError("pattern value", inputTypeInfo, typeInfo);
		}
		patternNode.setTypeInfo(typeInfo);
		patternNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		typeNode.checkTypes(this);
		patternNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		typeNode.foldConstants(this);
		patternNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		typeNode.generateIntermediate(this);
		
		if (dataId != null && !dataId.typeInfo.equals(typeInfo)) {
			DataId castDataId = routine.nextRegId(typeInfo);
			addBindingAssignmentAction(castDataId, dataId);
			dataId = castDataId;
		}
		
		patternNode.dataId = dataId;
		patternNode.generateIntermediate(this);
	}
}
