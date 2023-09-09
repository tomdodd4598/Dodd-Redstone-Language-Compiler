package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class ReturnNode extends StopNode {
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo expectedTypeInfo = null;
	
	public ReturnNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		if (routine.isRootRoutine()) {
			throw error("Root routine can not return - use an exit statement!");
		}
		
		scope.definiteLocalReturn = true;
		
		expectedTypeInfo = routine.getReturnTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		if (!Main.generator.voidTypeInfo.canImplicitCastTo(expectedTypeInfo)) {
			throw castError("return value", Main.generator.voidTypeInfo, expectedTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		routine.addReturnAction(this);
	}
}
