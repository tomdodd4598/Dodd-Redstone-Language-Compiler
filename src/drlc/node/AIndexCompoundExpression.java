/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AIndexCompoundExpression extends PCompoundExpression
{
    private PCompoundExpression _compoundExpression_;
    private TLBracket _lBracket_;
    private PExpression _expression_;
    private TRBracket _rBracket_;

    public AIndexCompoundExpression()
    {
        // Constructor
    }

    public AIndexCompoundExpression(
        @SuppressWarnings("hiding") PCompoundExpression _compoundExpression_,
        @SuppressWarnings("hiding") TLBracket _lBracket_,
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") TRBracket _rBracket_)
    {
        // Constructor
        setCompoundExpression(_compoundExpression_);

        setLBracket(_lBracket_);

        setExpression(_expression_);

        setRBracket(_rBracket_);

    }

    @Override
    public Object clone()
    {
        return new AIndexCompoundExpression(
            cloneNode(this._compoundExpression_),
            cloneNode(this._lBracket_),
            cloneNode(this._expression_),
            cloneNode(this._rBracket_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIndexCompoundExpression(this);
    }

    public PCompoundExpression getCompoundExpression()
    {
        return this._compoundExpression_;
    }

    public void setCompoundExpression(PCompoundExpression node)
    {
        if(this._compoundExpression_ != null)
        {
            this._compoundExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._compoundExpression_ = node;
    }

    public TLBracket getLBracket()
    {
        return this._lBracket_;
    }

    public void setLBracket(TLBracket node)
    {
        if(this._lBracket_ != null)
        {
            this._lBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBracket_ = node;
    }

    public PExpression getExpression()
    {
        return this._expression_;
    }

    public void setExpression(PExpression node)
    {
        if(this._expression_ != null)
        {
            this._expression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expression_ = node;
    }

    public TRBracket getRBracket()
    {
        return this._rBracket_;
    }

    public void setRBracket(TRBracket node)
    {
        if(this._rBracket_ != null)
        {
            this._rBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBracket_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._compoundExpression_)
            + toString(this._lBracket_)
            + toString(this._expression_)
            + toString(this._rBracket_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._compoundExpression_ == child)
        {
            this._compoundExpression_ = null;
            return;
        }

        if(this._lBracket_ == child)
        {
            this._lBracket_ = null;
            return;
        }

        if(this._expression_ == child)
        {
            this._expression_ = null;
            return;
        }

        if(this._rBracket_ == child)
        {
            this._rBracket_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._compoundExpression_ == oldChild)
        {
            setCompoundExpression((PCompoundExpression) newChild);
            return;
        }

        if(this._lBracket_ == oldChild)
        {
            setLBracket((TLBracket) newChild);
            return;
        }

        if(this._expression_ == oldChild)
        {
            setExpression((PExpression) newChild);
            return;
        }

        if(this._rBracket_ == oldChild)
        {
            setRBracket((TRBracket) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}