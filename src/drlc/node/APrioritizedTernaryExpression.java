/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedTernaryExpression extends PTernaryExpression
{
    private PLogicalExpression _logicalExpression_;

    public APrioritizedTernaryExpression()
    {
        // Constructor
    }

    public APrioritizedTernaryExpression(
        @SuppressWarnings("hiding") PLogicalExpression _logicalExpression_)
    {
        // Constructor
        setLogicalExpression(_logicalExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedTernaryExpression(
            cloneNode(this._logicalExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedTernaryExpression(this);
    }

    public PLogicalExpression getLogicalExpression()
    {
        return this._logicalExpression_;
    }

    public void setLogicalExpression(PLogicalExpression node)
    {
        if(this._logicalExpression_ != null)
        {
            this._logicalExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._logicalExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._logicalExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._logicalExpression_ == child)
        {
            this._logicalExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._logicalExpression_ == oldChild)
        {
            setLogicalExpression((PLogicalExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}