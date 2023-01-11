/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import java.util.*;
import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AConditionalSection extends PConditionalSection
{
    private TConditionalBranchSectionKeyword _conditionalBranchSectionKeyword_;
    private PExpressionRvalue _expressionRvalue_;
    private TLBrace _lBrace_;
    private final LinkedList<PBasicSection> _basicSection_ = new LinkedList<PBasicSection>();
    private PStopStatement _stopStatement_;
    private TRBrace _rBrace_;
    private PElseSection _elseSection_;

    public AConditionalSection()
    {
        // Constructor
    }

    public AConditionalSection(
        @SuppressWarnings("hiding") TConditionalBranchSectionKeyword _conditionalBranchSectionKeyword_,
        @SuppressWarnings("hiding") PExpressionRvalue _expressionRvalue_,
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") List<?> _basicSection_,
        @SuppressWarnings("hiding") PStopStatement _stopStatement_,
        @SuppressWarnings("hiding") TRBrace _rBrace_,
        @SuppressWarnings("hiding") PElseSection _elseSection_)
    {
        // Constructor
        setConditionalBranchSectionKeyword(_conditionalBranchSectionKeyword_);

        setExpressionRvalue(_expressionRvalue_);

        setLBrace(_lBrace_);

        setBasicSection(_basicSection_);

        setStopStatement(_stopStatement_);

        setRBrace(_rBrace_);

        setElseSection(_elseSection_);

    }

    @Override
    public Object clone()
    {
        return new AConditionalSection(
            cloneNode(this._conditionalBranchSectionKeyword_),
            cloneNode(this._expressionRvalue_),
            cloneNode(this._lBrace_),
            cloneList(this._basicSection_),
            cloneNode(this._stopStatement_),
            cloneNode(this._rBrace_),
            cloneNode(this._elseSection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAConditionalSection(this);
    }

    public TConditionalBranchSectionKeyword getConditionalBranchSectionKeyword()
    {
        return this._conditionalBranchSectionKeyword_;
    }

    public void setConditionalBranchSectionKeyword(TConditionalBranchSectionKeyword node)
    {
        if(this._conditionalBranchSectionKeyword_ != null)
        {
            this._conditionalBranchSectionKeyword_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._conditionalBranchSectionKeyword_ = node;
    }

    public PExpressionRvalue getExpressionRvalue()
    {
        return this._expressionRvalue_;
    }

    public void setExpressionRvalue(PExpressionRvalue node)
    {
        if(this._expressionRvalue_ != null)
        {
            this._expressionRvalue_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expressionRvalue_ = node;
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

    public PElseSection getElseSection()
    {
        return this._elseSection_;
    }

    public void setElseSection(PElseSection node)
    {
        if(this._elseSection_ != null)
        {
            this._elseSection_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._elseSection_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._conditionalBranchSectionKeyword_)
            + toString(this._expressionRvalue_)
            + toString(this._lBrace_)
            + toString(this._basicSection_)
            + toString(this._stopStatement_)
            + toString(this._rBrace_)
            + toString(this._elseSection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._conditionalBranchSectionKeyword_ == child)
        {
            this._conditionalBranchSectionKeyword_ = null;
            return;
        }

        if(this._expressionRvalue_ == child)
        {
            this._expressionRvalue_ = null;
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

        if(this._elseSection_ == child)
        {
            this._elseSection_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._conditionalBranchSectionKeyword_ == oldChild)
        {
            setConditionalBranchSectionKeyword((TConditionalBranchSectionKeyword) newChild);
            return;
        }

        if(this._expressionRvalue_ == oldChild)
        {
            setExpressionRvalue((PExpressionRvalue) newChild);
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

        if(this._elseSection_ == oldChild)
        {
            setElseSection((PElseSection) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
