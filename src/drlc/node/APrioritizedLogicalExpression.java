/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedLogicalExpression extends PLogicalExpression
{
    private PEqualityExpression _equalityExpression_;

    public APrioritizedLogicalExpression()
    {
        // Constructor
    }

    public APrioritizedLogicalExpression(
        @SuppressWarnings("hiding") PEqualityExpression _equalityExpression_)
    {
        // Constructor
        setEqualityExpression(_equalityExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedLogicalExpression(
            cloneNode(this._equalityExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedLogicalExpression(this);
    }

    public PEqualityExpression getEqualityExpression()
    {
        return this._equalityExpression_;
    }

    public void setEqualityExpression(PEqualityExpression node)
    {
        if(this._equalityExpression_ != null)
        {
            this._equalityExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._equalityExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._equalityExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._equalityExpression_ == child)
        {
            this._equalityExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._equalityExpression_ == oldChild)
        {
            setEqualityExpression((PEqualityExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
