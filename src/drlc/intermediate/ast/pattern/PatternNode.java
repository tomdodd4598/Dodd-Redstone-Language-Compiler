package drlc.intermediate.ast.pattern;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public abstract class PatternNode extends ASTNode<Scope> {
	
	protected @Nullable Boolean _static = null;
	
	protected @Nullable TypeInfo typeInfo = null;
	
	public @Nullable DataId dataId = null;
	
	protected PatternNode(Source source) {
		super(source);
	}
	
	public boolean canDeclareExcludingInitializer() {
		return false;
	}
	
	public @Nullable TypeInfo getExplicitTypeInfo() {
		return null;
	}
	
	public boolean hasStaticBinding() {
		if (_static == null) {
			for (DeclaratorNode declaratorNode : getDeclaratorNodes()) {
				boolean staticModifier = declaratorNode.variableModifier._static;
				if (_static != null && _static != staticModifier) {
					throw error("Can not mix static and non-static variables in one pattern!");
				}
				_static = staticModifier;
			}
			
			if (_static == null) {
				_static = false;
			}
		}
		return _static;
	}
	
	public @NonNull List<DeclaratorNode> getDeclaratorNodes() {
		List<DeclaratorNode> declaratorNodes = new ArrayList<>();
		collectDeclaratorNodes(declaratorNodes);
		return declaratorNodes;
	}
	
	protected abstract void collectDeclaratorNodes(List<DeclaratorNode> declaratorNodes);
	
	public void checkDeclaratorNames() {
		Set<String> names = new HashSet<>();
		for (DeclaratorNode declaratorNode : getDeclaratorNodes()) {
			if (!names.add(declaratorNode.name)) {
				throw Helpers.nodeError(declaratorNode, "Repeated variable name \"%s\" in pattern!", declaratorNode.name);
			}
		}
	}
	
	public void setTypeInfo(@NonNull TypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	@SuppressWarnings("null")
	protected @NonNull TypeInfo getTypeInfo() {
		if (typeInfo == null) {
			throw error("Attempted to get null type for pattern!");
		}
		return typeInfo;
	}
	
	protected @NonNull DataId getInputAddressDataId(DataId inputDataId) {
		@NonNull TypeInfo inputTypeInfo = inputDataId.typeInfo;
		if (inputTypeInfo.isAddress()) {
			throw error("Can not destructure address value of type \"%s\"!", inputTypeInfo);
		}
		
		return inputDataId.removeDereference(this);
	}
	
	protected void addBindingAssignmentAction(DataId target, DataId arg) {
		@NonNull TypeInfo argTypeInfo = arg.typeInfo, targetTypeInfo = target.typeInfo;
		if (argTypeInfo.equals(targetTypeInfo)) {
			routine.addAssignmentAction(this, target, arg);
		}
		else if (argTypeInfo.canImplicitCastTo(targetTypeInfo)) {
			routine.addTypeCastAction(this, scope, targetTypeInfo, argTypeInfo, target, arg);
		}
		else {
			throw castError("pattern value", argTypeInfo, targetTypeInfo);
		}
	}
}
