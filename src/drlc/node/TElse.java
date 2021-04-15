/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TElse extends Token
{
    public TElse()
    {
        super.setText("else");
    }

    public TElse(int line, int pos)
    {
        super.setText("else");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TElse(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTElse(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TElse text.");
    }
}
