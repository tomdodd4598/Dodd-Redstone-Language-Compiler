/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADead1DeadSection extends PDeadSection
{
    private TExit _exit_;
    private PExpression _expression_;
    private TSemicolon _semicolon_;

    public ADead1DeadSection()
    {
        // Constructor
    }

    public ADead1DeadSection(
        @SuppressWarnings("hiding") TExit _exit_,
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") TSemicolon _semicolon_)
    {
        // Constructor
        setExit(_exit_);

        setExpression(_expression_);

        setSemicolon(_semicolon_);

    }

    @Override
    public Object clone()
    {
        return new ADead1DeadSection(
            cloneNode(this._exit_),
            cloneNode(this._expression_),
            cloneNode(this._semicolon_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADead1DeadSection(this);
    }

    public TExit getExit()
    {
        return this._exit_;
    }

    public void setExit(TExit node)
    {
        if(this._exit_ != null)
        {
            this._exit_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._exit_ = node;
    }

    public PExpression getExpression()
    {
        return this._expression_;
    }

    public void setExpression(PExpression node)
    {
        if(this._expression_ != null)
        {
            this._expression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expression_ = node;
    }

    public TSemicolon getSemicolon()
    {
        return this._semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(this._semicolon_ != null)
        {
            this._semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._semicolon_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._exit_)
            + toString(this._expression_)
            + toString(this._semicolon_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._exit_ == child)
        {
            this._exit_ = null;
            return;
        }

        if(this._expression_ == child)
        {
            this._expression_ = null;
            return;
        }

        if(this._semicolon_ == child)
        {
            this._semicolon_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._exit_ == oldChild)
        {
            setExit((TExit) newChild);
            return;
        }

        if(this._expression_ == oldChild)
        {
            setExpression((PExpression) newChild);
            return;
        }

        if(this._semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
