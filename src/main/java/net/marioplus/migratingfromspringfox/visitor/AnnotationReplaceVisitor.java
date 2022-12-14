package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.swagger.v3.oas.annotations.Hidden;
import net.marioplus.migratingfromspringfox.convertor.AnnotationExprFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeSimpleNameFilterConvertor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final Map<String, String> singleMemberAnnoNameMap = new HashMap<>();

    private static final List<IFilterConvertor<AnnotationExpr>> CONVERTORS = new ArrayList<>();

    static {
        CONVERTORS.add(new AnnotationExprFilterConvertor("Api", "Tag"));
        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiIgnore", "Hidden"));
        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiImplicitParam", "Parameter"));
        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiModel", "Schema"));
        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiParam", "Parameter"));
        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiModelProperty", "Schema", Collections.singletonList(
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

        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiOperation", "Operation", Arrays.asList(
                new NodeSimpleNameFilterConvertor<>("value", "summary"),
                new NodeSimpleNameFilterConvertor<>("notes", "description")
        )));

        CONVERTORS.add(new AnnotationExprFilterConvertor("ApiOperation", Arrays.asList(
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
        singleMemberAnnoNameMap.put("ApiImplicitParams", "Parameters");
    }

    @Override

    @Hidden
    public void visit(NormalAnnotationExpr expr, AtomicBoolean changed) {
        for (IFilterConvertor<AnnotationExpr> convertor : CONVERTORS) {
            if (convertor.filter(expr)) {
                changed.set(true);
                convertor.convert(expr);
            }
        }
    }

    @Override
    public void visit(SingleMemberAnnotationExpr expr, AtomicBoolean changed) {
        String exprName = expr.getName().toString();
        String newName = singleMemberAnnoNameMap.get(exprName);
        if (newName != null) {
            expr.setName(newName);
            changed.set(true);
        }
    }
}
