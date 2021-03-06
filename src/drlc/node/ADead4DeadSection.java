/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADead4DeadSection extends PDeadSection
{
    private TBreak _break_;
    private PSeparator _separator_;

    public ADead4DeadSection()
    {
        // Constructor
    }

    public ADead4DeadSection(
        @SuppressWarnings("hiding") TBreak _break_,
        @SuppressWarnings("hiding") PSeparator _separator_)
    {
        // Constructor
        setBreak(_break_);

        setSeparator(_separator_);

    }

    @Override
    public Object clone()
    {
        return new ADead4DeadSection(
            cloneNode(this._break_),
            cloneNode(this._separator_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADead4DeadSection(this);
    }

    public TBreak getBreak()
    {
        return this._break_;
    }

    public void setBreak(TBreak node)
    {
        if(this._break_ != null)
        {
            this._break_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._break_ = node;
    }

    public PSeparator getSeparator()
    {
        return this._separator_;
    }

    public void setSeparator(PSeparator node)
    {
        if(this._separator_ != null)
        {
            this._separator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._separator_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._break_)
            + toString(this._separator_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._break_ == child)
        {
            this._break_ = null;
            return;
        }

        if(this._separator_ == child)
        {
            this._separator_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._break_ == oldChild)
        {
            setBreak((TBreak) newChild);
            return;
        }

        if(this._separator_ == oldChild)
        {
            setSeparator((PSeparator) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
