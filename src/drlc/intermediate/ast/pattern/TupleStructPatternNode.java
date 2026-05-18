package drlc.intermediate.ast.pattern;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;

public class TupleStructPatternNode extends PatternNode {
	
	public final @NonNull Path path;
	public final @NonNull List<PatternNode> patternNodes;
	public final int count;
	
	@SuppressWarnings("null")
	public @NonNull StructTypeInfo typeInfo = null;
	@SuppressWarnings("null")
	public @NonNull List<MemberInfo> memberInfos = null;
	
	public TupleStructPatternNode(Source source, @NonNull Path path, @NonNull List<PatternNode> patternNodes) {
		super(source);
		this.path = path;
		this.patternNodes = patternNodes;
		count = patternNodes.size();
	}
	
	@Override
	public @Nullable TypeInfo getExplicitTypeInfo() {
		return getStructTypeInfo();
	}
	
	protected @NonNull StructTypeInfo getStructTypeInfo() {
		@NonNull TypeInfo pathTypeInfo = scope.pathGet(this, path, (x, name) -> x.getTypeInfo(this, name, false));
		if (!(pathTypeInfo instanceof StructTypeInfo structTypeInfo) || pathTypeInfo.isAddress()) {
			throw error("Type \"%s\" is not a struct type!", pathTypeInfo);
		}
		return structTypeInfo;
	}
	
	@Override
	protected void collectDeclaratorNodes(List<DeclaratorNode> declaratorNodes) {
		for (PatternNode patternNode : patternNodes) {
			patternNode.collectDeclaratorNodes(declaratorNodes);
		}
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		for (PatternNode patternNode : patternNodes) {
			patternNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (PatternNode patternNode : patternNodes) {
			patternNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (PatternNode patternNode : patternNodes) {
			patternNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		@NonNull TypeInfo inputTypeInfo = getTypeInfo();
		typeInfo = getStructTypeInfo();
		if (!inputTypeInfo.canImplicitCastTo(typeInfo)) {
			throw castError("tuple struct pattern value", inputTypeInfo, typeInfo);
		}
		if (typeInfo.count != count) {
			throw error("Tuple struct pattern requires %d members but received %d!", typeInfo.count, count);
		}
		
		memberInfos = new ArrayList<>();
		for (int i = 0; i < count; ++i) {
			String label = Integer.toString(i);
			MemberInfo info = typeInfo.getMemberInfo(label);
			if (info == null) {
				throw error("Pattern of type \"%s\" has no member \"%s\"!", typeInfo, label);
			}
			memberInfos.add(info);
			
			PatternNode patternNode = patternNodes.get(i);
			patternNode.setTypeInfo(info.typeInfo);
			patternNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (PatternNode patternNode : patternNodes) {
			patternNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (PatternNode patternNode : patternNodes) {
			patternNode.foldConstants(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		if (dataId == null) {
			throw error("Tuple struct pattern requires an initializer!");
		}
		DataId addressDataId = getInputAddressDataId(dataId);
		
		for (int i = 0; i < count; ++i) {
			PatternNode patternNode = patternNodes.get(i);
			patternNode.dataId = routine.addMemberValueAction(this, addressDataId, memberInfos.get(i));
			patternNode.generateIntermediate(this);
		}
	}
}
