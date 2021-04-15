/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AMoreOrEqualPrioritizedBinaryOp extends PPrioritizedBinaryOp
{
    private TMoreOrEqual _moreOrEqual_;

    public AMoreOrEqualPrioritizedBinaryOp()
    {
        // Constructor
    }

    public AMoreOrEqualPrioritizedBinaryOp(
        @SuppressWarnings("hiding") TMoreOrEqual _moreOrEqual_)
    {
        // Constructor
        setMoreOrEqual(_moreOrEqual_);

    }

    @Override
    public Object clone()
    {
        return new AMoreOrEqualPrioritizedBinaryOp(
            cloneNode(this._moreOrEqual_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMoreOrEqualPrioritizedBinaryOp(this);
    }

    public TMoreOrEqual getMoreOrEqual()
    {
        return this._moreOrEqual_;
    }

    public void setMoreOrEqual(TMoreOrEqual node)
    {
        if(this._moreOrEqual_ != null)
        {
            this._moreOrEqual_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._moreOrEqual_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._moreOrEqual_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._moreOrEqual_ == child)
        {
            this._moreOrEqual_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._moreOrEqual_ == oldChild)
        {
            setMoreOrEqual((TMoreOrEqual) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
