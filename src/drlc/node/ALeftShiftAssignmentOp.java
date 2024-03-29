/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALeftShiftAssignmentOp extends PAssignmentOp
{
    private TLeftShiftEquals _leftShiftEquals_;

    public ALeftShiftAssignmentOp()
    {
        // Constructor
    }

    public ALeftShiftAssignmentOp(
        @SuppressWarnings("hiding") TLeftShiftEquals _leftShiftEquals_)
    {
        // Constructor
        setLeftShiftEquals(_leftShiftEquals_);

    }

    @Override
    public Object clone()
    {
        return new ALeftShiftAssignmentOp(
            cloneNode(this._leftShiftEquals_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALeftShiftAssignmentOp(this);
    }

    public TLeftShiftEquals getLeftShiftEquals()
    {
        return this._leftShiftEquals_;
    }

    public void setLeftShiftEquals(TLeftShiftEquals node)
    {
        if(this._leftShiftEquals_ != null)
        {
            this._leftShiftEquals_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._leftShiftEquals_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._leftShiftEquals_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._leftShiftEquals_ == child)
        {
            this._leftShiftEquals_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._leftShiftEquals_ == oldChild)
        {
            setLeftShiftEquals((TLeftShiftEquals) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
