package org.dev.rewrite.petstore.spring;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.openrewrite.ExecutionContext;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.ClassDeclaration;

public class CreateAppLoggerClass extends ScanningRecipe<CreateAppLoggerClass.Scanned> {
    
    private static final String APP_CLASS_NAME = "App";
    private static final String APP_LOGGER_CLASS_NAME = "AppLogger";
    private static final String APP_LOGGER_CLASS_SOURCE_PATH = "%s/" + APP_LOGGER_CLASS_NAME + ".java";
    private static final String CLASS_TEMPLATE = "package %s;\n\n%s";
    
    @Override
    public String getDisplayName() {
        return "Create an AppLogger class when an App class exists";
    }
    
    @Override
    public String getDescription() {
        return "Creates a new AppLogger class with a log method that prints 'Hello World' " +
                "when an App class is found in the codebase.";
    }

    @Override
    public Scanned getInitialValue(ExecutionContext ctx) {
        Scanned scanned = new Scanned();
        scanned.sourcePath = null;
        scanned.packageName = null;
        scanned.className = null;
        return scanned;
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Scanned acc) {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
                // Check if any class in this compilation unit is named App
                boolean containsAppClass = cu.getClasses().stream()
                    .anyMatch(clazz -> APP_CLASS_NAME.equals(clazz.getSimpleName()));
                
                if (containsAppClass) {
                    acc.sourcePath = cu.getSourcePath().subpath(0, cu.getSourcePath().getNameCount() - 1);
                    acc.packageName = cu.getPackageDeclaration().getPackageName();
                    acc.className = APP_LOGGER_CLASS_NAME;
                }
                
                return super.visitCompilationUnit(cu, ctx);
            }
        };
    }
    
    @Override
    public Collection<? extends SourceFile> generate(Scanned acc, Collection<SourceFile> generatedInThisCycle, ExecutionContext ctx) {
        if (acc.className != null) {
            Path path = Paths.get(String.format(APP_LOGGER_CLASS_SOURCE_PATH, acc.sourcePath));
            SourceFile sourceFile = createAppLoggerClass(acc, ctx).findFirst().get().withSourcePath(path);
            return Collections.singleton(sourceFile);
        }
        return generate(acc, ctx);
    }
    
    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Scanned acc) {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
                Optional<ClassDeclaration> classDeclaration = cu.getClasses().stream()
                    .filter(clazz -> acc.className.equals(clazz.getSimpleName())).findAny();
                if (classDeclaration.isPresent()) {
                    System.out.println("class created: " + classDeclaration.get().getSimpleName());
                }
                return cu;
            }
        };
    }
    
    protected Stream<SourceFile> createAppLoggerClass(Scanned acc, ExecutionContext ctx) {
        final String classSource = String.format(CLASS_TEMPLATE, acc.packageName, getClassSource());
        return JavaParser.fromJavaVersion().build()
                .parse(ctx, classSource);
    }
    
    protected String getClassSource() {
        return "public class " + APP_LOGGER_CLASS_NAME + " {\n" +
                "    public void log() {\n" +
                "        System.out.println(\"Hello World\");\n" +
                "    }\n" +
                "}";
    }
    
    public static class Scanned {
        Path sourcePath;
        String packageName;
        String className;
    }
}
