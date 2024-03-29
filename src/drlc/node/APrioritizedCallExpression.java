/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedCallExpression extends PCallExpression
{
    private PCompoundExpression _compoundExpression_;

    public APrioritizedCallExpression()
    {
        // Constructor
    }

    public APrioritizedCallExpression(
        @SuppressWarnings("hiding") PCompoundExpression _compoundExpression_)
    {
        // Constructor
        setCompoundExpression(_compoundExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedCallExpression(
            cloneNode(this._compoundExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedCallExpression(this);
    }

    public PCompoundExpression getCompoundExpression()
    {
        return this._compoundExpression_;
    }

    public void setCompoundExpression(PCompoundExpression node)
    {
        if(this._compoundExpression_ != null)
        {
            this._compoundExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._compoundExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._compoundExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._compoundExpression_ == child)
        {
            this._compoundExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._compoundExpression_ == oldChild)
        {
            setCompoundExpression((PCompoundExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
