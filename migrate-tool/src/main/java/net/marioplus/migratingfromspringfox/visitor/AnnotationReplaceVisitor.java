package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.marioplus.migratingfromspringfox.convertor.AnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.NormalAnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    // @xxx(xxx=xxx)
    private static final AnnotationExprConvertors<NormalAnnotationExpr> NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS = new AnnotationExprConvertors<>();
    // @xxx(@xxx(xxx=xxx))
    private static final AnnotationExprConvertors<SingleMemberAnnotationExpr> SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS = new AnnotationExprConvertors<>();
    // @xxx
    private static final List<NodeNameFilterConvertor<NodeWithName<AnnotationExpr>, AnnotationExpr>> MARKER_ANNOTATION_EXPR_FILTER_CONVERTOR = new ArrayList<>();

    static {
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "Api", "Tag", Collections.singletonList(
                        new NodeSimpleNameFilterConvertor<>("tags", "name")
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiIgnore", Collections.singletonList(
                        (IAlwaysConvertor<MemberValuePair>) expr -> expr.getParentNode().ifPresent(p -> {
                            p.replace(new NormalAnnotationExpr(new Name("Hidden"), new NodeList<>()));
                        })
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiModel", "Schema", Collections.singletonList(
                        new NodeSimpleNameFilterConvertor<>("value", "name")
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiParam", "Parameter"))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiModelProperty", "Schema", Arrays.asList(
                        new NodeSimpleNameFilterConvertor<>("value", "description"),
                        new NodeSimpleNameFilterConvertor<>("allowEmptyValue", "nullable")
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiOperation", "Operation", Arrays.asList(
                        new NodeSimpleNameFilterConvertor<>("value", "summary"),
                        new NodeSimpleNameFilterConvertor<>("notes", "description")
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiResponse", Arrays.asList(
                        new NodeSimpleNameFilterConvertor<>("code", "responseCode"),
                        new NodeSimpleNameFilterConvertor<>("responseCode", new ExpressionConvertor<>(MemberValuePair::getValue, MemberValuePair::setValue, ExpressionConvertor.INT_2_STRING)),
                        new NodeSimpleNameFilterConvertor<>("message", "description")
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiImplicitParam", "Parameter", Collections.singletonList(
                        new NodeSimpleNameFilterConvertor<>("value", "description")
                )))
        ;
    }

    static {
        SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS
                .add(new AnnotationExprFilterConvertor<>(NormalAnnotationExpr.class, "ApiImplicitParams", "Parameters", Collections.singletonList(
                        new NormalAnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiImplicitParam", "Parameter", Collections.singletonList(
                                new NodeSimpleNameFilterConvertor<>("value", "description")
                        ))
                )))
                .add(new AnnotationExprFilterConvertor<>(StringLiteralExpr.class, "ApiIgnore", Collections.singletonList(
                        (IAlwaysConvertor<StringLiteralExpr>) expr -> expr.getParentNode().ifPresent(p -> p.replace(new NormalAnnotationExpr(new Name("Hidden"), new NodeList<>())))
                )));
    }

    static {
        MARKER_ANNOTATION_EXPR_FILTER_CONVERTOR.add(
                new NodeNameFilterConvertor<>("ApiIgnore", "Hidden")
        );
    }

    @Override
    public void visit(NormalAnnotationExpr expr, AtomicBoolean changed) {
        System.out.printf("NormalAnnotationExpr:\t%s%n", expr.getNameAsString());
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.convert(expr, unused -> changed.set(true));
    }

    @Override
    public void visit(SingleMemberAnnotationExpr expr, AtomicBoolean changed) {
        System.out.printf("SingleMemberAnnotationExpr:\t%s%n", expr.getNameAsString());
        SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS.convert(expr, unused -> changed.set(true));
    }

    @Override
    public void visit(MarkerAnnotationExpr expr, AtomicBoolean changed) {
        System.out.printf("MarkerAnnotationExpr:\t%s%n", expr.getNameAsString());
        for (NodeNameFilterConvertor<NodeWithName<AnnotationExpr>, AnnotationExpr> filterConvertor : MARKER_ANNOTATION_EXPR_FILTER_CONVERTOR) {
            if (filterConvertor.filter(expr)) {
                changed.set(true);
                filterConvertor.convert(expr);
            }
        }
    }
}
