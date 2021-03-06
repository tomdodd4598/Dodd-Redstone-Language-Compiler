/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AToBoolUnaryOp extends PUnaryOp
{
    private TToBool _toBool_;

    public AToBoolUnaryOp()
    {
        // Constructor
    }

    public AToBoolUnaryOp(
        @SuppressWarnings("hiding") TToBool _toBool_)
    {
        // Constructor
        setToBool(_toBool_);

    }

    @Override
    public Object clone()
    {
        return new AToBoolUnaryOp(
            cloneNode(this._toBool_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAToBoolUnaryOp(this);
    }

    public TToBool getToBool()
    {
        return this._toBool_;
    }

    public void setToBool(TToBool node)
    {
        if(this._toBool_ != null)
        {
            this._toBool_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._toBool_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._toBool_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._toBool_ == child)
        {
            this._toBool_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._toBool_ == oldChild)
        {
            setToBool((TToBool) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
