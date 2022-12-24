package net.marioplus.migratingfromspringfox.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileUtils {

    public static final Function<File, Boolean> FILE_FILTER_JAVA = file -> file.getName().endsWith(".java");

    public static List<File> loadFile(List<String> paths, Function<File, Boolean> filter) {
        return paths.stream()
                .map(path -> FileUtils.loadFile(path, filter))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<File> loadFile(String path, Function<File, Boolean> filter) {
        File file = new File(path);
        return loadFile(file, filter);
    }

    public static List<File> loadFile(File file, Function<File, Boolean> filter) {
        if (!file.exists()) {
            return Collections.emptyList();
        }
        // file
        if (file.isFile() || filter.apply(file)) {
            return Collections.singletonList(file);
        }
        // dir
        File[] childFiles = file.listFiles();
        if (file.isDirectory() && childFiles != null) {
            ArrayList<File> files = new ArrayList<>();
            for (File childFile : childFiles) {
                files.addAll(loadFile(childFile, filter));
            }
            return files;
        }

        return Collections.emptyList();
    }
}
