/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADirectiveEnd extends PDirectiveEnd
{
    private THash _hash_;
    private TEnd _end_;

    public ADirectiveEnd()
    {
        // Constructor
    }

    public ADirectiveEnd(
        @SuppressWarnings("hiding") THash _hash_,
        @SuppressWarnings("hiding") TEnd _end_)
    {
        // Constructor
        setHash(_hash_);

        setEnd(_end_);

    }

    @Override
    public Object clone()
    {
        return new ADirectiveEnd(
            cloneNode(this._hash_),
            cloneNode(this._end_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADirectiveEnd(this);
    }

    public THash getHash()
    {
        return this._hash_;
    }

    public void setHash(THash node)
    {
        if(this._hash_ != null)
        {
            this._hash_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._hash_ = node;
    }

    public TEnd getEnd()
    {
        return this._end_;
    }

    public void setEnd(TEnd node)
    {
        if(this._end_ != null)
        {
            this._end_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._end_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._hash_)
            + toString(this._end_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._hash_ == child)
        {
            this._hash_ = null;
            return;
        }

        if(this._end_ == child)
        {
            this._end_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._hash_ == oldChild)
        {
            setHash((THash) newChild);
            return;
        }

        if(this._end_ == oldChild)
        {
            setEnd((TEnd) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
