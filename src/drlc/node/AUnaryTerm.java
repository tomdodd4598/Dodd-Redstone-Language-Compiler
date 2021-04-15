/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AUnaryTerm extends PTerm
{
    private PUnaryOp _unaryOp_;
    private PTerm _term_;

    public AUnaryTerm()
    {
        // Constructor
    }

    public AUnaryTerm(
        @SuppressWarnings("hiding") PUnaryOp _unaryOp_,
        @SuppressWarnings("hiding") PTerm _term_)
    {
        // Constructor
        setUnaryOp(_unaryOp_);

        setTerm(_term_);

    }

    @Override
    public Object clone()
    {
        return new AUnaryTerm(
            cloneNode(this._unaryOp_),
            cloneNode(this._term_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUnaryTerm(this);
    }

    public PUnaryOp getUnaryOp()
    {
        return this._unaryOp_;
    }

    public void setUnaryOp(PUnaryOp node)
    {
        if(this._unaryOp_ != null)
        {
            this._unaryOp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._unaryOp_ = node;
    }

    public PTerm getTerm()
    {
        return this._term_;
    }

    public void setTerm(PTerm node)
    {
        if(this._term_ != null)
        {
            this._term_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._term_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._unaryOp_)
            + toString(this._term_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._unaryOp_ == child)
        {
            this._unaryOp_ = null;
            return;
        }

        if(this._term_ == child)
        {
            this._term_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._unaryOp_ == oldChild)
        {
            setUnaryOp((PUnaryOp) newChild);
            return;
        }

        if(this._term_ == oldChild)
        {
            setTerm((PTerm) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
