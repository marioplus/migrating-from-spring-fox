package net.marioplus.migratingfromspringfox.visitor;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImportReplaceVisitor extends VoidVisitorAdapter<AtomicBoolean> {

    private static final Map<String, String> importMap = new HashMap<>();

    static {
        // api
        importMap.put("io.swagger.annotations.Api", "io.swagger.v3.oas.annotations.Tag");
        importMap.put("io.swagger.annotations.ApiImplicitParam", "io.swagger.v3.oas.annotations.Parameter");
        importMap.put("io.swagger.annotations.ApiImplicitParams", "io.swagger.v3.oas.annotations.Parameters");
        importMap.put("io.swagger.annotations.ApiOperation", "io.swagger.v3.oas.annotations.Operation");

        // model
        importMap.put("io.swagger.annotations.ApiModel", "io.swagger.v3.oas.annotations.Schema");
        importMap.put("io.swagger.annotations.ApiModelProperty", "io.swagger.v3.oas.annotations.Schema");
    }

    @Override
    public void visit(ImportDeclaration n, AtomicBoolean changed) {
        String newName = importMap.get(n.getName().toString());
        if (newName != null) {
            n.setName(newName);
            changed.set(true);
        }
    }
}
