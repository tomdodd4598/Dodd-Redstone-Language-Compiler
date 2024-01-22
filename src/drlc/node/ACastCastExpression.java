/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ACastCastExpression extends PCastExpression
{
    private PUnaryExpression _unaryExpression_;
    private TAs _as_;
    private PType _type_;

    public ACastCastExpression()
    {
        // Constructor
    }

    public ACastCastExpression(
        @SuppressWarnings("hiding") PUnaryExpression _unaryExpression_,
        @SuppressWarnings("hiding") TAs _as_,
        @SuppressWarnings("hiding") PType _type_)
    {
        // Constructor
        setUnaryExpression(_unaryExpression_);

        setAs(_as_);

        setType(_type_);

    }

    @Override
    public Object clone()
    {
        return new ACastCastExpression(
            cloneNode(this._unaryExpression_),
            cloneNode(this._as_),
            cloneNode(this._type_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACastCastExpression(this);
    }

    public PUnaryExpression getUnaryExpression()
    {
        return this._unaryExpression_;
    }

    public void setUnaryExpression(PUnaryExpression node)
    {
        if(this._unaryExpression_ != null)
        {
            this._unaryExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._unaryExpression_ = node;
    }

    public TAs getAs()
    {
        return this._as_;
    }

    public void setAs(TAs node)
    {
        if(this._as_ != null)
        {
            this._as_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._as_ = node;
    }

    public PType getType()
    {
        return this._type_;
    }

    public void setType(PType node)
    {
        if(this._type_ != null)
        {
            this._type_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._type_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._unaryExpression_)
            + toString(this._as_)
            + toString(this._type_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._unaryExpression_ == child)
        {
            this._unaryExpression_ = null;
            return;
        }

        if(this._as_ == child)
        {
            this._as_ = null;
            return;
        }

        if(this._type_ == child)
        {
            this._type_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._unaryExpression_ == oldChild)
        {
            setUnaryExpression((PUnaryExpression) newChild);
            return;
        }

        if(this._as_ == oldChild)
        {
            setAs((TAs) newChild);
            return;
        }

        if(this._type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}