/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TRightRotate extends Token
{
    public TRightRotate()
    {
        super.setText(">>>");
    }

    public TRightRotate(int line, int pos)
    {
        super.setText(">>>");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TRightRotate(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRightRotate(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TRightRotate text.");
    }
}
