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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        List<String> paths = Arrays.asList(
                "G:/Code/Demo/migrating-from-spring-fox/src/main/java/net/marioplus/migratingfromspringfox"
        );
        List<File> files = FileUtils.loadFile(paths, FileUtils.FILE_FILTER_JAVA);
        for (File file : files) {
            System.out.printf("====%s====%n", file.getName());
            migrate(file);
        }
    }

    private static void migrate(File file) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);
        List<VoidVisitorAdapter<AtomicBoolean>> visitorAdapters = Arrays.asList(
                new AnnotationReplaceVisitor(),
                new ImportReplaceVisitor()
        );
        AtomicBoolean changed = new AtomicBoolean(false);
        for (VoidVisitorAdapter<AtomicBoolean> visitorAdapter : visitorAdapters) {
            visitorAdapter.visit(cu, changed);
        }

        if (changed.get()) {
            System.out.println(CompilationUnitUtils.prettyPrint(cu));
        }
    }
}
