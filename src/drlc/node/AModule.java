/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AModule extends PModule
{
    private final LinkedList<PStaticSection> _staticSection_ = new LinkedList<PStaticSection>();

    public AModule()
    {
        // Constructor
    }

    public AModule(
        @SuppressWarnings("hiding") List<?> _staticSection_)
    {
        // Constructor
        setStaticSection(_staticSection_);

    }

    @Override
    public Object clone()
    {
        return new AModule(
            cloneList(this._staticSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAModule(this);
    }

    public LinkedList<PStaticSection> getStaticSection()
    {
        return this._staticSection_;
    }

    public void setStaticSection(List<?> list)
    {
        for(PStaticSection e : this._staticSection_)
        {
            e.parent(null);
        }
        this._staticSection_.clear();

        for(Object obj_e : list)
        {
            PStaticSection e = (PStaticSection) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._staticSection_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._staticSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._staticSection_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PStaticSection> i = this._staticSection_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PStaticSection) newChild);
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
