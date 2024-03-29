/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TSelf extends Token
{
    public TSelf()
    {
        super.setText("self");
    }

    public TSelf(int line, int pos)
    {
        super.setText("self");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TSelf(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSelf(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TSelf text.");
    }
}
