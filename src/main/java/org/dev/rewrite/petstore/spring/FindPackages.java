package org.dev.rewrite.petstore.spring;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openrewrite.ExecutionContext;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;

import org.dev.rewrite.petstore.spring.table.PackageName;

import static java.util.Collections.emptyList;

@Value
@EqualsAndHashCode(callSuper = false)
public class FindPackages extends ScanningRecipe<FindPackages.Accumulator> {

    transient PackageName packageName = new PackageName(this);
    
    @Override
    public String getDisplayName() {
        return "Find packages";
    }

    @Override
    public String getDescription() {
        return "Finds all packages.";
    }

    @Override
    public Accumulator getInitialValue(ExecutionContext ctx) {
        return new Accumulator();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Accumulator acc) {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext ctx) {
                acc.getPackageNames().add(cu.getPackageDeclaration().getPackageName());
                return cu;
            }
        };
    }
    
    @Override
    public Collection<SourceFile> generate(Accumulator acc, ExecutionContext ctx) {
        acc.packageNames.stream().forEach(
                x -> packageName.insertRow(ctx, new PackageName.Row(x)));
        return emptyList();
    }
    
    @Value
    public static class Accumulator {
        Set<String> packageNames = new LinkedHashSet<>();
    }
}
