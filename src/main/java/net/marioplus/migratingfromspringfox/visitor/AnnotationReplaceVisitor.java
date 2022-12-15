package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.swagger.v3.oas.annotations.Hidden;
import net.marioplus.migratingfromspringfox.convertor.NormalAnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.SingleMemberAnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeSimpleNameFilterConvertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final List<NormalAnnotationExprFilterConvertor> NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS = new ArrayList<>();

    private static final List<SingleMemberAnnotationExprFilterConvertor> SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS = new ArrayList<>();

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
                        String pack = "io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY";
                        String[] split = pack.split("\\.");
                        FieldAccessExpr expr = new FieldAccessExpr(new NameExpr(split[0]), split[1]);
                        for (int i = 2; i < split.length; i++) {
                            expr = new FieldAccessExpr(expr, split[i]);
                        }
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
                        if (expr instanceof IntegerLiteralExpr) {
                            IntegerLiteralExpr intExpr = (IntegerLiteralExpr) expr;
                            StringLiteralExpr newExpr = new StringLiteralExpr(String.valueOf(intExpr.getValue()));
                            pair.setValue(newExpr);
                        }
                    }
                },
                new NodeSimpleNameFilterConvertor<>("message", "description")
        )));
    }

    static {
        SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS.add(new SingleMemberAnnotationExprFilterConvertor("ApiImplicitParams", "Parameters", Collections.singletonList(
                new NormalAnnotationExprFilterConvertor("ApiImplicitParam", "Parameter")
        )));
    }

    @Override

    @Hidden
    public void visit(NormalAnnotationExpr expr, AtomicBoolean changed) {
        for (NormalAnnotationExprFilterConvertor filterConvertor : NORMAL_ANNOTATION_EXPR_FILTER_CONVERTORS) {
            if (filterConvertor.filter(expr)) {
                changed.set(true);
                filterConvertor.convert(expr);
            }
        }
    }

    @Override
    public void visit(SingleMemberAnnotationExpr expr, AtomicBoolean changed) {
        for (SingleMemberAnnotationExprFilterConvertor filterConvertor : SINGLE_MEMBER_ANNOTATION_EXPR_FILTER_CONVERTORS) {
            if (filterConvertor.filter(expr)) {
                changed.set(true);
                filterConvertor.convert(expr);
            }
        }
    }
}
