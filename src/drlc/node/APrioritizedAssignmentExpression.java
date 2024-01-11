/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedAssignmentExpression extends PAssignmentExpression
{
    private PTernaryExpression _ternaryExpression_;

    public APrioritizedAssignmentExpression()
    {
        // Constructor
    }

    public APrioritizedAssignmentExpression(
        @SuppressWarnings("hiding") PTernaryExpression _ternaryExpression_)
    {
        // Constructor
        setTernaryExpression(_ternaryExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedAssignmentExpression(
            cloneNode(this._ternaryExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedAssignmentExpression(this);
    }

    public PTernaryExpression getTernaryExpression()
    {
        return this._ternaryExpression_;
    }

    public void setTernaryExpression(PTernaryExpression node)
    {
        if(this._ternaryExpression_ != null)
        {
            this._ternaryExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._ternaryExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._ternaryExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._ternaryExpression_ == child)
        {
            this._ternaryExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._ternaryExpression_ == oldChild)
        {
            setTernaryExpression((PTernaryExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}