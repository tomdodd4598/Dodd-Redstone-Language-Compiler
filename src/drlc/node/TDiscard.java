/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TDiscard extends Token
{
    public TDiscard()
    {
        super.setText("_");
    }

    public TDiscard(int line, int pos)
    {
        super.setText("_");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TDiscard(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTDiscard(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TDiscard text.");
    }
}