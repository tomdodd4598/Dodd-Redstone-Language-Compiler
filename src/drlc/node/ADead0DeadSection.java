/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADead0DeadSection extends PDeadSection
{
    private PRuntimeSection _runtimeSection_;

    public ADead0DeadSection()
    {
        // Constructor
    }

    public ADead0DeadSection(
        @SuppressWarnings("hiding") PRuntimeSection _runtimeSection_)
    {
        // Constructor
        setRuntimeSection(_runtimeSection_);

    }

    @Override
    public Object clone()
    {
        return new ADead0DeadSection(
            cloneNode(this._runtimeSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADead0DeadSection(this);
    }

    public PRuntimeSection getRuntimeSection()
    {
        return this._runtimeSection_;
    }

    public void setRuntimeSection(PRuntimeSection node)
    {
        if(this._runtimeSection_ != null)
        {
            this._runtimeSection_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._runtimeSection_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._runtimeSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._runtimeSection_ == child)
        {
            this._runtimeSection_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._runtimeSection_ == oldChild)
        {
            setRuntimeSection((PRuntimeSection) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
