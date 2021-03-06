/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AGotoStatementBasicSection extends PBasicSection
{
    private PGotoStatement _gotoStatement_;

    public AGotoStatementBasicSection()
    {
        // Constructor
    }

    public AGotoStatementBasicSection(
        @SuppressWarnings("hiding") PGotoStatement _gotoStatement_)
    {
        // Constructor
        setGotoStatement(_gotoStatement_);

    }

    @Override
    public Object clone()
    {
        return new AGotoStatementBasicSection(
            cloneNode(this._gotoStatement_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAGotoStatementBasicSection(this);
    }

    public PGotoStatement getGotoStatement()
    {
        return this._gotoStatement_;
    }

    public void setGotoStatement(PGotoStatement node)
    {
        if(this._gotoStatement_ != null)
        {
            this._gotoStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._gotoStatement_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._gotoStatement_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._gotoStatement_ == child)
        {
            this._gotoStatement_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._gotoStatement_ == oldChild)
        {
            setGotoStatement((PGotoStatement) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
