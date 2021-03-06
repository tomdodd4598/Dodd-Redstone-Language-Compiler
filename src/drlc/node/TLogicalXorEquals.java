/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TLogicalXorEquals extends Token
{
    public TLogicalXorEquals()
    {
        super.setText("^?=");
    }

    public TLogicalXorEquals(int line, int pos)
    {
        super.setText("^?=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLogicalXorEquals(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLogicalXorEquals(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLogicalXorEquals text.");
    }
}
