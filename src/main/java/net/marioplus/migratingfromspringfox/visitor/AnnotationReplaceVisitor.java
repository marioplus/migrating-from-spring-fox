package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.marioplus.migratingfromspringfox.convertor.NormalAnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.SingleMemberAnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeSimpleNameFilterConvertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final List<NormalAnnotationExprFilterConvertor> NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS = new ArrayList<>();

    static {
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("Api", "Tag"));
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("ApiIgnore", "Hidden"));
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("ApiModel", "Schema"));
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("ApiParam", "Parameter"));
        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("ApiModelProperty", "Schema", Collections.singletonList(
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
        )));

        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("ApiOperation", "Operation", Arrays.asList(
                new NodeSimpleNameFilterConvertor<>("value", "summary"),
                new NodeSimpleNameFilterConvertor<>("notes", "description")
        )));

        NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new NormalAnnotationExprFilterConvertor("ApiOperation", Arrays.asList(
                new NodeSimpleNameFilterConvertor<>("code", "responseCode"),
                new IFilterConvertor<MemberValuePair>() {

                    @Override
                    public boolean filter(MemberValuePair pair) {
                        return pair.getNameAsString().equals("code");
                    }

                    @Override
                    public void convert(MemberValuePair pair) {
                        Expression expr = pair.getValue();
                        IntegerLiteralExpr intExpr = (IntegerLiteralExpr) expr;
                        StringLiteralExpr newExpr = new StringLiteralExpr(String.valueOf(intExpr.getValue()));
                        pair.setValue(newExpr);
                    }
                },
                new NodeSimpleNameFilterConvertor<>("message", "description")
        )));
    }

    private static final List<SingleMemberAnnotationExprFilterConvertor<?>> SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS = new ArrayList<>();

    static {
        SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new SingleMemberAnnotationExprFilterConvertor<>(NormalAnnotationExpr.class, "ApiImplicitParams", "Parameters", Collections.singletonList(
                new NormalAnnotationExprFilterConvertor("ApiImplicitParam", "Parameter")
        )));
        SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new SingleMemberAnnotationExprFilterConvertor<>(NormalAnnotationExpr.class, "ApiIgnore", "Hidden", Collections.singletonList(
                new NormalAnnotationExprFilterConvertor("ApiImplicitParam", "Parameter")
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
        for (NormalAnnotationExprFilterConvertor filterConvertor : NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS) {
            if (filterConvertor.filter(expr)) {
                changed.set(true);
                filterConvertor.convert(expr);
            }
        }
    }

    @Override
    public void visit(SingleMemberAnnotationExpr expr, AtomicBoolean changed) {
        System.out.printf("SingleMemberAnnotationExpr:\t%s%n", expr.getNameAsString());
        for (SingleMemberAnnotationExprFilterConvertor<?> filterConvertor : SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS) {
            if (filterConvertor.filter(expr)) {
                changed.set(true);
                filterConvertor.convert(expr);
            }
        }
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
