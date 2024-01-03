/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ABinaryConditionalExpression4 extends PConditionalExpression4
{
    private PConditionalExpression4 _conditionalExpression4_;
    private PMultiplicativeBinaryOp _multiplicativeBinaryOp_;
    private PConditionalExpression5 _conditionalExpression5_;

    public ABinaryConditionalExpression4()
    {
        // Constructor
    }

    public ABinaryConditionalExpression4(
        @SuppressWarnings("hiding") PConditionalExpression4 _conditionalExpression4_,
        @SuppressWarnings("hiding") PMultiplicativeBinaryOp _multiplicativeBinaryOp_,
        @SuppressWarnings("hiding") PConditionalExpression5 _conditionalExpression5_)
    {
        // Constructor
        setConditionalExpression4(_conditionalExpression4_);

        setMultiplicativeBinaryOp(_multiplicativeBinaryOp_);

        setConditionalExpression5(_conditionalExpression5_);

    }

    @Override
    public Object clone()
    {
        return new ABinaryConditionalExpression4(
            cloneNode(this._conditionalExpression4_),
            cloneNode(this._multiplicativeBinaryOp_),
            cloneNode(this._conditionalExpression5_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABinaryConditionalExpression4(this);
    }

    public PConditionalExpression4 getConditionalExpression4()
    {
        return this._conditionalExpression4_;
    }

    public void setConditionalExpression4(PConditionalExpression4 node)
    {
        if(this._conditionalExpression4_ != null)
        {
            this._conditionalExpression4_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionalExpression4_ = node;
    }

    public PMultiplicativeBinaryOp getMultiplicativeBinaryOp()
    {
        return this._multiplicativeBinaryOp_;
    }

    public void setMultiplicativeBinaryOp(PMultiplicativeBinaryOp node)
    {
        if(this._multiplicativeBinaryOp_ != null)
        {
            this._multiplicativeBinaryOp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._multiplicativeBinaryOp_ = node;
    }

    public PConditionalExpression5 getConditionalExpression5()
    {
        return this._conditionalExpression5_;
    }

    public void setConditionalExpression5(PConditionalExpression5 node)
    {
        if(this._conditionalExpression5_ != null)
        {
            this._conditionalExpression5_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionalExpression5_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._conditionalExpression4_)
            + toString(this._multiplicativeBinaryOp_)
            + toString(this._conditionalExpression5_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._conditionalExpression4_ == child)
        {
            this._conditionalExpression4_ = null;
            return;
        }

        if(this._multiplicativeBinaryOp_ == child)
        {
            this._multiplicativeBinaryOp_ = null;
            return;
        }

        if(this._conditionalExpression5_ == child)
        {
            this._conditionalExpression5_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._conditionalExpression4_ == oldChild)
        {
            setConditionalExpression4((PConditionalExpression4) newChild);
            return;
        }

        if(this._multiplicativeBinaryOp_ == oldChild)
        {
            setMultiplicativeBinaryOp((PMultiplicativeBinaryOp) newChild);
            return;
        }

        if(this._conditionalExpression5_ == oldChild)
        {
            setConditionalExpression5((PConditionalExpression5) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}