/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AConditionalSection extends PConditionalSection
{
    private TConditionalBranchKeyword _conditionalBranchKeyword_;
    private PExpression _expression_;
    private TLBrace _lBrace_;
    private PScopeContents _scopeContents_;
    private TRBrace _rBrace_;
    private PElseSection _elseSection_;

    public AConditionalSection()
    {
        // Constructor
    }

    public AConditionalSection(
        @SuppressWarnings("hiding") TConditionalBranchKeyword _conditionalBranchKeyword_,
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") PScopeContents _scopeContents_,
        @SuppressWarnings("hiding") TRBrace _rBrace_,
        @SuppressWarnings("hiding") PElseSection _elseSection_)
    {
        // Constructor
        setConditionalBranchKeyword(_conditionalBranchKeyword_);

        setExpression(_expression_);

        setLBrace(_lBrace_);

        setScopeContents(_scopeContents_);

        setRBrace(_rBrace_);

        setElseSection(_elseSection_);

    }

    @Override
    public Object clone()
    {
        return new AConditionalSection(
            cloneNode(this._conditionalBranchKeyword_),
            cloneNode(this._expression_),
            cloneNode(this._lBrace_),
            cloneNode(this._scopeContents_),
            cloneNode(this._rBrace_),
            cloneNode(this._elseSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAConditionalSection(this);
    }

    public TConditionalBranchKeyword getConditionalBranchKeyword()
    {
        return this._conditionalBranchKeyword_;
    }

    public void setConditionalBranchKeyword(TConditionalBranchKeyword node)
    {
        if(this._conditionalBranchKeyword_ != null)
        {
            this._conditionalBranchKeyword_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionalBranchKeyword_ = node;
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

    public PScopeContents getScopeContents()
    {
        return this._scopeContents_;
    }

    public void setScopeContents(PScopeContents node)
    {
        if(this._scopeContents_ != null)
        {
            this._scopeContents_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._scopeContents_ = node;
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

    public PElseSection getElseSection()
    {
        return this._elseSection_;
    }

    public void setElseSection(PElseSection node)
    {
        if(this._elseSection_ != null)
        {
            this._elseSection_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._elseSection_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._conditionalBranchKeyword_)
            + toString(this._expression_)
            + toString(this._lBrace_)
            + toString(this._scopeContents_)
            + toString(this._rBrace_)
            + toString(this._elseSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._conditionalBranchKeyword_ == child)
        {
            this._conditionalBranchKeyword_ = null;
            return;
        }

        if(this._expression_ == child)
        {
            this._expression_ = null;
            return;
        }

        if(this._lBrace_ == child)
        {
            this._lBrace_ = null;
            return;
        }

        if(this._scopeContents_ == child)
        {
            this._scopeContents_ = null;
            return;
        }

        if(this._rBrace_ == child)
        {
            this._rBrace_ = null;
            return;
        }

        if(this._elseSection_ == child)
        {
            this._elseSection_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._conditionalBranchKeyword_ == oldChild)
        {
            setConditionalBranchKeyword((TConditionalBranchKeyword) newChild);
            return;
        }

        if(this._expression_ == oldChild)
        {
            setExpression((PExpression) newChild);
            return;
        }

        if(this._lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        if(this._scopeContents_ == oldChild)
        {
            setScopeContents((PScopeContents) newChild);
            return;
        }

        if(this._rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

        if(this._elseSection_ == oldChild)
        {
            setElseSection((PElseSection) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}