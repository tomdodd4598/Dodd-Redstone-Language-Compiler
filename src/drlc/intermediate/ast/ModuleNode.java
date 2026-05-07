package drlc.intermediate.ast;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.section.StaticSectionNode;
import drlc.intermediate.scope.Scope;

public class ModuleNode extends ASTNode<Scope> {
	
	public final @NonNull List<StaticSectionNode<?>> sectionNodes;
	
	public ModuleNode(Source source, @NonNull List<StaticSectionNode<?>> sectionNodes) {
		super(source);
		this.sectionNodes = sectionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.setScopes(this);
		}
	}
	
	@Override
	public void declareImports(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.declareImports(this);
		}
	}
	
	@Override
	public void declareTypes(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.declareTypes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareFunctions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.declareFunctions(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkImports(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.checkImports(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.foldConstants(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.generateIntermediate(this);
		}
	}
}
