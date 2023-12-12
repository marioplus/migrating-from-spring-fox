package net.marioplus.migratingfromspringfox.convertor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import net.marioplus.migratingfromspringfox.convertor.base.IConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilter;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AnnotationExprFilterConvertor<T extends AnnotationExpr, C> implements IFilterConvertor<T> {

    public static BiFunction<String, String, IConvertor<SingleMemberAnnotationExpr>> SINGLE_STRING_MEMBER_CONVERTOR_FN =
            newSingleMemberConvertor(Expression::isStringLiteralExpr, Expression::asStringLiteralExpr, StringLiteralExpr::new);

    public static BiFunction<String, String, IConvertor<SingleMemberAnnotationExpr>> SINGLE_INTEGER_MEMBER_CONVERTOR =
            newSingleMemberConvertor(Expression::isIntegerLiteralExpr, Expression::asIntegerLiteralExpr, IntegerLiteralExpr::new);

    private static BiFunction<String, String, IConvertor<SingleMemberAnnotationExpr>> newSingleMemberConvertor(Function<Expression, Boolean> isExprFn, Function<Expression, LiteralStringValueExpr> asExprFn, Function<String, Expression> newMemberValFn) {
        return new BiFunction<String, String, IConvertor<SingleMemberAnnotationExpr>>() {
            @Override
            public IConvertor<SingleMemberAnnotationExpr> apply(String newName, String newMemberName) {
                return new IConvertor<SingleMemberAnnotationExpr>() {
                    @Override
                    public void convert(SingleMemberAnnotationExpr expr) {
                        Expression member = expr.getMemberValue();
                        if (!isExprFn.apply(member)) {
                            return;
                        }

                        String value = asExprFn.apply(member).getValue();
                        NodeList<MemberValuePair> nodes = new NodeList<>();
                        Expression apply = newMemberValFn.apply(value);
                        new MemberValuePair(newMemberName, apply);
                        nodes.add(new MemberValuePair(newMemberName, apply));
                        NormalAnnotationExpr schema = new NormalAnnotationExpr(new Name(newName), nodes);
                        expr.replace(schema);

                    }
                };
            }
        };
    }

    private final Class<C> childNodeClass;

    private final NodeNameFilter<T, AnnotationExpr> filter;

    private final NodeNameConvertor<T, AnnotationExpr> nameConvertor;

    private final List<IFilterConvertor<C>> childNodeConvertors;

    private final IConvertor<T> convertor;

    public AnnotationExprFilterConvertor(Class<C> childNodeClass, String name, String newName, List<IFilterConvertor<C>> childNodeConvertors) {
        this.childNodeClass = childNodeClass;
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = new NodeNameConvertor<>(newName);
        this.childNodeConvertors = childNodeConvertors;
        this.convertor = null;
    }

    public AnnotationExprFilterConvertor(Class<C> childNodeClass, String name, List<IFilterConvertor<C>> childNodeConvertors) {
        this(childNodeClass, name, null, childNodeConvertors);
    }

    public AnnotationExprFilterConvertor(Class<C> childNodeClass, String name, String newName) {
        this(childNodeClass, name, newName, Collections.emptyList());
    }

    public AnnotationExprFilterConvertor(String name, String newName, String memberName, BiFunction<String, String, IConvertor<SingleMemberAnnotationExpr>> convertorFn) {
        this.childNodeClass = null;
        this.filter = new NodeNameFilter<>(name);
        this.nameConvertor = null;
        this.childNodeConvertors = null;
        this.convertor = (IConvertor<T>) convertorFn.apply(newName, memberName);
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

        if (convertor != null) {
            convertor.convert(expr);
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
