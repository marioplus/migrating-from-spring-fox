package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

public class NodeNameConvertor<T extends NodeWithName<S>, S extends Node> implements IConvertor<T>, INodeNameProvider {

    private final String name;

    public NodeNameConvertor(String name) {
        this.name = name;
    }

    @Override
    public void convert(T t) {
        t.setName(getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
