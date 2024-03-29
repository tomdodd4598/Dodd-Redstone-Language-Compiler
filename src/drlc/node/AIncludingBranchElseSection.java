/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AIncludingBranchElseSection extends PElseSection
{
    private TElse _else_;
    private PConditionalSection _conditionalSection_;

    public AIncludingBranchElseSection()
    {
        // Constructor
    }

    public AIncludingBranchElseSection(
        @SuppressWarnings("hiding") TElse _else_,
        @SuppressWarnings("hiding") PConditionalSection _conditionalSection_)
    {
        // Constructor
        setElse(_else_);

        setConditionalSection(_conditionalSection_);

    }

    @Override
    public Object clone()
    {
        return new AIncludingBranchElseSection(
            cloneNode(this._else_),
            cloneNode(this._conditionalSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIncludingBranchElseSection(this);
    }

    public TElse getElse()
    {
        return this._else_;
    }

    public void setElse(TElse node)
    {
        if(this._else_ != null)
        {
            this._else_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._else_ = node;
    }

    public PConditionalSection getConditionalSection()
    {
        return this._conditionalSection_;
    }

    public void setConditionalSection(PConditionalSection node)
    {
        if(this._conditionalSection_ != null)
        {
            this._conditionalSection_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionalSection_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._else_)
            + toString(this._conditionalSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._else_ == child)
        {
            this._else_ = null;
            return;
        }

        if(this._conditionalSection_ == child)
        {
            this._conditionalSection_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._else_ == oldChild)
        {
            setElse((TElse) newChild);
            return;
        }

        if(this._conditionalSection_ == oldChild)
        {
            setConditionalSection((PConditionalSection) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
