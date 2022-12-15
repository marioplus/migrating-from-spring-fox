package net.marioplus.migratingfromspringfox.convertor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilter;

import java.util.Collections;
import java.util.List;

public class SingleMemberAnnotationExprFilterConvertor implements IFilterConvertor<SingleMemberAnnotationExpr> {

    private final NodeNameFilter<SingleMemberAnnotationExpr, AnnotationExpr> filter;

    private final NodeNameConvertor<SingleMemberAnnotationExpr, AnnotationExpr> nameConvertor;

    private final List<NormalAnnotationExprFilterConvertor> childNodeConvertors;

    public SingleMemberAnnotationExprFilterConvertor(String name, String nameName, List<NormalAnnotationExprFilterConvertor> childNodeConvertors) {
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = new NodeNameConvertor<>(nameName);
        this.childNodeConvertors = childNodeConvertors;
    }

    public SingleMemberAnnotationExprFilterConvertor(String name, List<NormalAnnotationExprFilterConvertor> childNodeConvertors) {
        this(name, null, childNodeConvertors);
    }

    public SingleMemberAnnotationExprFilterConvertor(String name, String nameName) {
        this(name, nameName, Collections.emptyList());
    }

    @Override
    public boolean filter(SingleMemberAnnotationExpr expr) {
        return filter.filter(expr);
    }

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
            if (!(childNode instanceof NormalAnnotationExpr)) {
                continue;
            }
            NormalAnnotationExpr childExpr = (NormalAnnotationExpr) childNode;
            for (NormalAnnotationExprFilterConvertor childNodeConvertor : childNodeConvertors) {
                if (childNodeConvertor.filter(childExpr)) {
                    childNodeConvertor.convert(childExpr);
                }
            }
        }
    }

}
