package net.marioplus.migratingfromspringfox.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

public class CompilationUnitUtils {

    private static final DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

    static {
        PrinterConfiguration config = new DefaultPrinterConfiguration()
                .addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.PRINT_COMMENTS, true))
                .addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.PRINT_JAVADOC, true));
        PRETTY_PRINTER.setConfiguration(config);
    }

    public static String prettyPrint(CompilationUnit cu) {
        return PRETTY_PRINTER.print(cu);
    }
}
