/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AExpressionList extends PExpressionList
{
    private PExpression _expression_;
    private final LinkedList<PExpressionListTail> _expressionListTail_ = new LinkedList<PExpressionListTail>();
    private TComma _comma_;

    public AExpressionList()
    {
        // Constructor
    }

    public AExpressionList(
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") List<?> _expressionListTail_,
        @SuppressWarnings("hiding") TComma _comma_)
    {
        // Constructor
        setExpression(_expression_);

        setExpressionListTail(_expressionListTail_);

        setComma(_comma_);

    }

    @Override
    public Object clone()
    {
        return new AExpressionList(
            cloneNode(this._expression_),
            cloneList(this._expressionListTail_),
            cloneNode(this._comma_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAExpressionList(this);
    }

    public PExpression getExpression()
    {
        return this._expression_;
    }

    public void setExpression(PExpression node)
    {
        if(this._expression_ != null)
        {
            this._expression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expression_ = node;
    }

    public LinkedList<PExpressionListTail> getExpressionListTail()
    {
        return this._expressionListTail_;
    }

    public void setExpressionListTail(List<?> list)
    {
        for(PExpressionListTail e : this._expressionListTail_)
        {
            e.parent(null);
        }
        this._expressionListTail_.clear();

        for(Object obj_e : list)
        {
            PExpressionListTail e = (PExpressionListTail) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._expressionListTail_.add(e);
        }
    }

    public TComma getComma()
    {
        return this._comma_;
    }

    public void setComma(TComma node)
    {
        if(this._comma_ != null)
        {
            this._comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comma_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._expression_)
            + toString(this._expressionListTail_)
            + toString(this._comma_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._expression_ == child)
        {
            this._expression_ = null;
            return;
        }

        if(this._expressionListTail_.remove(child))
        {
            return;
        }

        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._expression_ == oldChild)
        {
            setExpression((PExpression) newChild);
            return;
        }

        for(ListIterator<PExpressionListTail> i = this._expressionListTail_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PExpressionListTail) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}