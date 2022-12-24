package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ExpressionConvertor<T extends Node, E extends Expression, S extends Expression> implements IConvertor<T> {

    public static final Function<Expression, Expression> INT_2_STRING = intExpr -> new StringLiteralExpr(((IntegerLiteralExpr) intExpr).getValue());

    private final Function<T, E> getExprFn;
    private final BiConsumer<T, S> setExprConsumer;
    private final Function<E, S> convertFn;

    public ExpressionConvertor(Function<T, E> getExprFn, BiConsumer<T, S> setExprConsumer, Function<E, S> convertFn) {
        this.getExprFn = getExprFn;
        this.setExprConsumer = setExprConsumer;
        this.convertFn = convertFn;
    }

    @Override
    public void convert(T t) {
        E oldExpr = getExprFn.apply(t);
        S newExpr = convertFn.apply(oldExpr);
        setExprConsumer.accept(t, newExpr);
    }
}
