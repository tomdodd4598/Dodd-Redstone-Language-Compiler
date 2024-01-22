/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedBraceExpression extends PBraceExpression
{
    private PBraceAssignmentExpression _braceAssignmentExpression_;

    public APrioritizedBraceExpression()
    {
        // Constructor
    }

    public APrioritizedBraceExpression(
        @SuppressWarnings("hiding") PBraceAssignmentExpression _braceAssignmentExpression_)
    {
        // Constructor
        setBraceAssignmentExpression(_braceAssignmentExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedBraceExpression(
            cloneNode(this._braceAssignmentExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedBraceExpression(this);
    }

    public PBraceAssignmentExpression getBraceAssignmentExpression()
    {
        return this._braceAssignmentExpression_;
    }

    public void setBraceAssignmentExpression(PBraceAssignmentExpression node)
    {
        if(this._braceAssignmentExpression_ != null)
        {
            this._braceAssignmentExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._braceAssignmentExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._braceAssignmentExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._braceAssignmentExpression_ == child)
        {
            this._braceAssignmentExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._braceAssignmentExpression_ == oldChild)
        {
            setBraceAssignmentExpression((PBraceAssignmentExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}