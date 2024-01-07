/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AStructCompoundExpression extends PCompoundExpression
{
    private TName _name_;
    private TLBrace _lBrace_;
    private PStructExpressionList _structExpressionList_;
    private TRBrace _rBrace_;

    public AStructCompoundExpression()
    {
        // Constructor
    }

    public AStructCompoundExpression(
        @SuppressWarnings("hiding") TName _name_,
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") PStructExpressionList _structExpressionList_,
        @SuppressWarnings("hiding") TRBrace _rBrace_)
    {
        // Constructor
        setName(_name_);

        setLBrace(_lBrace_);

        setStructExpressionList(_structExpressionList_);

        setRBrace(_rBrace_);

    }

    @Override
    public Object clone()
    {
        return new AStructCompoundExpression(
            cloneNode(this._name_),
            cloneNode(this._lBrace_),
            cloneNode(this._structExpressionList_),
            cloneNode(this._rBrace_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStructCompoundExpression(this);
    }

    public TName getName()
    {
        return this._name_;
    }

    public void setName(TName node)
    {
        if(this._name_ != null)
        {
            this._name_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._name_ = node;
    }

    public TLBrace getLBrace()
    {
        return this._lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(this._lBrace_ != null)
        {
            this._lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBrace_ = node;
    }

    public PStructExpressionList getStructExpressionList()
    {
        return this._structExpressionList_;
    }

    public void setStructExpressionList(PStructExpressionList node)
    {
        if(this._structExpressionList_ != null)
        {
            this._structExpressionList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._structExpressionList_ = node;
    }

    public TRBrace getRBrace()
    {
        return this._rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(this._rBrace_ != null)
        {
            this._rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBrace_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._name_)
            + toString(this._lBrace_)
            + toString(this._structExpressionList_)
            + toString(this._rBrace_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._name_ == child)
        {
            this._name_ = null;
            return;
        }

        if(this._lBrace_ == child)
        {
            this._lBrace_ = null;
            return;
        }

        if(this._structExpressionList_ == child)
        {
            this._structExpressionList_ = null;
            return;
        }

        if(this._rBrace_ == child)
        {
            this._rBrace_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._name_ == oldChild)
        {
            setName((TName) newChild);
            return;
        }

        if(this._lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        if(this._structExpressionList_ == oldChild)
        {
            setStructExpressionList((PStructExpressionList) newChild);
            return;
        }

        if(this._rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}