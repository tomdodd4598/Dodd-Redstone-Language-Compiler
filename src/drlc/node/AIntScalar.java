/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AIntScalar extends PScalar
{
    private TIntValue _intValue_;

    public AIntScalar()
    {
        // Constructor
    }

    public AIntScalar(
        @SuppressWarnings("hiding") TIntValue _intValue_)
    {
        // Constructor
        setIntValue(_intValue_);

    }

    @Override
    public Object clone()
    {
        return new AIntScalar(
            cloneNode(this._intValue_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIntScalar(this);
    }

    public TIntValue getIntValue()
    {
        return this._intValue_;
    }

    public void setIntValue(TIntValue node)
    {
        if(this._intValue_ != null)
        {
            this._intValue_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._intValue_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._intValue_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._intValue_ == child)
        {
            this._intValue_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._intValue_ == oldChild)
        {
            setIntValue((TIntValue) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
