package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.MemberValuePair;

import java.util.function.BiFunction;

public abstract class IMemberValuePairNameConvertor implements IMemberValuePairConvertor {

    public static final BiFunction<String, String, IMemberValuePairNameConvertor> DEFAULT = (name, newName) -> new IMemberValuePairNameConvertor() {

        @Override
        public boolean match(MemberValuePair pair) {
            return pair.getNameAsString().equals(name);
        }

        @Override
        public String convert(String n) {
            return newName;
        }
    };

    @Override
    public void convert(MemberValuePair pair) {
        pair.setName(pair.getName());
    }

    public abstract String convert(String name);

}
