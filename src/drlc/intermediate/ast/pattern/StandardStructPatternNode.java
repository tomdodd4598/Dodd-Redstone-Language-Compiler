package drlc.intermediate.ast.pattern;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Helpers.Pair;
import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;

public class StandardStructPatternNode extends PatternNode {
	
	public final @NonNull Path path;
	public final @NonNull List<String> labels;
	public final @NonNull List<PatternNode> patternNodes;
	public final int count;
	
	@SuppressWarnings("null")
	public @NonNull StructTypeInfo typeInfo = null;
	@SuppressWarnings("null")
	public @NonNull List<MemberInfo> memberInfos = null;
	
	public StandardStructPatternNode(Source source, @NonNull Path path, @NonNull Pair<@NonNull List<String>, @NonNull List<PatternNode>> patternNodesPair) {
		super(source);
		this.path = path;
		labels = patternNodesPair.left;
		patternNodes = patternNodesPair.right;
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
			throw castError("struct pattern value", inputTypeInfo, typeInfo);
		}
		
		Set<String> uniqueLabels = new HashSet<>();
		memberInfos = new ArrayList<>();
		for (int i = 0; i < count; ++i) {
			@NonNull String label = labels.get(i);
			MemberInfo info = typeInfo.getMemberInfo(label);
			if (info == null) {
				throw error("Pattern of type \"%s\" has no member \"%s\"!", typeInfo, label);
			}
			if (!uniqueLabels.add(label)) {
				throw error("Repeated member \"%s\" in \"%s\" pattern!", label, typeInfo);
			}
			memberInfos.add(info);
		}
		
		for (String label : typeInfo.typeDef.memberMap.keySet()) {
			if (!uniqueLabels.contains(label)) {
				throw error("Missing member \"%s\" in \"%s\" pattern!", label, typeInfo);
			}
		}
		
		for (int i = 0; i < count; ++i) {
			PatternNode patternNode = patternNodes.get(i);
			patternNode.setTypeInfo(memberInfos.get(i).typeInfo);
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
			throw error("Struct pattern requires an initializer!");
		}
		DataId addressDataId = getInputAddressDataId(dataId);
		
		for (int i = 0; i < count; ++i) {
			PatternNode patternNode = patternNodes.get(i);
			patternNode.dataId = routine.addMemberValueAction(this, addressDataId, memberInfos.get(i));
			patternNode.generateIntermediate(this);
		}
	}
}
