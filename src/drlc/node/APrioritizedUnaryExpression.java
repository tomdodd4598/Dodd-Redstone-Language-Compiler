/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrioritizedUnaryExpression extends PUnaryExpression
{
    private PSuffixExpression _suffixExpression_;

    public APrioritizedUnaryExpression()
    {
        // Constructor
    }

    public APrioritizedUnaryExpression(
        @SuppressWarnings("hiding") PSuffixExpression _suffixExpression_)
    {
        // Constructor
        setSuffixExpression(_suffixExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrioritizedUnaryExpression(
            cloneNode(this._suffixExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrioritizedUnaryExpression(this);
    }

    public PSuffixExpression getSuffixExpression()
    {
        return this._suffixExpression_;
    }

    public void setSuffixExpression(PSuffixExpression node)
    {
        if(this._suffixExpression_ != null)
        {
            this._suffixExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._suffixExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._suffixExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._suffixExpression_ == child)
        {
            this._suffixExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._suffixExpression_ == oldChild)
        {
            setSuffixExpression((PSuffixExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
