/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AReturnType extends PReturnType
{
    private TArrow _arrow_;
    private PType _type_;

    public AReturnType()
    {
        // Constructor
    }

    public AReturnType(
        @SuppressWarnings("hiding") TArrow _arrow_,
        @SuppressWarnings("hiding") PType _type_)
    {
        // Constructor
        setArrow(_arrow_);

        setType(_type_);

    }

    @Override
    public Object clone()
    {
        return new AReturnType(
            cloneNode(this._arrow_),
            cloneNode(this._type_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAReturnType(this);
    }

    public TArrow getArrow()
    {
        return this._arrow_;
    }

    public void setArrow(TArrow node)
    {
        if(this._arrow_ != null)
        {
            this._arrow_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._arrow_ = node;
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
            + toString(this._arrow_)
            + toString(this._type_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._arrow_ == child)
        {
            this._arrow_ = null;
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
        if(this._arrow_ == oldChild)
        {
            setArrow((TArrow) newChild);
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
