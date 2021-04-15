/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TName extends Token
{
    public TName(String text)
    {
        setText(text);
    }

    public TName(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TName(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTName(this);
    }
}
