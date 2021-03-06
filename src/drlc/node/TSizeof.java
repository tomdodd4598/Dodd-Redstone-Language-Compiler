/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TSizeof extends Token
{
    public TSizeof()
    {
        super.setText("sizeof");
    }

    public TSizeof(int line, int pos)
    {
        super.setText("sizeof");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TSizeof(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSizeof(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TSizeof text.");
    }
}
