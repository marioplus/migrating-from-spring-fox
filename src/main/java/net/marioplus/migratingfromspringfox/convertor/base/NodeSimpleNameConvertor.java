package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

public class NodeSimpleNameConvertor<T extends NodeWithSimpleName<S>, S extends Node> implements IConvertor<T>, INodeNameProvider {

    private final String name;

    public NodeSimpleNameConvertor(String name) {
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
