/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedConditionalExpression1 extends PConditionalExpression1
{
    private PConditionalExpression2 _conditionalExpression2_;

    public APrioritizedConditionalExpression1()
    {
        // Constructor
    }

    public APrioritizedConditionalExpression1(
        @SuppressWarnings("hiding") PConditionalExpression2 _conditionalExpression2_)
    {
        // Constructor
        setConditionalExpression2(_conditionalExpression2_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedConditionalExpression1(
            cloneNode(this._conditionalExpression2_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedConditionalExpression1(this);
    }

    public PConditionalExpression2 getConditionalExpression2()
    {
        return this._conditionalExpression2_;
    }

    public void setConditionalExpression2(PConditionalExpression2 node)
    {
        if(this._conditionalExpression2_ != null)
        {
            this._conditionalExpression2_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionalExpression2_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._conditionalExpression2_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._conditionalExpression2_ == child)
        {
            this._conditionalExpression2_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._conditionalExpression2_ == oldChild)
        {
            setConditionalExpression2((PConditionalExpression2) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}