/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APrimaryBraceCompoundExpression extends PBraceCompoundExpression
{
    private PPrimaryExpression _primaryExpression_;

    public APrimaryBraceCompoundExpression()
    {
        // Constructor
    }

    public APrimaryBraceCompoundExpression(
        @SuppressWarnings("hiding") PPrimaryExpression _primaryExpression_)
    {
        // Constructor
        setPrimaryExpression(_primaryExpression_);

    }

    @Override
    public Object clone()
    {
        return new APrimaryBraceCompoundExpression(
            cloneNode(this._primaryExpression_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPrimaryBraceCompoundExpression(this);
    }

    public PPrimaryExpression getPrimaryExpression()
    {
        return this._primaryExpression_;
    }

    public void setPrimaryExpression(PPrimaryExpression node)
    {
        if(this._primaryExpression_ != null)
        {
            this._primaryExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._primaryExpression_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._primaryExpression_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._primaryExpression_ == child)
        {
            this._primaryExpression_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._primaryExpression_ == oldChild)
        {
            setPrimaryExpression((PPrimaryExpression) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}