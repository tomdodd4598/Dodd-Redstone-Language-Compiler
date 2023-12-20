/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALoopIterativeSection extends PIterativeSection
{
    private PIterativeSectionLabel _iterativeSectionLabel_;
    private TLoop _loop_;
    private TLBrace _lBrace_;
    private PScopeContents _scopeContents_;
    private TRBrace _rBrace_;

    public ALoopIterativeSection()
    {
        // Constructor
    }

    public ALoopIterativeSection(
        @SuppressWarnings("hiding") PIterativeSectionLabel _iterativeSectionLabel_,
        @SuppressWarnings("hiding") TLoop _loop_,
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") PScopeContents _scopeContents_,
        @SuppressWarnings("hiding") TRBrace _rBrace_)
    {
        // Constructor
        setIterativeSectionLabel(_iterativeSectionLabel_);

        setLoop(_loop_);

        setLBrace(_lBrace_);

        setScopeContents(_scopeContents_);

        setRBrace(_rBrace_);

    }

    @Override
    public Object clone()
    {
        return new ALoopIterativeSection(
            cloneNode(this._iterativeSectionLabel_),
            cloneNode(this._loop_),
            cloneNode(this._lBrace_),
            cloneNode(this._scopeContents_),
            cloneNode(this._rBrace_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALoopIterativeSection(this);
    }

    public PIterativeSectionLabel getIterativeSectionLabel()
    {
        return this._iterativeSectionLabel_;
    }

    public void setIterativeSectionLabel(PIterativeSectionLabel node)
    {
        if(this._iterativeSectionLabel_ != null)
        {
            this._iterativeSectionLabel_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._iterativeSectionLabel_ = node;
    }

    public TLoop getLoop()
    {
        return this._loop_;
    }

    public void setLoop(TLoop node)
    {
        if(this._loop_ != null)
        {
            this._loop_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._loop_ = node;
    }

    public TLBrace getLBrace()
    {
        return this._lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(this._lBrace_ != null)
        {
            this._lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBrace_ = node;
    }

    public PScopeContents getScopeContents()
    {
        return this._scopeContents_;
    }

    public void setScopeContents(PScopeContents node)
    {
        if(this._scopeContents_ != null)
        {
            this._scopeContents_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._scopeContents_ = node;
    }

    public TRBrace getRBrace()
    {
        return this._rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(this._rBrace_ != null)
        {
            this._rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBrace_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._iterativeSectionLabel_)
            + toString(this._loop_)
            + toString(this._lBrace_)
            + toString(this._scopeContents_)
            + toString(this._rBrace_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._iterativeSectionLabel_ == child)
        {
            this._iterativeSectionLabel_ = null;
            return;
        }

        if(this._loop_ == child)
        {
            this._loop_ = null;
            return;
        }

        if(this._lBrace_ == child)
        {
            this._lBrace_ = null;
            return;
        }

        if(this._scopeContents_ == child)
        {
            this._scopeContents_ = null;
            return;
        }

        if(this._rBrace_ == child)
        {
            this._rBrace_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._iterativeSectionLabel_ == oldChild)
        {
            setIterativeSectionLabel((PIterativeSectionLabel) newChild);
            return;
        }

        if(this._loop_ == oldChild)
        {
            setLoop((TLoop) newChild);
            return;
        }

        if(this._lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        if(this._scopeContents_ == oldChild)
        {
            setScopeContents((PScopeContents) newChild);
            return;
        }

        if(this._rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}