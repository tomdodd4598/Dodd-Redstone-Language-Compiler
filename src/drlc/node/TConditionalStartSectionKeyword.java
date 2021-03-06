/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class TConditionalStartSectionKeyword extends Token
{
    public TConditionalStartSectionKeyword(String text)
    {
        setText(text);
    }

    public TConditionalStartSectionKeyword(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TConditionalStartSectionKeyword(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTConditionalStartSectionKeyword(this);
    }
}
