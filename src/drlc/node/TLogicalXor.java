/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TLogicalXor extends Token
{
    public TLogicalXor()
    {
        super.setText("^?");
    }

    public TLogicalXor(int line, int pos)
    {
        super.setText("^?");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLogicalXor(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLogicalXor(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLogicalXor text.");
    }
}
