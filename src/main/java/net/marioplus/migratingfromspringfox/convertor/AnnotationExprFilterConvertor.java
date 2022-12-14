package net.marioplus.migratingfromspringfox.convertor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilter;

import java.util.Collections;
import java.util.List;

public class AnnotationExprFilterConvertor implements IFilterConvertor<AnnotationExpr> {

    private final NodeNameFilter<NodeWithName<AnnotationExpr>, AnnotationExpr> filter;

    private final NodeNameConvertor<NodeWithName<AnnotationExpr>, AnnotationExpr> nameConvertor;

    private final List<IFilterConvertor<MemberValuePair>> childNodeConvertors;

    public AnnotationExprFilterConvertor(String name, String nameName, List<IFilterConvertor<MemberValuePair>> childNodeConvertors) {
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = new NodeNameConvertor<>(nameName);
        this.childNodeConvertors = childNodeConvertors;
    }

    public AnnotationExprFilterConvertor(String name, List<IFilterConvertor<MemberValuePair>> childNodeConvertors) {
        this(name, null, childNodeConvertors);
    }

    public AnnotationExprFilterConvertor(String name, String nameName) {
        this(name, nameName, Collections.emptyList());
    }

    @Override
    public boolean filter(AnnotationExpr expr) {
        return filter.filter(expr);
    }

    @Override
    public void convert(AnnotationExpr expr) {
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
            if (!(childNode instanceof MemberValuePair)) {
                continue;
            }
            MemberValuePair pair = (MemberValuePair) childNode;
            for (IFilterConvertor<MemberValuePair> childNodeConvertor : childNodeConvertors) {
                if (childNodeConvertor.filter(pair)) {
                    childNodeConvertor.convert(pair);
                }
            }
        }
    }
}
