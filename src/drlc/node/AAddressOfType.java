/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AAddressOfType extends PType
{
    private TAnd _and_;
    private TMut _mut_;
    private PType _type_;

    public AAddressOfType()
    {
        // Constructor
    }

    public AAddressOfType(
        @SuppressWarnings("hiding") TAnd _and_,
        @SuppressWarnings("hiding") TMut _mut_,
        @SuppressWarnings("hiding") PType _type_)
    {
        // Constructor
        setAnd(_and_);

        setMut(_mut_);

        setType(_type_);

    }

    @Override
    public Object clone()
    {
        return new AAddressOfType(
            cloneNode(this._and_),
            cloneNode(this._mut_),
            cloneNode(this._type_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAddressOfType(this);
    }

    public TAnd getAnd()
    {
        return this._and_;
    }

    public void setAnd(TAnd node)
    {
        if(this._and_ != null)
        {
            this._and_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._and_ = node;
    }

    public TMut getMut()
    {
        return this._mut_;
    }

    public void setMut(TMut node)
    {
        if(this._mut_ != null)
        {
            this._mut_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._mut_ = node;
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
            + toString(this._and_)
            + toString(this._mut_)
            + toString(this._type_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._and_ == child)
        {
            this._and_ = null;
            return;
        }

        if(this._mut_ == child)
        {
            this._mut_ = null;
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
        if(this._and_ == oldChild)
        {
            setAnd((TAnd) newChild);
            return;
        }

        if(this._mut_ == oldChild)
        {
            setMut((TMut) newChild);
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
