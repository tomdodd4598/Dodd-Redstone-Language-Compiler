/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AScopedBody extends PScopedBody
{
    private final LinkedList<PRuntimeSection> _runtimeSection_ = new LinkedList<PRuntimeSection>();
    private PStopStatement _stopStatement_;

    public AScopedBody()
    {
        // Constructor
    }

    public AScopedBody(
        @SuppressWarnings("hiding") List<?> _runtimeSection_,
        @SuppressWarnings("hiding") PStopStatement _stopStatement_)
    {
        // Constructor
        setRuntimeSection(_runtimeSection_);

        setStopStatement(_stopStatement_);

    }

    @Override
    public Object clone()
    {
        return new AScopedBody(
            cloneList(this._runtimeSection_),
            cloneNode(this._stopStatement_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAScopedBody(this);
    }

    public LinkedList<PRuntimeSection> getRuntimeSection()
    {
        return this._runtimeSection_;
    }

    public void setRuntimeSection(List<?> list)
    {
        for(PRuntimeSection e : this._runtimeSection_)
        {
            e.parent(null);
        }
        this._runtimeSection_.clear();

        for(Object obj_e : list)
        {
            PRuntimeSection e = (PRuntimeSection) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._runtimeSection_.add(e);
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

    @Override
    public String toString()
    {
        return ""
            + toString(this._runtimeSection_)
            + toString(this._stopStatement_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._runtimeSection_.remove(child))
        {
            return;
        }

        if(this._stopStatement_ == child)
        {
            this._stopStatement_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PRuntimeSection> i = this._runtimeSection_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PRuntimeSection) newChild);
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

        throw new RuntimeException("Not a child.");
    }
}