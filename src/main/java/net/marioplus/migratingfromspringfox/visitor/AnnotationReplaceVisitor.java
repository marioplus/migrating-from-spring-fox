package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final Map<String, String> normalAnnoNameMap = new HashMap<>();
    private static final Map<String, List<IMemberValuePairConvertor>> memberValuePairConvertorMap = new HashMap<>();
    private static final Map<String, String> singleMemberAnnoNameMap = new HashMap<>();

    static {
        normalAnnoNameMap.put("Api", "Tag");
        normalAnnoNameMap.put("ApiIgnore", "io.swagger.v3.oas.annotations.Hidden");
        normalAnnoNameMap.put("ApiImplicitParam", "Parameter");
        normalAnnoNameMap.put("ApiModel", "Schema");
        normalAnnoNameMap.put("ApiModelProperty", "Schema");
        normalAnnoNameMap.put("ApiParam", "Parameter");

        normalAnnoNameMap.put("ApiOperation", "Operation");
        memberValuePairConvertorMap.put("ApiOperation", Arrays.asList(
                IMemberValuePairNameConvertor.DEFAULT.apply("value", "summary"),
                IMemberValuePairNameConvertor.DEFAULT.apply("notes", "description")
        ));

        memberValuePairConvertorMap.put("ApiResponse", Arrays.asList(
                IMemberValuePairNameConvertor.DEFAULT.apply("code", "responseCode"),
                IMemberValuePairValueStringToIntConvertor.DEFAULT.apply("code"),
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
                    if (valuePairConvertor.match(memberValuePair.getNameAsString())) {
                        valuePairConvertor.convert(memberValuePair);
                        changed.set(true);
                    }
                }
            }
        }

    }

    @Override
    public void visit(SingleMemberAnnotationExpr expr, AtomicBoolean changed) {
        System.out.println("注解");
        System.out.println(expr);
    }
}
