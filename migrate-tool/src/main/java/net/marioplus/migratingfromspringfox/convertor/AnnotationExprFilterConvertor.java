package net.marioplus.migratingfromspringfox.convertor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilter;

import java.util.Collections;
import java.util.List;

public class AnnotationExprFilterConvertor<T extends AnnotationExpr, C> implements IFilterConvertor<T> {

    private final Class<C> childNodeClass;

    private final NodeNameFilter<T, AnnotationExpr> filter;

    private final NodeNameConvertor<T, AnnotationExpr> nameConvertor;

    private final List<IFilterConvertor<C>> childNodeConvertors;

    public AnnotationExprFilterConvertor(Class<C> childNodeClass, String name, String newName, List<IFilterConvertor<C>> childNodeConvertors) {
        this.childNodeClass = childNodeClass;
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = new NodeNameConvertor<>(newName);
        this.childNodeConvertors = childNodeConvertors;
    }

    public AnnotationExprFilterConvertor(Class<C> childNodeClass, String name, List<IFilterConvertor<C>> childNodeConvertors) {
        this(childNodeClass, name, null, childNodeConvertors);
    }

    public AnnotationExprFilterConvertor(Class<C> childNodeClass, String name, String newName) {
        this(childNodeClass, name, newName, Collections.emptyList());
    }

    @Override
    public boolean filter(T expr) {
        return filter.filter(expr);
    }

    @Override
    public void convert(T expr) {
        if (!filter(expr)) {
            return;
        }
        nameConvertor.convert(expr);
        List<Node> childNodes = expr.getChildNodes();
        if (childNodes.isEmpty() && childNodeConvertors.isEmpty()) {
            return;
        }
        for (Node node : childNodes) {
            if (node instanceof ArrayInitializerExpr) {
                for (Expression value : ((ArrayInitializerExpr) node).getValues()) {
                    this.convertChildNode(value);
                }
            } else {
                this.convertChildNode(node);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void convertChildNode(Node node) {
        if (node.getClass().isAssignableFrom(childNodeClass)) {
            for (IFilterConvertor<C> childNodeConvertor : childNodeConvertors) {
                if (childNodeConvertor.filter((C) node)) {
                    childNodeConvertor.convert((C) node);
                }
            }
        }
    }
}
