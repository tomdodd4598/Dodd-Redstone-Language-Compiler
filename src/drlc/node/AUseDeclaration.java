/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AUseDeclaration extends PUseDeclaration
{
    private TUse _use_;
    private PUseTree _useTree_;
    private TSemicolon _semicolon_;

    public AUseDeclaration()
    {
        // Constructor
    }

    public AUseDeclaration(
        @SuppressWarnings("hiding") TUse _use_,
        @SuppressWarnings("hiding") PUseTree _useTree_,
        @SuppressWarnings("hiding") TSemicolon _semicolon_)
    {
        // Constructor
        setUse(_use_);

        setUseTree(_useTree_);

        setSemicolon(_semicolon_);

    }

    @Override
    public Object clone()
    {
        return new AUseDeclaration(
            cloneNode(this._use_),
            cloneNode(this._useTree_),
            cloneNode(this._semicolon_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUseDeclaration(this);
    }

    public TUse getUse()
    {
        return this._use_;
    }

    public void setUse(TUse node)
    {
        if(this._use_ != null)
        {
            this._use_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._use_ = node;
    }

    public PUseTree getUseTree()
    {
        return this._useTree_;
    }

    public void setUseTree(PUseTree node)
    {
        if(this._useTree_ != null)
        {
            this._useTree_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._useTree_ = node;
    }

    public TSemicolon getSemicolon()
    {
        return this._semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(this._semicolon_ != null)
        {
            this._semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._semicolon_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._use_)
            + toString(this._useTree_)
            + toString(this._semicolon_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._use_ == child)
        {
            this._use_ = null;
            return;
        }

        if(this._useTree_ == child)
        {
            this._useTree_ = null;
            return;
        }

        if(this._semicolon_ == child)
        {
            this._semicolon_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._use_ == oldChild)
        {
            setUse((TUse) newChild);
            return;
        }

        if(this._useTree_ == oldChild)
        {
            setUseTree((PUseTree) newChild);
            return;
        }

        if(this._semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}