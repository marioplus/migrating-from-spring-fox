package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.marioplus.migratingfromspringfox.convertor.base.IFilterConvertor;
import net.marioplus.migratingfromspringfox.convertor.base.NodeNameFilterConvertor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImportReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final Map<String, String> importMap = new HashMap<>();
    private static final List<IFilterConvertor<ImportDeclaration>> CONVERTORS = new ArrayList<>();

    static {

        // (带 * 的去掉*)
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations", "io.swagger.v3.oas.annotations"));
        // api
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.Api", "io.swagger.v3.oas.annotations.Tag"));
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.ApiIgnore", "io.swagger.v3.oas.annotations.Hidden"));
        CONVERTORS.add(new NodeNameFilterConvertor<>("springfox.documentation.annotations.ApiIgnore", "io.swagger.v3.oas.annotations.Hidden"));
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.ApiImplicitParam", "io.swagger.v3.oas.annotations.Parameter"));
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.ApiImplicitParams", "io.swagger.v3.oas.annotations.Parameters"));
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.ApiOperation", "io.swagger.v3.oas.annotations.Operation"));

        // model
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.ApiModel", "io.swagger.v3.oas.annotations.Schema"));
        CONVERTORS.add(new NodeNameFilterConvertor<>("io.swagger.annotations.ApiModelProperty", "io.swagger.v3.oas.annotations.Schema"));
    }

    @Override
    public void visit(ImportDeclaration importDeclaration, AtomicBoolean changed) {
        for (IFilterConvertor<ImportDeclaration> convertor : CONVERTORS) {
            if (convertor.filter(importDeclaration)) {
                changed.set(true);
                convertor.convert(importDeclaration);
            }
        }
    }
}
