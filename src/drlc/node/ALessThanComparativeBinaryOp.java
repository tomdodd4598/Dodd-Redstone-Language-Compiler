/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALessThanComparativeBinaryOp extends PComparativeBinaryOp
{
    private TLessThan _lessThan_;

    public ALessThanComparativeBinaryOp()
    {
        // Constructor
    }

    public ALessThanComparativeBinaryOp(
        @SuppressWarnings("hiding") TLessThan _lessThan_)
    {
        // Constructor
        setLessThan(_lessThan_);

    }

    @Override
    public Object clone()
    {
        return new ALessThanComparativeBinaryOp(
            cloneNode(this._lessThan_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALessThanComparativeBinaryOp(this);
    }

    public TLessThan getLessThan()
    {
        return this._lessThan_;
    }

    public void setLessThan(TLessThan node)
    {
        if(this._lessThan_ != null)
        {
            this._lessThan_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lessThan_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._lessThan_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._lessThan_ == child)
        {
            this._lessThan_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._lessThan_ == oldChild)
        {
            setLessThan((TLessThan) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}