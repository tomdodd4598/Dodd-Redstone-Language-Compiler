/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.analysis;

import drlc.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseAUnit(AUnit node);
    void caseASetupSection(ASetupSection node);
    void caseAInputSpecification(AInputSpecification node);
    void caseAMethodDefinitionGeneralSection(AMethodDefinitionGeneralSection node);
    void caseAFunctionDefinitionGeneralSection(AFunctionDefinitionGeneralSection node);
    void caseABasicGeneralSection(ABasicGeneralSection node);
    void caseAConstantDefinitionBasicSection(AConstantDefinitionBasicSection node);
    void caseAVariableDeclarationBasicSection(AVariableDeclarationBasicSection node);
    void caseAVariableModificationBasicSection(AVariableModificationBasicSection node);
    void caseAMethodCallBasicSection(AMethodCallBasicSection node);
    void caseAConditionalBasicSection(AConditionalBasicSection node);
    void caseAIterativeBasicSection(AIterativeBasicSection node);
    void caseAMethodDefinition(AMethodDefinition node);
    void caseAFunctionDefinition(AFunctionDefinition node);
    void caseAConstantDefinition(AConstantDefinition node);
    void caseANoInitialisationVariableDeclaration(ANoInitialisationVariableDeclaration node);
    void caseAWithInitialisationVariableDeclaration(AWithInitialisationVariableDeclaration node);
    void caseAVariableModification(AVariableModification node);
    void caseABuiltInOutMethodCall(ABuiltInOutMethodCall node);
    void caseADefinedMethodCall(ADefinedMethodCall node);
    void caseAIfBlock(AIfBlock node);
    void caseAElseIfBlock(AElseIfBlock node);
    void caseAElseBlock(AElseBlock node);
    void caseAIterativeBlock(AIterativeBlock node);
    void caseAReturnStopStatement(AReturnStopStatement node);
    void caseAContinueStopStatement(AContinueStopStatement node);
    void caseABreakStopStatement(ABreakStopStatement node);
    void caseAReturnExpressionStopStatement(AReturnExpressionStopStatement node);
    void caseADead1DeadCode(ADead1DeadCode node);
    void caseADead2DeadCode(ADead2DeadCode node);
    void caseADead3DeadCode(ADead3DeadCode node);
    void caseADead4DeadCode(ADead4DeadCode node);
    void caseADead5DeadCode(ADead5DeadCode node);
    void caseAPrioritizedExpression(APrioritizedExpression node);
    void caseABinaryExpression(ABinaryExpression node);
    void caseATermPrioritizedExpression(ATermPrioritizedExpression node);
    void caseABinaryPrioritizedExpression(ABinaryPrioritizedExpression node);
    void caseAValueTerm(AValueTerm node);
    void caseAAddressOfTerm(AAddressOfTerm node);
    void caseADereferenceTerm(ADereferenceTerm node);
    void caseAUnaryTerm(AUnaryTerm node);
    void caseAParExpressionTerm(AParExpressionTerm node);
    void caseAIntegerValue(AIntegerValue node);
    void caseAVariableValue(AVariableValue node);
    void caseAFunctionValue(AFunctionValue node);
    void caseABuiltInArgcFunction(ABuiltInArgcFunction node);
    void caseABuiltInArgvFunction(ABuiltInArgvFunction node);
    void caseADefinedFunction(ADefinedFunction node);
    void caseARvalueVariable(ARvalueVariable node);
    void caseALvalueVariable(ALvalueVariable node);
    void caseAArgumentList(AArgumentList node);
    void caseAArgumentListTail(AArgumentListTail node);
    void caseAParameterList(AParameterList node);
    void caseAParameterListTail(AParameterListTail node);
    void caseAPlusUnaryOp(APlusUnaryOp node);
    void caseAMinusUnaryOp(AMinusUnaryOp node);
    void caseAComplementUnaryOp(AComplementUnaryOp node);
    void caseAToBoolUnaryOp(AToBoolUnaryOp node);
    void caseANotUnaryOp(ANotUnaryOp node);
    void caseAPlusBinaryOp(APlusBinaryOp node);
    void caseAAndBinaryOp(AAndBinaryOp node);
    void caseAOrBinaryOp(AOrBinaryOp node);
    void caseAXorBinaryOp(AXorBinaryOp node);
    void caseAMinusBinaryOp(AMinusBinaryOp node);
    void caseALeftShiftPrioritizedBinaryOp(ALeftShiftPrioritizedBinaryOp node);
    void caseARightShiftPrioritizedBinaryOp(ARightShiftPrioritizedBinaryOp node);
    void caseAMultiplyPrioritizedBinaryOp(AMultiplyPrioritizedBinaryOp node);
    void caseAEqualToPrioritizedBinaryOp(AEqualToPrioritizedBinaryOp node);
    void caseADividePrioritizedBinaryOp(ADividePrioritizedBinaryOp node);
    void caseAModuloPrioritizedBinaryOp(AModuloPrioritizedBinaryOp node);
    void caseANotEqualToPrioritizedBinaryOp(ANotEqualToPrioritizedBinaryOp node);
    void caseALessThanPrioritizedBinaryOp(ALessThanPrioritizedBinaryOp node);
    void caseALessOrEqualPrioritizedBinaryOp(ALessOrEqualPrioritizedBinaryOp node);
    void caseAMoreThanPrioritizedBinaryOp(AMoreThanPrioritizedBinaryOp node);
    void caseAMoreOrEqualPrioritizedBinaryOp(AMoreOrEqualPrioritizedBinaryOp node);

    void caseTSetArgc(TSetArgc node);
    void caseTConst(TConst node);
    void caseTVar(TVar node);
    void caseTVoid(TVoid node);
    void caseTFun(TFun node);
    void caseTOut(TOut node);
    void caseTArgc(TArgc node);
    void caseTArgv(TArgv node);
    void caseTReturn(TReturn node);
    void caseTIf(TIf node);
    void caseTElse(TElse node);
    void caseTWhile(TWhile node);
    void caseTContinue(TContinue node);
    void caseTBreak(TBreak node);
    void caseTEquals(TEquals node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTLBrace(TLBrace node);
    void caseTRBrace(TRBrace node);
    void caseTComma(TComma node);
    void caseTSemicolon(TSemicolon node);
    void caseTAddressOf(TAddressOf node);
    void caseTDereference(TDereference node);
    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTComplement(TComplement node);
    void caseTToBool(TToBool node);
    void caseTNot(TNot node);
    void caseTAnd(TAnd node);
    void caseTOr(TOr node);
    void caseTXor(TXor node);
    void caseTLeftShift(TLeftShift node);
    void caseTRightShift(TRightShift node);
    void caseTMultiply(TMultiply node);
    void caseTDivide(TDivide node);
    void caseTModulo(TModulo node);
    void caseTEqualTo(TEqualTo node);
    void caseTNotEqualTo(TNotEqualTo node);
    void caseTLessThan(TLessThan node);
    void caseTLessOrEqual(TLessOrEqual node);
    void caseTMoreThan(TMoreThan node);
    void caseTMoreOrEqual(TMoreOrEqual node);
    void caseTName(TName node);
    void caseTInteger(TInteger node);
    void caseTBlank(TBlank node);
    void caseTComment(TComment node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}
