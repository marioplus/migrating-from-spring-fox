package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.util.function.Function;

public abstract class IMemberValuePairValueStringToIntConvertor implements IMemberValuePairConvertor {

    public static final Function<String, IMemberValuePairValueStringToIntConvertor> DEFAULT = name -> new IMemberValuePairValueStringToIntConvertor() {
        @Override
        public boolean filter(MemberValuePair pair) {
            return pair.getNameAsString().equals(name);
        }
    };

    @Override
    public void convert(MemberValuePair pair) {
        Expression expr = pair.getValue();
        if (expr instanceof StringLiteralExpr) {
            StringLiteralExpr slExpr = (StringLiteralExpr) expr;
            IntegerLiteralExpr newExpr = new IntegerLiteralExpr(slExpr.getValue().replaceAll("^\"(.+)\"$", "$1"));
            pair.setValue(newExpr);
        }
    }
}
