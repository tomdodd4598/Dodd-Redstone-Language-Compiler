/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TLogicalAnd extends Token
{
    public TLogicalAnd()
    {
        super.setText("&&");
    }

    public TLogicalAnd(int line, int pos)
    {
        super.setText("&&");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLogicalAnd(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLogicalAnd(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLogicalAnd text.");
    }
}
