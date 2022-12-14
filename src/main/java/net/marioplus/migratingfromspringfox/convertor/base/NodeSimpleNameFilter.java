package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

public class NodeSimpleNameFilter<T extends NodeWithSimpleName<S>, S extends Node> implements IFilter<T>, INodeNameProvider {

    private final String name;

    public NodeSimpleNameFilter(String name) {
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
