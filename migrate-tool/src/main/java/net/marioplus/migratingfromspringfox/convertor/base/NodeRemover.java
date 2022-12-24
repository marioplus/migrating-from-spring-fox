package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;

public class NodeRemover<T extends Node> implements IConvertor<T> {

    @Override
    public void convert(T t) {
        // t.getParentNode().ifPresent(p->p.removeForced());
        t.removeForced();
    }

}
