/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TRemainderEquals extends Token
{
    public TRemainderEquals()
    {
        super.setText("%=");
    }

    public TRemainderEquals(int line, int pos)
    {
        super.setText("%=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TRemainderEquals(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRemainderEquals(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TRemainderEquals text.");
    }
}
