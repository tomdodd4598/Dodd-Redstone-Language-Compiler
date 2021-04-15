/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TToBool extends Token
{
    public TToBool()
    {
        super.setText("?");
    }

    public TToBool(int line, int pos)
    {
        super.setText("?");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TToBool(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTToBool(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TToBool text.");
    }
}
