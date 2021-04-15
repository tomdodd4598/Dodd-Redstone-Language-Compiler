/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TXor extends Token
{
    public TXor()
    {
        super.setText("^");
    }

    public TXor(int line, int pos)
    {
        super.setText("^");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TXor(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTXor(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TXor text.");
    }
}
