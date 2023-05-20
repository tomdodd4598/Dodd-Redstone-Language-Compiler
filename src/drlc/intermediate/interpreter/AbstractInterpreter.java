package drlc.intermediate.interpreter;

import java.util.*;
import java.util.stream.Collectors;

import drlc.*;
import drlc.analysis.DepthFirstAdapter;
import drlc.intermediate.Scope;
import drlc.intermediate.component.Variable;
import drlc.intermediate.component.constant.Constant;
import drlc.intermediate.component.info.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.routine.Routine;
import drlc.node.*;

public abstract class AbstractInterpreter extends DepthFirstAdapter {
	
	protected final Generator generator;
	protected Scope scope;
	
	public AbstractInterpreter(Generator generator) {
		this.generator = generator;
	}
	
	@Override
	public void defaultIn(Node node) {
		System.out.println(node.getClass().getSimpleName().concat(" node in logic not supported!"));
	}
	
	@Override
	public void defaultOut(Node node) {
		System.out.println(node.getClass().getSimpleName().concat(" node out logic not supported!"));
	}
	
	protected void checkLastExpressionIsBool(Node node, String error, TypeInfo inputInfo) {
		TypeInfo boolTypeInfo = generator.boolTypeInfo;
		if (!inputInfo.equals(boolTypeInfo)) {
			throw new IllegalArgumentException(String.format(error, inputInfo, boolTypeInfo, node));
		}
	}
	
	protected VariableModifierInfo getVariableModifierInfo(List<TVariableModifier> tModifierList) {
		Set<String> modifiers = tModifierList.stream().map(Token::getText).collect(Collectors.toSet());
		return new VariableModifierInfo(modifiers.contains(Global.STATIC));
	}
	
	protected TypeInfo createReturnTypeInfo(Node node, Scope scope, PReturnType pReturnType) {
		return createTypeInfo(node, scope, pReturnType == null ? null : ((AReturnType) pReturnType).getType());
	}
	
	protected TypeInfo createTypeInfo(Node node, Scope scope, PType pType) {
		if (pType == null) {
			return generator.voidTypeInfo;
		}
		
		PRawType pRawType = ((AType) pType).getRawType();
		int referenceLevel = ((AType) pType).getAnd().size();
		
		if (pRawType instanceof ABasicRawType) {
			ABasicRawType aBasicType = (ABasicRawType) pRawType;
			String typeName = aBasicType.getName().getText();
			TypeInfo builtInTypeInfo = generator.builtInTypeInfo(typeName, referenceLevel);
			if (builtInTypeInfo != null) {
				return builtInTypeInfo;
			}
			else {
				Type type = scope.getType(node, typeName);
				throw new IllegalArgumentException(String.format("Attempted to use an unimplemented type \"%s\"! %s", type, node));
			}
		}
		else if (pRawType instanceof AArrayRawType) {
			// TODO
			return null;
		}
		else {
			AFunctionRawType aFunctionType = (AFunctionRawType) pRawType;
			aFunctionType.getParParameterList().apply(this);
			TypeInfo[] paramTypeInfos = Helpers.paramTypeInfoArray(generator.program.getParamArray(node, getParameterListLength(aFunctionType.getParParameterList()), true));
			return new FunctionTypeInfo(node, scope, referenceLevel, createReturnTypeInfo(node, scope, aFunctionType.getReturnType()), paramTypeInfos);
		}
	}
	
	protected int getParameterListLength(PParParameterList parList) {
		AParameterList list = (AParameterList) ((AParParameterList) parList).getParameterList();
		return list == null ? 0 : 1 + list.getParameterListTail().size();
	}
	
	protected DeclaratorInfo createDeclaratorInfo(Node node, PDeclarator pDeclarator, VariableModifierInfo modifierInfo, Integer expectSize) {
		ADeclarator aDeclarator = (ADeclarator) pDeclarator;
		IdentifierInfo identifierInfo = createIdentifierInfo(node, aDeclarator.getIdentifier(), false);
		Variable variable = new Variable(identifierInfo.name, modifierInfo, createTypeInfo(node, scope, getTypeFromAnnotation(aDeclarator.getTypeAnnotation())));
		return createDeclaratorInfoInternal(node, variable, true, expectSize);
	}
	
	protected DeclaratorInfo createDeclaratorInfo(Node node, PParameter pParameter, VariableModifierInfo modifierInfo, Integer expectSize) {
		IdentifierInfo identifierInfo;
		PType pType;
		if (pParameter instanceof AIncludingIdentifierParameter) {
			AIncludingIdentifierParameter aParameter = (AIncludingIdentifierParameter) pParameter;
			identifierInfo = createIdentifierInfo(node, aParameter.getIdentifier(), true);
			pType = getTypeFromAnnotation(aParameter.getTypeAnnotation());
		}
		else {
			AExcludingIdentifierParameter aParameter = (AExcludingIdentifierParameter) pParameter;
			identifierInfo = new IdentifierInfo(getDiscardPrefixName(node, true));
			pType = aParameter.getType();
		}
		Variable variable = new Variable(identifierInfo.name, modifierInfo, createTypeInfo(node, scope, pType));
		return createDeclaratorInfoInternal(node, variable, true, expectSize);
	}
	
	protected DeclaratorInfo createDeclaratorInfoInternal(Node node, Variable variable, boolean declaration, Integer expectSize) {
		// TODO: Handle array/struct declaration
		TypeInfo typeInfo = variable.typeInfo;
		if (typeInfo.isVoid(node)) {
			throw new IllegalArgumentException(String.format("Declarator \"%s\" can not be void! %s", variable.name, node));
		}
		
		if (expectSize != null) {
			int size = typeInfo.getSize(node, generator);
			if (expectSize != size) {
				throw new IllegalArgumentException(String.format("Declarator \"%s\" requires having a size %s, but has a size %s! %s", variable.name, expectSize, size, node));
			}
		}
		return new DeclaratorInfo(node, variable);
	}
	
	protected IdentifierInfo createIdentifierInfo(Node node, PIdentifier pIdentifier, boolean param) {
		if (pIdentifier instanceof ANameIdentifier) {
			ANameIdentifier aNameIdentifier = (ANameIdentifier) pIdentifier;
			return new IdentifierInfo(aNameIdentifier.getName().getText());
		}
		else {
			return new IdentifierInfo(getDiscardPrefixName(node, param));
		}
	}
	
	protected String getDiscardPrefixName(Node node, boolean param) {
		return param ? Global.DISCARD_PARAM_PREFIX.concat(Integer.toString(generator.program.currentParamListSize(node))) : Global.DISCARD_PARAM_PREFIX;
	}
	
	protected PType getTypeFromAnnotation(PTypeAnnotation pTypeAnnotation) {
		return ((ATypeAnnotation) pTypeAnnotation).getType();
	}
	
	protected FunctionTypeInfo tryGetDirectFunctionTypeInfo(PExpression7 expression) {
		String name = Helpers.removeParentheses(expression.toString());
		if (scope.functionExists(name)) {
			Variable variable = scope.getVariable(expression, name);
			TypeInfo typeInfo = variable.typeInfo;
			if (typeInfo.isFunction()) {
				Routine routine = generator.program.currentRoutine();
				routine.currentExpressionInfo(expression).setTypeInfo(typeInfo);
				routine.incrementRegId();
				routine.addDirectFunctionRegisterAssignmentAction(expression, variable);
				routine.currentExpressionInfo(expression).isDirectFunction = true;
				return (FunctionTypeInfo) typeInfo;
			}
		}
		return null;
	}
	
	protected List<Constant> getArgumentEvaluationInfoList(PExpressionList pExpressionList) {
		List<Constant> infoList = new ArrayList<>();
		if (pExpressionList != null) {
			AExpressionList expressionList = (AExpressionList) pExpressionList;
			PExpressionRvalue pExpressionRvalue = expressionList.getExpressionRvalue();
			infoList.add(Evaluator.evaluate(pExpressionRvalue, generator, scope, pExpressionRvalue.toString()));
			for (PExpressionListTail tail : expressionList.getExpressionListTail()) {
				pExpressionRvalue = ((AExpressionListTail) tail).getExpressionRvalue();
				infoList.add(Evaluator.evaluate(pExpressionRvalue, generator, scope, pExpressionRvalue.toString()));
			}
		}
		return infoList;
	}
	
	// Tokens
	
	@Override
	public void caseTLBrace(TLBrace node) {
		scope = new Scope(node, generator, scope);
	}
	
	@Override
	public void caseTRBrace(TRBrace node) {
		scope = scope.previous;
	}
}
