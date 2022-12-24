package net.marioplus.migratingfromspringfox.convertor.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithName;


public class NodeNameFilterConvertor<T extends NodeWithName<S>, S extends Node> extends AbstractNodeNameFilterConvertor<T> {

    public NodeNameFilterConvertor(String name, String newName) {
        filter = new NodeNameFilter<>(name);
        convertor = new NodeNameConvertor<>(newName);
    }

}
