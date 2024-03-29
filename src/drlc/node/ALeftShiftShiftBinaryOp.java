/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALeftShiftShiftBinaryOp extends PShiftBinaryOp
{
    private TLeftShift _leftShift_;

    public ALeftShiftShiftBinaryOp()
    {
        // Constructor
    }

    public ALeftShiftShiftBinaryOp(
        @SuppressWarnings("hiding") TLeftShift _leftShift_)
    {
        // Constructor
        setLeftShift(_leftShift_);

    }

    @Override
    public Object clone()
    {
        return new ALeftShiftShiftBinaryOp(
            cloneNode(this._leftShift_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALeftShiftShiftBinaryOp(this);
    }

    public TLeftShift getLeftShift()
    {
        return this._leftShift_;
    }

    public void setLeftShift(TLeftShift node)
    {
        if(this._leftShift_ != null)
        {
            this._leftShift_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._leftShift_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._leftShift_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._leftShift_ == child)
        {
            this._leftShift_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._leftShift_ == oldChild)
        {
            setLeftShift((TLeftShift) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
