/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ADead3DeadSection extends PDeadSection
{
    private TContinue _continue_;
    private final LinkedList<TSemicolon> _semicolon_ = new LinkedList<TSemicolon>();

    public ADead3DeadSection()
    {
        // Constructor
    }

    public ADead3DeadSection(
        @SuppressWarnings("hiding") TContinue _continue_,
        @SuppressWarnings("hiding") List<?> _semicolon_)
    {
        // Constructor
        setContinue(_continue_);

        setSemicolon(_semicolon_);

    }

    @Override
    public Object clone()
    {
        return new ADead3DeadSection(
            cloneNode(this._continue_),
            cloneList(this._semicolon_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADead3DeadSection(this);
    }

    public TContinue getContinue()
    {
        return this._continue_;
    }

    public void setContinue(TContinue node)
    {
        if(this._continue_ != null)
        {
            this._continue_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._continue_ = node;
    }

    public LinkedList<TSemicolon> getSemicolon()
    {
        return this._semicolon_;
    }

    public void setSemicolon(List<?> list)
    {
        for(TSemicolon e : this._semicolon_)
        {
            e.parent(null);
        }
        this._semicolon_.clear();

        for(Object obj_e : list)
        {
            TSemicolon e = (TSemicolon) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._semicolon_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._continue_)
            + toString(this._semicolon_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._continue_ == child)
        {
            this._continue_ = null;
            return;
        }

        if(this._semicolon_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._continue_ == oldChild)
        {
            setContinue((TContinue) newChild);
            return;
        }

        for(ListIterator<TSemicolon> i = this._semicolon_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((TSemicolon) newChild);
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
