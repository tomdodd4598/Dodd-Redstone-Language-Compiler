/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALabelledStructExpressionList extends PStructExpressionList
{
    private PLabelledExpressionList _labelledExpressionList_;

    public ALabelledStructExpressionList()
    {
        // Constructor
    }

    public ALabelledStructExpressionList(
        @SuppressWarnings("hiding") PLabelledExpressionList _labelledExpressionList_)
    {
        // Constructor
        setLabelledExpressionList(_labelledExpressionList_);

    }

    @Override
    public Object clone()
    {
        return new ALabelledStructExpressionList(
            cloneNode(this._labelledExpressionList_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALabelledStructExpressionList(this);
    }

    public PLabelledExpressionList getLabelledExpressionList()
    {
        return this._labelledExpressionList_;
    }

    public void setLabelledExpressionList(PLabelledExpressionList node)
    {
        if(this._labelledExpressionList_ != null)
        {
            this._labelledExpressionList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._labelledExpressionList_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._labelledExpressionList_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._labelledExpressionList_ == child)
        {
            this._labelledExpressionList_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._labelledExpressionList_ == oldChild)
        {
            setLabelledExpressionList((PLabelledExpressionList) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
