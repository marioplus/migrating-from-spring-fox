package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MemberValuePair;

public class NodeRemover<T extends Node> implements IConvertor<T> {

    @Override
    public void convert(T t) {
        t.remove();
    }

}
