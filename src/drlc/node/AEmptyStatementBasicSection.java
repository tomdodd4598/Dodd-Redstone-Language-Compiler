/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AEmptyStatementBasicSection extends PBasicSection
{
    private PEmptyStatement _emptyStatement_;

    public AEmptyStatementBasicSection()
    {
        // Constructor
    }

    public AEmptyStatementBasicSection(
        @SuppressWarnings("hiding") PEmptyStatement _emptyStatement_)
    {
        // Constructor
        setEmptyStatement(_emptyStatement_);

    }

    @Override
    public Object clone()
    {
        return new AEmptyStatementBasicSection(
            cloneNode(this._emptyStatement_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAEmptyStatementBasicSection(this);
    }

    public PEmptyStatement getEmptyStatement()
    {
        return this._emptyStatement_;
    }

    public void setEmptyStatement(PEmptyStatement node)
    {
        if(this._emptyStatement_ != null)
        {
            this._emptyStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._emptyStatement_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._emptyStatement_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._emptyStatement_ == child)
        {
            this._emptyStatement_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._emptyStatement_ == oldChild)
        {
            setEmptyStatement((PEmptyStatement) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
