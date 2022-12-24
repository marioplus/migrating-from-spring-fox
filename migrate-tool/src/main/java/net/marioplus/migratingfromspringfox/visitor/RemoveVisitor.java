package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeRemover;
import net.marioplus.migratingfromspringfox.convertor.base.NodeSimpleNameFilterConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final List<NodeSimpleNameFilterConvertor<MemberValuePair, MemberValuePair>> MEMBER_VALUE_CONVERTORS = new ArrayList<>();

    static {
        MEMBER_VALUE_CONVERTORS.add(new NodeSimpleNameFilterConvertor<>("dataTypeClass", new NodeRemover<>()));
    }

    @Override
    public void visit(MemberValuePair pair, AtomicBoolean changed) {
        for (IFilterConvertor<MemberValuePair> convertor : MEMBER_VALUE_CONVERTORS) {
            if (convertor.filter(pair)) {
                changed.set(true);
                convertor.convert(pair);
            }
        }
    }
}
