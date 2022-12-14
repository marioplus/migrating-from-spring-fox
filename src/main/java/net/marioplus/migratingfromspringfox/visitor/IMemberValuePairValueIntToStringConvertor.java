package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.util.function.Function;

public abstract class IMemberValuePairValueIntToStringConvertor implements IMemberValuePairConvertor {

    public static final Function<String, IMemberValuePairValueIntToStringConvertor> DEFAULT = name -> new IMemberValuePairValueIntToStringConvertor() {
        @Override
        public boolean filter(MemberValuePair pair) {
            return pair.getNameAsString().equals(name);
        }
    };

    @Override
    public void convert(MemberValuePair pair) {
        Expression expr = pair.getValue();
        if (expr instanceof IntegerLiteralExpr) {
            IntegerLiteralExpr intExpr = (IntegerLiteralExpr) expr;
            StringLiteralExpr newExpr = new StringLiteralExpr(String.valueOf(intExpr.getValue()));
            pair.setValue(newExpr);
        }
    }
}
