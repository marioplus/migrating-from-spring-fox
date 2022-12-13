package net.marioplus.migratingfromspringfox;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    private static final Map<String, String> importMap = new HashMap<>();

    static {
        importMap.put("io.swagger.annotations.Api", "io.swagger.v3.oas.annotations.Tag");
        importMap.put("io.swagger.annotations.ApiImplicitParam", "io.swagger.v3.oas.annotations.Parameter");
        importMap.put("io.swagger.annotations.ApiImplicitParams", "io.swagger.v3.oas.annotations.Parameters");
        importMap.put("io.swagger.annotations.ApiOperation", "io.swagger.v3.oas.annotations.Operation");

        importMap.put("io.swagger.annotations.ApiModel", "io.swagger.v3.oas.annotations.Schema");
        importMap.put("io.swagger.annotations.ApiModelProperty", "io.swagger.v3.oas.annotations.Schema");
    }

    public static void main(String[] args) {
        List<String> paths = Arrays.asList(
                "G:/Code/Demo/migrating-from-spring-fox/src/main/java/net/marioplus/migratingfromspringfox"
        );
        List<File> files = loadFile(paths);
    }

    private static List<File> loadFile(List<String> paths) {
        return paths.stream()
                .map(App::loadFile)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<File> loadFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Collections.emptyList();
        }
        // file
        if (file.isFile() || path.equalsIgnoreCase(".java")) {
            return Collections.singletonList(file);
        }
        // dir
        String[] subPaths = file.list();
        if (file.isDirectory() && subPaths != null) {
            ArrayList<File> files = new ArrayList<>();
            for (String subPath : subPaths) {
                files.addAll(loadFile(subPath));
            }
            return files;
        }

        return Collections.emptyList();
    }

    private static void migrate(File file) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);
    }

    private static class SwaggerVisitor extends VoidVisitorAdapter<Void> {


        @Override
        public void visit(JavadocComment n, Void arg) {
            super.visit(n, arg);
        }
    }
}
