/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AStructDefinitionStaticSection extends PStaticSection
{
    private PStructDefinition _structDefinition_;

    public AStructDefinitionStaticSection()
    {
        // Constructor
    }

    public AStructDefinitionStaticSection(
        @SuppressWarnings("hiding") PStructDefinition _structDefinition_)
    {
        // Constructor
        setStructDefinition(_structDefinition_);

    }

    @Override
    public Object clone()
    {
        return new AStructDefinitionStaticSection(
            cloneNode(this._structDefinition_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStructDefinitionStaticSection(this);
    }

    public PStructDefinition getStructDefinition()
    {
        return this._structDefinition_;
    }

    public void setStructDefinition(PStructDefinition node)
    {
        if(this._structDefinition_ != null)
        {
            this._structDefinition_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._structDefinition_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._structDefinition_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._structDefinition_ == child)
        {
            this._structDefinition_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._structDefinition_ == oldChild)
        {
            setStructDefinition((PStructDefinition) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}