package drlc.intermediate.ast.pattern;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;

public class TuplePatternNode extends PatternNode {
	
	public final @NonNull List<PatternNode> patternNodes;
	public final int count;
	
	@SuppressWarnings("null")
	public @NonNull TupleTypeInfo typeInfo = null;
	@SuppressWarnings("null")
	public @NonNull List<MemberInfo> memberInfos = null;
	
	public TuplePatternNode(Source source, @NonNull List<PatternNode> patternNodes) {
		super(source);
		this.patternNodes = patternNodes;
		count = patternNodes.size();
	}
	
	@Override
	public @Nullable TypeInfo getExplicitTypeInfo() {
		List<TypeInfo> typeInfos = new ArrayList<>();
		for (PatternNode patternNode : patternNodes) {
			TypeInfo typeInfo = patternNode.getExplicitTypeInfo();
			if (typeInfo == null) {
				return null;
			}
			typeInfos.add(typeInfo);
		}
		return new TupleTypeInfo(this, new ArrayList<>(), typeInfos);
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
		if (!(inputTypeInfo instanceof TupleTypeInfo tupleTypeInfo) || inputTypeInfo.isAddress()) {
			throw error("Attempted to destructure non-tuple type \"%s\" as tuple pattern!", inputTypeInfo);
		}
		
		typeInfo = tupleTypeInfo;
		if (typeInfo.count != count) {
			throw error("Tuple pattern requires %d members but received %d!", count, typeInfo.count);
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
			throw error("Tuple pattern requires an initializer!");
		}
		DataId addressDataId = getInputAddressDataId(dataId);
		
		for (int i = 0; i < count; ++i) {
			PatternNode patternNode = patternNodes.get(i);
			patternNode.dataId = routine.addMemberValueAction(this, addressDataId, memberInfos.get(i));
			patternNode.generateIntermediate(this);
		}
	}
}
