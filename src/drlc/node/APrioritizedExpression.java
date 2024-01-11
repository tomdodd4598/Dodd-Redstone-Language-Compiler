/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedExpression extends PExpression
{
    private PAssignmentExpression _assignmentExpression_;

    public APrioritizedExpression()
    {
        // Constructor
    }

    public APrioritizedExpression(
        @SuppressWarnings("hiding") PAssignmentExpression _assignmentExpression_)
    {
        // Constructor
        setAssignmentExpression(_assignmentExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedExpression(
            cloneNode(this._assignmentExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedExpression(this);
    }

    public PAssignmentExpression getAssignmentExpression()
    {
        return this._assignmentExpression_;
    }

    public void setAssignmentExpression(PAssignmentExpression node)
    {
        if(this._assignmentExpression_ != null)
        {
            this._assignmentExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._assignmentExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._assignmentExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._assignmentExpression_ == child)
        {
            this._assignmentExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._assignmentExpression_ == oldChild)
        {
            setAssignmentExpression((PAssignmentExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
