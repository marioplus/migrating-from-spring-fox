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

    private static final AnnotationExprConvertors<NormalAnnotationExpr> NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS = new AnnotationExprConvertors<>();

    static {
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "Api", "Tag"))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiIgnore", Collections.singletonList(
                        (IAlwaysConvertor<MemberValuePair>) expr -> expr.getParentNode().ifPresent(p -> {
                            p.replace(new NormalAnnotationExpr(new Name("Hidden"), new NodeList<>()));
                        })
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiModel", "Schema"))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiParam", "Parameter"))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiModelProperty", "Schema", Collections.singletonList(
                        new IFilterConvertor<MemberValuePair>() {
                            @Override
                            public boolean filter(MemberValuePair pair) {
                                if (!pair.getNameAsString().equals("hidden")) {
                                    return false;
                                }
                                return ((BooleanLiteralExpr) pair.getValue()).getValue();
                            }

                            @Override
                            public void convert(MemberValuePair pair) {
                                FieldAccessExpr expr = new FieldAccessExpr(new NameExpr("io.swagger.v3.oas.annotations.media.Schema.AccessMode"), "READ_ONLY");
                                pair.setValue(expr);
                            }
                        }
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiOperation", "Operation", Arrays.asList(
                        new NodeSimpleNameFilterConvertor<>("value", "summary"),
                        new NodeSimpleNameFilterConvertor<>("notes", "description")
                )))
                .add(new AnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiOperation", Arrays.asList(
                        new NodeSimpleNameFilterConvertor<>("code", "responseCode"),
                        new NodeSimpleNameFilterConvertor<>("code", pair -> {
                            Expression expr = pair.getValue();
                            IntegerLiteralExpr intExpr = (IntegerLiteralExpr) expr;
                            StringLiteralExpr newExpr = new StringLiteralExpr(String.valueOf(intExpr.getValue()));
                            pair.setValue(newExpr);
                        }),
                        new NodeSimpleNameFilterConvertor<>("message", "description")
                )));
    }

    private static final AnnotationExprConvertors<SingleMemberAnnotationExpr> SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS = new AnnotationExprConvertors<>();

    static {
        SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS
                .add(new AnnotationExprFilterConvertor<>(NormalAnnotationExpr.class, "ApiImplicitParams", "Parameters", Collections.singletonList(
                        new NormalAnnotationExprFilterConvertor<>(MemberValuePair.class, "ApiImplicitParam", "Parameter")
                )))
                .add(new AnnotationExprFilterConvertor<>(StringLiteralExpr.class, "ApiIgnore", Collections.singletonList(
                        (IAlwaysConvertor<StringLiteralExpr>) expr -> expr.getParentNode().ifPresent(p -> p.replace(new NormalAnnotationExpr(new Name("Hidden"), new NodeList<>())))
                )));
    }

    private static final List<NodeNameFilterConvertor<NodeWithName<AnnotationExpr>, AnnotationExpr>> MARKER_ANNOTATION_EXPR_FILTER_CONVERTOR = new ArrayList<>();

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
