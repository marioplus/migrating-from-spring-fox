package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.expr.AnnotationExpr;
import net.marioplus.migratingfromspringfox.convertor.AnnotationExprFilterConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnnotationExprConvertors<T extends AnnotationExpr> {

    public List<AnnotationExprFilterConvertor<T, ?>> filterConvertors = new ArrayList<>();

    public AnnotationExprConvertors<T> add(AnnotationExprFilterConvertor<T, ?> filterConvertor) {
        this.filterConvertors.add(filterConvertor);
        return this;
    }

    public void convert(T expr, Consumer<Void> changed) {
        for (AnnotationExprFilterConvertor<T, ?> filterConvertor : filterConvertors) {
            if (filterConvertor.filter(expr)) {
                changed.accept(null);
                filterConvertor.convert(expr);
            }
        }
    }
}
