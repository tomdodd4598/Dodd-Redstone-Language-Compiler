/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ANotEqualToEqualityBinaryOp extends PEqualityBinaryOp
{
    private TNotEqualTo _notEqualTo_;

    public ANotEqualToEqualityBinaryOp()
    {
        // Constructor
    }

    public ANotEqualToEqualityBinaryOp(
        @SuppressWarnings("hiding") TNotEqualTo _notEqualTo_)
    {
        // Constructor
        setNotEqualTo(_notEqualTo_);

    }

    @Override
    public Object clone()
    {
        return new ANotEqualToEqualityBinaryOp(
            cloneNode(this._notEqualTo_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANotEqualToEqualityBinaryOp(this);
    }

    public TNotEqualTo getNotEqualTo()
    {
        return this._notEqualTo_;
    }

    public void setNotEqualTo(TNotEqualTo node)
    {
        if(this._notEqualTo_ != null)
        {
            this._notEqualTo_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._notEqualTo_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._notEqualTo_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._notEqualTo_ == child)
        {
            this._notEqualTo_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._notEqualTo_ == oldChild)
        {
            setNotEqualTo((TNotEqualTo) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
