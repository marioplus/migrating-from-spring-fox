package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

public class NodeNameFilter<T extends NodeWithName<S>, S extends Node> implements IFilter<T>, INodeNameProvider {

    private final String name;

    public NodeNameFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean filter(T t) {
        return t.getNameAsString().equals(getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
