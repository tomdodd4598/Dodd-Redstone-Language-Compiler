/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADeclaratorListTail extends PDeclaratorListTail
{
    private TComma _comma_;
    private PDeclarator _declarator_;

    public ADeclaratorListTail()
    {
        // Constructor
    }

    public ADeclaratorListTail(
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PDeclarator _declarator_)
    {
        // Constructor
        setComma(_comma_);

        setDeclarator(_declarator_);

    }

    @Override
    public Object clone()
    {
        return new ADeclaratorListTail(
            cloneNode(this._comma_),
            cloneNode(this._declarator_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADeclaratorListTail(this);
    }

    public TComma getComma()
    {
        return this._comma_;
    }

    public void setComma(TComma node)
    {
        if(this._comma_ != null)
        {
            this._comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comma_ = node;
    }

    public PDeclarator getDeclarator()
    {
        return this._declarator_;
    }

    public void setDeclarator(PDeclarator node)
    {
        if(this._declarator_ != null)
        {
            this._declarator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._declarator_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._comma_)
            + toString(this._declarator_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._declarator_ == child)
        {
            this._declarator_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._declarator_ == oldChild)
        {
            setDeclarator((PDeclarator) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
