/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ABinaryConditionComparativeExpression extends PConditionComparativeExpression
{
    private PConditionComparativeExpression _conditionComparativeExpression_;
    private PComparativeBinaryOp _comparativeBinaryOp_;
    private PConditionAdditiveExpression _conditionAdditiveExpression_;

    public ABinaryConditionComparativeExpression()
    {
        // Constructor
    }

    public ABinaryConditionComparativeExpression(
        @SuppressWarnings("hiding") PConditionComparativeExpression _conditionComparativeExpression_,
        @SuppressWarnings("hiding") PComparativeBinaryOp _comparativeBinaryOp_,
        @SuppressWarnings("hiding") PConditionAdditiveExpression _conditionAdditiveExpression_)
    {
        // Constructor
        setConditionComparativeExpression(_conditionComparativeExpression_);

        setComparativeBinaryOp(_comparativeBinaryOp_);

        setConditionAdditiveExpression(_conditionAdditiveExpression_);

    }

    @Override
    public Object clone()
    {
        return new ABinaryConditionComparativeExpression(
            cloneNode(this._conditionComparativeExpression_),
            cloneNode(this._comparativeBinaryOp_),
            cloneNode(this._conditionAdditiveExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABinaryConditionComparativeExpression(this);
    }

    public PConditionComparativeExpression getConditionComparativeExpression()
    {
        return this._conditionComparativeExpression_;
    }

    public void setConditionComparativeExpression(PConditionComparativeExpression node)
    {
        if(this._conditionComparativeExpression_ != null)
        {
            this._conditionComparativeExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionComparativeExpression_ = node;
    }

    public PComparativeBinaryOp getComparativeBinaryOp()
    {
        return this._comparativeBinaryOp_;
    }

    public void setComparativeBinaryOp(PComparativeBinaryOp node)
    {
        if(this._comparativeBinaryOp_ != null)
        {
            this._comparativeBinaryOp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comparativeBinaryOp_ = node;
    }

    public PConditionAdditiveExpression getConditionAdditiveExpression()
    {
        return this._conditionAdditiveExpression_;
    }

    public void setConditionAdditiveExpression(PConditionAdditiveExpression node)
    {
        if(this._conditionAdditiveExpression_ != null)
        {
            this._conditionAdditiveExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionAdditiveExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._conditionComparativeExpression_)
            + toString(this._comparativeBinaryOp_)
            + toString(this._conditionAdditiveExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._conditionComparativeExpression_ == child)
        {
            this._conditionComparativeExpression_ = null;
            return;
        }

        if(this._comparativeBinaryOp_ == child)
        {
            this._comparativeBinaryOp_ = null;
            return;
        }

        if(this._conditionAdditiveExpression_ == child)
        {
            this._conditionAdditiveExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._conditionComparativeExpression_ == oldChild)
        {
            setConditionComparativeExpression((PConditionComparativeExpression) newChild);
            return;
        }

        if(this._comparativeBinaryOp_ == oldChild)
        {
            setComparativeBinaryOp((PComparativeBinaryOp) newChild);
            return;
        }

        if(this._conditionAdditiveExpression_ == oldChild)
        {
            setConditionAdditiveExpression((PConditionAdditiveExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}