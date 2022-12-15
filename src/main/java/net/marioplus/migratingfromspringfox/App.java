package net.marioplus.migratingfromspringfox;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import net.marioplus.migratingfromspringfox.util.CompilationUnitUtils;
import net.marioplus.migratingfromspringfox.util.FileUtils;
import net.marioplus.migratingfromspringfox.visitor.AnnotationReplaceVisitor;
import net.marioplus.migratingfromspringfox.visitor.ImportReplaceVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        List<String> paths = Collections.singletonList(String.format("%s/src/main/java/", Paths.get(".").normalize().toAbsolutePath()));
        List<VoidVisitorAdapter<AtomicBoolean>> visitorAdapters = Arrays.asList(new ImportReplaceVisitor(), new AnnotationReplaceVisitor());
        List<File> files = FileUtils.loadFile(paths, FileUtils.FILE_FILTER_JAVA);

        for (File file : files) {
            System.out.printf("====%s====%n", file.getName());
            // migrate(file, visitorAdapters, cu -> System.out.println(CompilationUnitUtils.prettyPrint(cu)));
            migrate(file, visitorAdapters, cu -> refreshFile(file, cu));
            System.out.println();
        }
    }

    private static void migrate(File file, List<VoidVisitorAdapter<AtomicBoolean>> visitorAdapters, Consumer<CompilationUnit> changedConsumer) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);

        AtomicBoolean changed = new AtomicBoolean(false);
        for (VoidVisitorAdapter<AtomicBoolean> visitorAdapter : visitorAdapters) {
            visitorAdapter.visit(cu, changed);
        }

        if (changed.get()) {
            changedConsumer.accept(cu);
        }
    }

    private static void refreshFile(File file, CompilationUnit cu) {
        System.out.println(cu);
    }
}
