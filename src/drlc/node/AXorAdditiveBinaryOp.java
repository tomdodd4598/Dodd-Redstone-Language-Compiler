/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AXorAdditiveBinaryOp extends PAdditiveBinaryOp
{
    private TXor _xor_;

    public AXorAdditiveBinaryOp()
    {
        // Constructor
    }

    public AXorAdditiveBinaryOp(
        @SuppressWarnings("hiding") TXor _xor_)
    {
        // Constructor
        setXor(_xor_);

    }

    @Override
    public Object clone()
    {
        return new AXorAdditiveBinaryOp(
            cloneNode(this._xor_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAXorAdditiveBinaryOp(this);
    }

    public TXor getXor()
    {
        return this._xor_;
    }

    public void setXor(TXor node)
    {
        if(this._xor_ != null)
        {
            this._xor_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._xor_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._xor_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._xor_ == child)
        {
            this._xor_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._xor_ == oldChild)
        {
            setXor((TXor) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
