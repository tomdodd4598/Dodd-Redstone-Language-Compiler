/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ARightShiftShiftBinaryOp extends PShiftBinaryOp
{
    private TRightShift _rightShift_;

    public ARightShiftShiftBinaryOp()
    {
        // Constructor
    }

    public ARightShiftShiftBinaryOp(
        @SuppressWarnings("hiding") TRightShift _rightShift_)
    {
        // Constructor
        setRightShift(_rightShift_);

    }

    @Override
    public Object clone()
    {
        return new ARightShiftShiftBinaryOp(
            cloneNode(this._rightShift_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseARightShiftShiftBinaryOp(this);
    }

    public TRightShift getRightShift()
    {
        return this._rightShift_;
    }

    public void setRightShift(TRightShift node)
    {
        if(this._rightShift_ != null)
        {
            this._rightShift_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rightShift_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._rightShift_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._rightShift_ == child)
        {
            this._rightShift_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._rightShift_ == oldChild)
        {
            setRightShift((TRightShift) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}