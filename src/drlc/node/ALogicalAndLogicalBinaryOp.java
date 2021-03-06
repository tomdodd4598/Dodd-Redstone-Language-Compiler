/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALogicalAndLogicalBinaryOp extends PLogicalBinaryOp
{
    private TLogicalAnd _logicalAnd_;

    public ALogicalAndLogicalBinaryOp()
    {
        // Constructor
    }

    public ALogicalAndLogicalBinaryOp(
        @SuppressWarnings("hiding") TLogicalAnd _logicalAnd_)
    {
        // Constructor
        setLogicalAnd(_logicalAnd_);

    }

    @Override
    public Object clone()
    {
        return new ALogicalAndLogicalBinaryOp(
            cloneNode(this._logicalAnd_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALogicalAndLogicalBinaryOp(this);
    }

    public TLogicalAnd getLogicalAnd()
    {
        return this._logicalAnd_;
    }

    public void setLogicalAnd(TLogicalAnd node)
    {
        if(this._logicalAnd_ != null)
        {
            this._logicalAnd_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._logicalAnd_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._logicalAnd_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._logicalAnd_ == child)
        {
            this._logicalAnd_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._logicalAnd_ == oldChild)
        {
            setLogicalAnd((TLogicalAnd) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
