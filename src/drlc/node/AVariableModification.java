/* This file was generated by SableCC (http://www.sablecc.org/). */

package drlc.node;

import drlc.analysis.*;

@SuppressWarnings("nls")
public final class AVariableModification extends PVariableModification
{
    private PNonAddressVariable _nonAddressVariable_;
    private TEquals _equals_;
    private PExpression _expression_;
    private TSemicolon _semicolon_;

    public AVariableModification()
    {
        // Constructor
    }

    public AVariableModification(
        @SuppressWarnings("hiding") PNonAddressVariable _nonAddressVariable_,
        @SuppressWarnings("hiding") TEquals _equals_,
        @SuppressWarnings("hiding") PExpression _expression_,
        @SuppressWarnings("hiding") TSemicolon _semicolon_)
    {
        // Constructor
        setNonAddressVariable(_nonAddressVariable_);

        setEquals(_equals_);

        setExpression(_expression_);

        setSemicolon(_semicolon_);

    }

    @Override
    public Object clone()
    {
        return new AVariableModification(
            cloneNode(this._nonAddressVariable_),
            cloneNode(this._equals_),
            cloneNode(this._expression_),
            cloneNode(this._semicolon_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAVariableModification(this);
    }

    public PNonAddressVariable getNonAddressVariable()
    {
        return this._nonAddressVariable_;
    }

    public void setNonAddressVariable(PNonAddressVariable node)
    {
        if(this._nonAddressVariable_ != null)
        {
            this._nonAddressVariable_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._nonAddressVariable_ = node;
    }

    public TEquals getEquals()
    {
        return this._equals_;
    }

    public void setEquals(TEquals node)
    {
        if(this._equals_ != null)
        {
            this._equals_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._equals_ = node;
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

    public TSemicolon getSemicolon()
    {
        return this._semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(this._semicolon_ != null)
        {
            this._semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._semicolon_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._nonAddressVariable_)
            + toString(this._equals_)
            + toString(this._expression_)
            + toString(this._semicolon_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._nonAddressVariable_ == child)
        {
            this._nonAddressVariable_ = null;
            return;
        }

        if(this._equals_ == child)
        {
            this._equals_ = null;
            return;
        }

        if(this._expression_ == child)
        {
            this._expression_ = null;
            return;
        }

        if(this._semicolon_ == child)
        {
            this._semicolon_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._nonAddressVariable_ == oldChild)
        {
            setNonAddressVariable((PNonAddressVariable) newChild);
            return;
        }

        if(this._equals_ == oldChild)
        {
            setEquals((TEquals) newChild);
            return;
        }

        if(this._expression_ == oldChild)
        {
            setExpression((PExpression) newChild);
            return;
        }

        if(this._semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
