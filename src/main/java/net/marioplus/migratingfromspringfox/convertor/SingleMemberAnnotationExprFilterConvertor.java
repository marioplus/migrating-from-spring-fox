package net.marioplus.migratingfromspringfox.convertor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilter;

import java.util.Collections;
import java.util.List;

public class SingleMemberAnnotationExprFilterConvertor<T> implements IFilterConvertor<SingleMemberAnnotationExpr> {

    private final Class<T> memberClass;
    private final NodeNameFilter<SingleMemberAnnotationExpr, AnnotationExpr> filter;

    private final NodeNameConvertor<SingleMemberAnnotationExpr, AnnotationExpr> nameConvertor;

    private final List<IFilterConvertor<T>> childNodeConvertors;

    public SingleMemberAnnotationExprFilterConvertor(Class<T> memberClass, String name, String nameName, List<IFilterConvertor<T>> childNodeConvertors) {
        this.memberClass = memberClass;
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = new NodeNameConvertor<>(nameName);
        this.childNodeConvertors = childNodeConvertors;
    }

    public SingleMemberAnnotationExprFilterConvertor(Class<T> memberClass, String name, List<IFilterConvertor<T>> childNodeConvertors) {
        this(memberClass, name, null, childNodeConvertors);
    }

    public SingleMemberAnnotationExprFilterConvertor(String name, String nameName) {
        this(null, name, nameName, Collections.emptyList());
    }

    @Override
    public boolean filter(SingleMemberAnnotationExpr expr) {
        return filter.filter(expr);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void convert(SingleMemberAnnotationExpr expr) {
        if (!filter(expr)) {
            return;
        }
        if (nameConvertor != null) {
            nameConvertor.convert(expr);
        }
        List<Node> childNodes = expr.getMemberValue().getChildNodes();
        if (childNodes.isEmpty() && childNodeConvertors.isEmpty()) {
            return;
        }
        for (Node childNode : childNodes) {
            if (!(childNode.getClass().isAssignableFrom(memberClass))) {
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
