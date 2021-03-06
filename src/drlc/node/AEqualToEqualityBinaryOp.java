/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AEqualToEqualityBinaryOp extends PEqualityBinaryOp
{
    private TEqualTo _equalTo_;

    public AEqualToEqualityBinaryOp()
    {
        // Constructor
    }

    public AEqualToEqualityBinaryOp(
        @SuppressWarnings("hiding") TEqualTo _equalTo_)
    {
        // Constructor
        setEqualTo(_equalTo_);

    }

    @Override
    public Object clone()
    {
        return new AEqualToEqualityBinaryOp(
            cloneNode(this._equalTo_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAEqualToEqualityBinaryOp(this);
    }

    public TEqualTo getEqualTo()
    {
        return this._equalTo_;
    }

    public void setEqualTo(TEqualTo node)
    {
        if(this._equalTo_ != null)
        {
            this._equalTo_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._equalTo_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._equalTo_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._equalTo_ == child)
        {
            this._equalTo_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._equalTo_ == oldChild)
        {
            setEqualTo((TEqualTo) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
