package net.marioplus.migratingfromspringfox.convertor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilter;

import java.util.Collections;
import java.util.List;

public class NormalAnnotationExprFilterConvertor<T> implements IFilterConvertor<NormalAnnotationExpr> {

    private final Class<T> childNodeClass;

    private final NodeNameFilter<NormalAnnotationExpr, AnnotationExpr> filter;

    private final NodeNameConvertor<NormalAnnotationExpr, AnnotationExpr> nameConvertor;

    private final List<IFilterConvertor<T>> childNodeConvertors;

    public NormalAnnotationExprFilterConvertor(Class<T> childNodeClass, String name, String nameName, List<IFilterConvertor<T>> childNodeConvertors) {
        this.childNodeClass = childNodeClass;
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = new NodeNameConvertor<>(nameName);
        this.childNodeConvertors = childNodeConvertors;
    }

    public NormalAnnotationExprFilterConvertor(Class<T> childNodeClass, String name, List<IFilterConvertor<T>> childNodeConvertors) {
        this(childNodeClass, name, null, childNodeConvertors);
    }

    public NormalAnnotationExprFilterConvertor(Class<T> childNodeClass, String name, String nameName) {
        this(childNodeClass, name, nameName, Collections.emptyList());
    }

    @Override
    public boolean filter(NormalAnnotationExpr expr) {
        return filter.filter(expr);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void convert(NormalAnnotationExpr expr) {
        if (!filter(expr)) {
            return;
        }
        if (nameConvertor != null) {
            nameConvertor.convert(expr);
        }
        List<Node> childNodes = expr.getChildNodes();
        if (childNodes.isEmpty() && childNodeConvertors.isEmpty()) {
            return;
        }
        for (Node childNode : childNodes) {
            if (!(childNode.getClass().isAssignableFrom(childNodeClass))) {
                continue;
            }
            for (IFilterConvertor<T> childNodeConvertor : childNodeConvertors) {
                if (childNodeConvertor.filter((T) childNode)) {
                    childNodeConvertor.convert((T) childNode);
                }
            }
        }
    }
}
