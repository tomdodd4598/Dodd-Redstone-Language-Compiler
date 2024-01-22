/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class APathPrimaryExpression extends PPrimaryExpression
{
    private PPath _path_;

    public APathPrimaryExpression()
    {
        // Constructor
    }

    public APathPrimaryExpression(
        @SuppressWarnings("hiding") PPath _path_)
    {
        // Constructor
        setPath(_path_);

    }

    @Override
    public Object clone()
    {
        return new APathPrimaryExpression(
            cloneNode(this._path_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPathPrimaryExpression(this);
    }

    public PPath getPath()
    {
        return this._path_;
    }

    public void setPath(PPath node)
    {
        if(this._path_ != null)
        {
            this._path_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._path_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._path_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._path_ == child)
        {
            this._path_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._path_ == oldChild)
        {
            setPath((PPath) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}