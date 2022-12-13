package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final Map<String, String> normalAnnoNameMap = new HashMap<>();
    private static final Map<String, List<IMemberValuePairConvertor>> memberValuePairConvertorMap = new HashMap<>();
    private static final Map<String, String> singleMemberAnnoNameMap = new HashMap<>();

    static {
        normalAnnoNameMap.put("Api", "Tag");
        normalAnnoNameMap.put("ApiIgnore", "Hidden");
        normalAnnoNameMap.put("ApiImplicitParam", "Parameter");
        normalAnnoNameMap.put("ApiModel", "Schema");
        normalAnnoNameMap.put("ApiParam", "Parameter");

        normalAnnoNameMap.put("ApiModelProperty", "Schema");
        memberValuePairConvertorMap.put("ApiModelProperty", Collections.singletonList(
                new IMemberValuePairConvertor() {

                    @Override
                    public boolean match(MemberValuePair pair) {
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
        ));

        normalAnnoNameMap.put("ApiOperation", "Operation");
        memberValuePairConvertorMap.put("ApiOperation", Arrays.asList(
                IMemberValuePairNameConvertor.DEFAULT.apply("value", "summary"),
                IMemberValuePairNameConvertor.DEFAULT.apply("notes", "description")
        ));

        memberValuePairConvertorMap.put("ApiResponse", Arrays.asList(
                IMemberValuePairNameConvertor.DEFAULT.apply("code", "responseCode"),
                IMemberValuePairValueIntToStringConvertor.DEFAULT.apply("code"),
                IMemberValuePairNameConvertor.DEFAULT.apply("message", "description")
        ));
    }

    static {
        singleMemberAnnoNameMap.put("ApiImplicitParams", "Parameters");
    }

    @Override

    @Hidden
    public void visit(NormalAnnotationExpr expr, AtomicBoolean changed) {
        String exprName = expr.getName().toString();
        String newName = normalAnnoNameMap.get(exprName);
        if (newName != null) {
            expr.setName(newName);
            changed.set(true);
        }

        List<IMemberValuePairConvertor> valuePairConvertors = memberValuePairConvertorMap.get(exprName);
        if (valuePairConvertors == null) {
            return;
        }

        for (Node node : expr.getChildNodes()) {
            if (node instanceof MemberValuePair) {
                MemberValuePair memberValuePair = (MemberValuePair) node;
                for (IMemberValuePairConvertor valuePairConvertor : valuePairConvertors) {
                    if (valuePairConvertor.match(memberValuePair)) {
                        valuePairConvertor.convert(memberValuePair);
                        changed.set(true);
                    }
                }
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
