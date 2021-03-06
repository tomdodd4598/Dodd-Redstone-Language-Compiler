/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AReturnStopStatement extends PStopStatement
{
    private TReturn _return_;
    private PSeparator _separator_;
    private final LinkedList<PDeadSection> _deadSection_ = new LinkedList<PDeadSection>();

    public AReturnStopStatement()
    {
        // Constructor
    }

    public AReturnStopStatement(
        @SuppressWarnings("hiding") TReturn _return_,
        @SuppressWarnings("hiding") PSeparator _separator_,
        @SuppressWarnings("hiding") List<?> _deadSection_)
    {
        // Constructor
        setReturn(_return_);

        setSeparator(_separator_);

        setDeadSection(_deadSection_);

    }

    @Override
    public Object clone()
    {
        return new AReturnStopStatement(
            cloneNode(this._return_),
            cloneNode(this._separator_),
            cloneList(this._deadSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAReturnStopStatement(this);
    }

    public TReturn getReturn()
    {
        return this._return_;
    }

    public void setReturn(TReturn node)
    {
        if(this._return_ != null)
        {
            this._return_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._return_ = node;
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

    public LinkedList<PDeadSection> getDeadSection()
    {
        return this._deadSection_;
    }

    public void setDeadSection(List<?> list)
    {
        for(PDeadSection e : this._deadSection_)
        {
            e.parent(null);
        }
        this._deadSection_.clear();

        for(Object obj_e : list)
        {
            PDeadSection e = (PDeadSection) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._deadSection_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._return_)
            + toString(this._separator_)
            + toString(this._deadSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._return_ == child)
        {
            this._return_ = null;
            return;
        }

        if(this._separator_ == child)
        {
            this._separator_ = null;
            return;
        }

        if(this._deadSection_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._return_ == oldChild)
        {
            setReturn((TReturn) newChild);
            return;
        }

        if(this._separator_ == oldChild)
        {
            setSeparator((PSeparator) newChild);
            return;
        }

        for(ListIterator<PDeadSection> i = this._deadSection_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PDeadSection) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
