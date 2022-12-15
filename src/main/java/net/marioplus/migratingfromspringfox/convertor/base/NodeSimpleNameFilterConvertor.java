package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

public class NodeSimpleNameFilterConvertor<T extends NodeWithSimpleName<S>, S extends Node> extends AbstractNodeNameFilterConvertor<T> {

    public NodeSimpleNameFilterConvertor(String name, String newName) {
        filter = new NodeSimpleNameFilter<>(name);
        convertor = new NodeSimpleNameConvertor<>(newName);
    }

    public NodeSimpleNameFilterConvertor(String name, IConvertor<T> convertor) {
        filter = new NodeSimpleNameFilter<>(name);
        super.convertor = convertor;
    }
}
