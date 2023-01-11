/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AIncludingIdentifierParameter extends PParameter
{
    private PIdentifier _identifier_;
    private PTypeAnnotation _typeAnnotation_;

    public AIncludingIdentifierParameter()
    {
        // Constructor
    }

    public AIncludingIdentifierParameter(
        @SuppressWarnings("hiding") PIdentifier _identifier_,
        @SuppressWarnings("hiding") PTypeAnnotation _typeAnnotation_)
    {
        // Constructor
        setIdentifier(_identifier_);

        setTypeAnnotation(_typeAnnotation_);

    }

    @Override
    public Object clone()
    {
        return new AIncludingIdentifierParameter(
            cloneNode(this._identifier_),
            cloneNode(this._typeAnnotation_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIncludingIdentifierParameter(this);
    }

    public PIdentifier getIdentifier()
    {
        return this._identifier_;
    }

    public void setIdentifier(PIdentifier node)
    {
        if(this._identifier_ != null)
        {
            this._identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._identifier_ = node;
    }

    public PTypeAnnotation getTypeAnnotation()
    {
        return this._typeAnnotation_;
    }

    public void setTypeAnnotation(PTypeAnnotation node)
    {
        if(this._typeAnnotation_ != null)
        {
            this._typeAnnotation_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._typeAnnotation_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._identifier_)
            + toString(this._typeAnnotation_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._identifier_ == child)
        {
            this._identifier_ = null;
            return;
        }

        if(this._typeAnnotation_ == child)
        {
            this._typeAnnotation_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._identifier_ == oldChild)
        {
            setIdentifier((PIdentifier) newChild);
            return;
        }

        if(this._typeAnnotation_ == oldChild)
        {
            setTypeAnnotation((PTypeAnnotation) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
