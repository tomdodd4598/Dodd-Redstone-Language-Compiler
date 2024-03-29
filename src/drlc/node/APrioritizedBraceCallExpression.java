/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedBraceCallExpression extends PBraceCallExpression
{
    private PBraceCompoundExpression _braceCompoundExpression_;

    public APrioritizedBraceCallExpression()
    {
        // Constructor
    }

    public APrioritizedBraceCallExpression(
        @SuppressWarnings("hiding") PBraceCompoundExpression _braceCompoundExpression_)
    {
        // Constructor
        setBraceCompoundExpression(_braceCompoundExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedBraceCallExpression(
            cloneNode(this._braceCompoundExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedBraceCallExpression(this);
    }

    public PBraceCompoundExpression getBraceCompoundExpression()
    {
        return this._braceCompoundExpression_;
    }

    public void setBraceCompoundExpression(PBraceCompoundExpression node)
    {
        if(this._braceCompoundExpression_ != null)
        {
            this._braceCompoundExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._braceCompoundExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._braceCompoundExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._braceCompoundExpression_ == child)
        {
            this._braceCompoundExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._braceCompoundExpression_ == oldChild)
        {
            setBraceCompoundExpression((PBraceCompoundExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
