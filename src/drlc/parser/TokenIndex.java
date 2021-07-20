/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.parser;

import drlc.node.*;
import drlc.analysis.*;

class TokenIndex extends AnalysisAdapter
{
    int index;

    @Override
    public void caseTSetupPrefix(@SuppressWarnings("unused") TSetupPrefix node)
    {
        this.index = 0;
    }

    @Override
    public void caseTSetArgc(@SuppressWarnings("unused") TSetArgc node)
    {
        this.index = 1;
    }

    @Override
    public void caseTConst(@SuppressWarnings("unused") TConst node)
    {
        this.index = 2;
    }

    @Override
    public void caseTVar(@SuppressWarnings("unused") TVar node)
    {
        this.index = 3;
    }

    @Override
    public void caseTVoid(@SuppressWarnings("unused") TVoid node)
    {
        this.index = 4;
    }

    @Override
    public void caseTFun(@SuppressWarnings("unused") TFun node)
    {
        this.index = 5;
    }

    @Override
    public void caseTOut(@SuppressWarnings("unused") TOut node)
    {
        this.index = 6;
    }

    @Override
    public void caseTArgc(@SuppressWarnings("unused") TArgc node)
    {
        this.index = 7;
    }

    @Override
    public void caseTArgv(@SuppressWarnings("unused") TArgv node)
    {
        this.index = 8;
    }

    @Override
    public void caseTReturn(@SuppressWarnings("unused") TReturn node)
    {
        this.index = 9;
    }

    @Override
    public void caseTIf(@SuppressWarnings("unused") TIf node)
    {
        this.index = 10;
    }

    @Override
    public void caseTElif(@SuppressWarnings("unused") TElif node)
    {
        this.index = 11;
    }

    @Override
    public void caseTElse(@SuppressWarnings("unused") TElse node)
    {
        this.index = 12;
    }

    @Override
    public void caseTWhile(@SuppressWarnings("unused") TWhile node)
    {
        this.index = 13;
    }

    @Override
    public void caseTContinue(@SuppressWarnings("unused") TContinue node)
    {
        this.index = 14;
    }

    @Override
    public void caseTBreak(@SuppressWarnings("unused") TBreak node)
    {
        this.index = 15;
    }

    @Override
    public void caseTEquals(@SuppressWarnings("unused") TEquals node)
    {
        this.index = 16;
    }

    @Override
<<<<<<< HEAD
    public void caseTLPar(@SuppressWarnings("unused") TLPar node)
    {
        this.index = 17;
    }

    @Override
    public void caseTRPar(@SuppressWarnings("unused") TRPar node)
    {
        this.index = 18;
    }

    @Override
    public void caseTLBracket(@SuppressWarnings("unused") TLBracket node)
=======
    public void caseTLBrace(@SuppressWarnings("unused") TLBrace node)
>>>>>>> parent of 543a4da (Added array syntax to language spec and added "all" utility compiler mode)
    {
        this.index = 17;
    }

    @Override
    public void caseTRBracket(@SuppressWarnings("unused") TRBracket node)
    {
        this.index = 18;
    }

    @Override
    public void caseTLBrace(@SuppressWarnings("unused") TLBrace node)
    {
        this.index = 19;
    }

    @Override
    public void caseTRBrace(@SuppressWarnings("unused") TRBrace node)
    {
        this.index = 20;
    }

    @Override
    public void caseTComma(@SuppressWarnings("unused") TComma node)
    {
        this.index = 21;
    }

    @Override
    public void caseTSemicolon(@SuppressWarnings("unused") TSemicolon node)
    {
        this.index = 22;
    }

    @Override
    public void caseTAddressOf(@SuppressWarnings("unused") TAddressOf node)
    {
        this.index = 23;
    }

    @Override
    public void caseTDereference(@SuppressWarnings("unused") TDereference node)
    {
        this.index = 24;
    }

    @Override
    public void caseTPlus(@SuppressWarnings("unused") TPlus node)
    {
        this.index = 25;
    }

    @Override
    public void caseTMinus(@SuppressWarnings("unused") TMinus node)
    {
        this.index = 26;
    }

    @Override
    public void caseTComplement(@SuppressWarnings("unused") TComplement node)
    {
        this.index = 27;
    }

    @Override
    public void caseTToBool(@SuppressWarnings("unused") TToBool node)
    {
        this.index = 28;
    }

    @Override
    public void caseTNot(@SuppressWarnings("unused") TNot node)
    {
        this.index = 29;
    }

    @Override
    public void caseTAnd(@SuppressWarnings("unused") TAnd node)
    {
        this.index = 30;
    }

    @Override
    public void caseTOr(@SuppressWarnings("unused") TOr node)
    {
        this.index = 31;
    }

    @Override
    public void caseTXor(@SuppressWarnings("unused") TXor node)
    {
        this.index = 32;
    }

    @Override
    public void caseTLeftShift(@SuppressWarnings("unused") TLeftShift node)
    {
        this.index = 33;
    }

    @Override
    public void caseTRightShift(@SuppressWarnings("unused") TRightShift node)
    {
        this.index = 34;
    }

    @Override
    public void caseTMultiply(@SuppressWarnings("unused") TMultiply node)
    {
        this.index = 35;
    }

    @Override
    public void caseTDivide(@SuppressWarnings("unused") TDivide node)
    {
        this.index = 36;
    }

    @Override
    public void caseTModulo(@SuppressWarnings("unused") TModulo node)
    {
        this.index = 37;
    }

    @Override
    public void caseTEqualTo(@SuppressWarnings("unused") TEqualTo node)
    {
        this.index = 38;
    }

    @Override
    public void caseTNotEqualTo(@SuppressWarnings("unused") TNotEqualTo node)
    {
        this.index = 39;
    }

    @Override
    public void caseTLessThan(@SuppressWarnings("unused") TLessThan node)
    {
        this.index = 40;
    }

    @Override
    public void caseTLessOrEqual(@SuppressWarnings("unused") TLessOrEqual node)
    {
        this.index = 41;
    }

    @Override
    public void caseTMoreThan(@SuppressWarnings("unused") TMoreThan node)
    {
        this.index = 42;
    }

    @Override
    public void caseTMoreOrEqual(@SuppressWarnings("unused") TMoreOrEqual node)
    {
        this.index = 43;
    }

    @Override
    public void caseTName(@SuppressWarnings("unused") TName node)
    {
        this.index = 44;
    }

    @Override
    public void caseTInteger(@SuppressWarnings("unused") TInteger node)
    {
        this.index = 47;
    }

    @Override
    public void caseEOF(@SuppressWarnings("unused") EOF node)
    {
        this.index = 48;
    }
}
