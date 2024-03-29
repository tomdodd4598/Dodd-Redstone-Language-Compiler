/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class ALeafUseTree extends PUseTree
{
    private final LinkedList<PPathPrefix> _pathPrefix_ = new LinkedList<PPathPrefix>();
    private PPathSegment _pathSegment_;
    private PUseAlias _useAlias_;

    public ALeafUseTree()
    {
        // Constructor
    }

    public ALeafUseTree(
        @SuppressWarnings("hiding") List<?> _pathPrefix_,
        @SuppressWarnings("hiding") PPathSegment _pathSegment_,
        @SuppressWarnings("hiding") PUseAlias _useAlias_)
    {
        // Constructor
        setPathPrefix(_pathPrefix_);

        setPathSegment(_pathSegment_);

        setUseAlias(_useAlias_);

    }

    @Override
    public Object clone()
    {
        return new ALeafUseTree(
            cloneList(this._pathPrefix_),
            cloneNode(this._pathSegment_),
            cloneNode(this._useAlias_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALeafUseTree(this);
    }

    public LinkedList<PPathPrefix> getPathPrefix()
    {
        return this._pathPrefix_;
    }

    public void setPathPrefix(List<?> list)
    {
        for(PPathPrefix e : this._pathPrefix_)
        {
            e.parent(null);
        }
        this._pathPrefix_.clear();

        for(Object obj_e : list)
        {
            PPathPrefix e = (PPathPrefix) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._pathPrefix_.add(e);
        }
    }

    public PPathSegment getPathSegment()
    {
        return this._pathSegment_;
    }

    public void setPathSegment(PPathSegment node)
    {
        if(this._pathSegment_ != null)
        {
            this._pathSegment_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._pathSegment_ = node;
    }

    public PUseAlias getUseAlias()
    {
        return this._useAlias_;
    }

    public void setUseAlias(PUseAlias node)
    {
        if(this._useAlias_ != null)
        {
            this._useAlias_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._useAlias_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._pathPrefix_)
            + toString(this._pathSegment_)
            + toString(this._useAlias_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._pathPrefix_.remove(child))
        {
            return;
        }

        if(this._pathSegment_ == child)
        {
            this._pathSegment_ = null;
            return;
        }

        if(this._useAlias_ == child)
        {
            this._useAlias_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PPathPrefix> i = this._pathPrefix_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PPathPrefix) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._pathSegment_ == oldChild)
        {
            setPathSegment((PPathSegment) newChild);
            return;
        }

        if(this._useAlias_ == oldChild)
        {
            setUseAlias((PUseAlias) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
