/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AArithmeticRightShiftShiftBinaryOp extends PShiftBinaryOp
{
    private TArithmeticRightShift _arithmeticRightShift_;

    public AArithmeticRightShiftShiftBinaryOp()
    {
        // Constructor
    }

    public AArithmeticRightShiftShiftBinaryOp(
        @SuppressWarnings("hiding") TArithmeticRightShift _arithmeticRightShift_)
    {
        // Constructor
        setArithmeticRightShift(_arithmeticRightShift_);

    }

    @Override
    public Object clone()
    {
        return new AArithmeticRightShiftShiftBinaryOp(
            cloneNode(this._arithmeticRightShift_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAArithmeticRightShiftShiftBinaryOp(this);
    }

    public TArithmeticRightShift getArithmeticRightShift()
    {
        return this._arithmeticRightShift_;
    }

    public void setArithmeticRightShift(TArithmeticRightShift node)
    {
        if(this._arithmeticRightShift_ != null)
        {
            this._arithmeticRightShift_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._arithmeticRightShift_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._arithmeticRightShift_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._arithmeticRightShift_ == child)
        {
            this._arithmeticRightShift_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._arithmeticRightShift_ == oldChild)
        {
            setArithmeticRightShift((TArithmeticRightShift) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
