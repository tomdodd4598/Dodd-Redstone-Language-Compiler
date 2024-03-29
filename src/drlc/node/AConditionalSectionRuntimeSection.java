/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AConditionalSectionRuntimeSection extends PRuntimeSection
{
    private PConditionalSection _conditionalSection_;

    public AConditionalSectionRuntimeSection()
    {
        // Constructor
    }

    public AConditionalSectionRuntimeSection(
        @SuppressWarnings("hiding") PConditionalSection _conditionalSection_)
    {
        // Constructor
        setConditionalSection(_conditionalSection_);

    }

    @Override
    public Object clone()
    {
        return new AConditionalSectionRuntimeSection(
            cloneNode(this._conditionalSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAConditionalSectionRuntimeSection(this);
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
            + toString(this._conditionalSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
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
        if(this._conditionalSection_ == oldChild)
        {
            setConditionalSection((PConditionalSection) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
