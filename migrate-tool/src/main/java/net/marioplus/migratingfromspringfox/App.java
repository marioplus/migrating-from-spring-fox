package net.marioplus.migratingfromspringfox;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.SneakyThrows;
import net.marioplus.migratingfromspringfox.util.FileUtils;
import net.marioplus.migratingfromspringfox.visitor.AnnotationReplaceVisitor;
import net.marioplus.migratingfromspringfox.visitor.ImportReplaceVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class App {

    private static final JavaParser javaParser;

    static {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLexicalPreservationEnabled(true);
        javaParser = new JavaParser(parserConfiguration);
    }

    public static void main(String[] args) throws FileNotFoundException {
        List<String> paths = Collections.singletonList(String.format("%s/spring-fox-demo/src/main/java/", Paths.get(".").normalize().toAbsolutePath()));
        List<VoidVisitorAdapter<AtomicBoolean>> visitorAdapters = Arrays.asList(
                new ImportReplaceVisitor(),
                new AnnotationReplaceVisitor()
        );
        List<File> files = FileUtils.loadFile(paths, FileUtils.FILE_FILTER_JAVA);

        for (File file : files) {
            System.out.printf("转换文件: %sn", file.getName());
            // migrate(file, visitorAdapters, cu -> System.out.println(CompilationUnitUtils.prettyPrint(cu)));
            migrate(file, visitorAdapters, cu -> refreshFile(file, cu));
            System.out.println();
        }
    }

    private static void migrate(File file, List<VoidVisitorAdapter<AtomicBoolean>> visitorAdapters, Consumer<CompilationUnit> changedConsumer) throws FileNotFoundException {
        javaParser.parse(file).getResult().ifPresent(cu -> {
            AtomicBoolean changed = new AtomicBoolean(false);
            for (VoidVisitorAdapter<AtomicBoolean> visitorAdapter : visitorAdapters) {
                visitorAdapter.visit(cu, changed);
            }

            cu.walk(MemberValuePair.class, pair -> {
                if (pair.getNameAsString().equals("dataTypeClass")) {
                    pair.remove();
                }
            });


            if (file.getName().endsWith("Rest.java")) {
                cu.addImport("io.swagger.v3.oas.annotations.tags.Tag");
                cu.addImport("io.swagger.v3.oas.annotations.responses.ApiResponse");
            }

            if (changed.get()) {
                changedConsumer.accept(cu);
            }
        });
    }

    @SneakyThrows
    private static void refreshFile(File file, CompilationUnit cu) {
        System.out.printf("文件被转换：%s%n", file.getName());
        Files.write(file.toPath(), cu.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
