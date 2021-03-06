/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AUnit extends PUnit
{
    private PSetup _setup_;
    private PProgram _program_;

    public AUnit()
    {
        // Constructor
    }

    public AUnit(
        @SuppressWarnings("hiding") PSetup _setup_,
        @SuppressWarnings("hiding") PProgram _program_)
    {
        // Constructor
        setSetup(_setup_);

        setProgram(_program_);

    }

    @Override
    public Object clone()
    {
        return new AUnit(
            cloneNode(this._setup_),
            cloneNode(this._program_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUnit(this);
    }

    public PSetup getSetup()
    {
        return this._setup_;
    }

    public void setSetup(PSetup node)
    {
        if(this._setup_ != null)
        {
            this._setup_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._setup_ = node;
    }

    public PProgram getProgram()
    {
        return this._program_;
    }

    public void setProgram(PProgram node)
    {
        if(this._program_ != null)
        {
            this._program_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._program_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._setup_)
            + toString(this._program_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._setup_ == child)
        {
            this._setup_ = null;
            return;
        }

        if(this._program_ == child)
        {
            this._program_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._setup_ == oldChild)
        {
            setSetup((PSetup) newChild);
            return;
        }

        if(this._program_ == oldChild)
        {
            setProgram((PProgram) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
