/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AElseBlock extends PElseBlock
{
    private TElse _else_;
    private TLBrace _lBrace_;
    private final LinkedList<PBasicSection> _basicSection_ = new LinkedList<PBasicSection>();
    private PStopStatement _stopStatement_;
    private TRBrace _rBrace_;

    public AElseBlock()
    {
        // Constructor
    }

    public AElseBlock(
        @SuppressWarnings("hiding") TElse _else_,
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") List<?> _basicSection_,
        @SuppressWarnings("hiding") PStopStatement _stopStatement_,
        @SuppressWarnings("hiding") TRBrace _rBrace_)
    {
        // Constructor
        setElse(_else_);

        setLBrace(_lBrace_);

        setBasicSection(_basicSection_);

        setStopStatement(_stopStatement_);

        setRBrace(_rBrace_);

    }

    @Override
    public Object clone()
    {
        return new AElseBlock(
            cloneNode(this._else_),
            cloneNode(this._lBrace_),
            cloneList(this._basicSection_),
            cloneNode(this._stopStatement_),
            cloneNode(this._rBrace_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAElseBlock(this);
    }

    public TElse getElse()
    {
        return this._else_;
    }

    public void setElse(TElse node)
    {
        if(this._else_ != null)
        {
            this._else_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._else_ = node;
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

    public LinkedList<PBasicSection> getBasicSection()
    {
        return this._basicSection_;
    }

    public void setBasicSection(List<?> list)
    {
        for(PBasicSection e : this._basicSection_)
        {
            e.parent(null);
        }
        this._basicSection_.clear();

        for(Object obj_e : list)
        {
            PBasicSection e = (PBasicSection) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._basicSection_.add(e);
        }
    }

    public PStopStatement getStopStatement()
    {
        return this._stopStatement_;
    }

    public void setStopStatement(PStopStatement node)
    {
        if(this._stopStatement_ != null)
        {
            this._stopStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._stopStatement_ = node;
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
            + toString(this._else_)
            + toString(this._lBrace_)
            + toString(this._basicSection_)
            + toString(this._stopStatement_)
            + toString(this._rBrace_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._else_ == child)
        {
            this._else_ = null;
            return;
        }

        if(this._lBrace_ == child)
        {
            this._lBrace_ = null;
            return;
        }

        if(this._basicSection_.remove(child))
        {
            return;
        }

        if(this._stopStatement_ == child)
        {
            this._stopStatement_ = null;
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
        if(this._else_ == oldChild)
        {
            setElse((TElse) newChild);
            return;
        }

        if(this._lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        for(ListIterator<PBasicSection> i = this._basicSection_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PBasicSection) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._stopStatement_ == oldChild)
        {
            setStopStatement((PStopStatement) newChild);
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
