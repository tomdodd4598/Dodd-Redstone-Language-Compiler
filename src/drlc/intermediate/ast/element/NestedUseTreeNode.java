package drlc.intermediate.ast.element;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;

public class NestedUseTreeNode extends UseTreeNode {
	
	public final @NonNull List<String> pathPrefix;
	public final @NonNull List<UseTreeNode> useTreeNodes;
	
	public NestedUseTreeNode(Source source, @NonNull List<String> pathPrefix, @NonNull List<UseTreeNode> useTreeNodes) {
		super(source);
		this.pathPrefix = pathPrefix;
		this.useTreeNodes = useTreeNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.setScopes(this);
		}
	}
	
	@Override
	public void declareImports(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.declareImports(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkImports(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.checkImports(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.foldConstants(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.generateIntermediate(this);
		}
	}
	
	@Override
	public void buildPath(@NonNull List<String> pathPrefix) {
		List<String> localPathPrefix = new ArrayList<>(pathPrefix);
		localPathPrefix.addAll(this.pathPrefix);
		pathSegments.clear();
		pathSegments.addAll(localPathPrefix);
		
		for (UseTreeNode useTreeNode : useTreeNodes) {
			useTreeNode.buildPath(new ArrayList<>(localPathPrefix));
		}
	}
}
