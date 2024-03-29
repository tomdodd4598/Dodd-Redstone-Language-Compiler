/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADoubleAddressOfType extends PType
{
    private TLogicalAnd _logicalAnd_;
    private TMut _mut_;
    private PType _type_;

    public ADoubleAddressOfType()
    {
        // Constructor
    }

    public ADoubleAddressOfType(
        @SuppressWarnings("hiding") TLogicalAnd _logicalAnd_,
        @SuppressWarnings("hiding") TMut _mut_,
        @SuppressWarnings("hiding") PType _type_)
    {
        // Constructor
        setLogicalAnd(_logicalAnd_);

        setMut(_mut_);

        setType(_type_);

    }

    @Override
    public Object clone()
    {
        return new ADoubleAddressOfType(
            cloneNode(this._logicalAnd_),
            cloneNode(this._mut_),
            cloneNode(this._type_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADoubleAddressOfType(this);
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
            + toString(this._logicalAnd_)
            + toString(this._mut_)
            + toString(this._type_);
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
        if(this._logicalAnd_ == oldChild)
        {
            setLogicalAnd((TLogicalAnd) newChild);
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
