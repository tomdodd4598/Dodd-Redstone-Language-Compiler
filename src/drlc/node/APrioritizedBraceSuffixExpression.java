/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedBraceSuffixExpression extends PBraceSuffixExpression
{
    private PBraceCallExpression _braceCallExpression_;

    public APrioritizedBraceSuffixExpression()
    {
        // Constructor
    }

    public APrioritizedBraceSuffixExpression(
        @SuppressWarnings("hiding") PBraceCallExpression _braceCallExpression_)
    {
        // Constructor
        setBraceCallExpression(_braceCallExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedBraceSuffixExpression(
            cloneNode(this._braceCallExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedBraceSuffixExpression(this);
    }

    public PBraceCallExpression getBraceCallExpression()
    {
        return this._braceCallExpression_;
    }

    public void setBraceCallExpression(PBraceCallExpression node)
    {
        if(this._braceCallExpression_ != null)
        {
            this._braceCallExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._braceCallExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._braceCallExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._braceCallExpression_ == child)
        {
            this._braceCallExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._braceCallExpression_ == oldChild)
        {
            setBraceCallExpression((PBraceCallExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}