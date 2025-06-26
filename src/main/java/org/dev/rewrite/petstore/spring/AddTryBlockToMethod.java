package org.dev.rewrite.petstore.spring;

import java.util.ArrayList;
import java.util.List;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Statement;

/**
 * OpenRewrite recipe that adds a try-catch-finally block around existing statements in a Java method.
 * This is useful for adding exception handling to methods that don't currently have it.
 */
public class AddTryBlockToMethod extends Recipe {

    private static final String METHOD_NAME = "processData";
    
    @Override
    public String getDisplayName() {
        return "Add try-catch-finally block to method";
    }

    @Override
    public String getDescription() {
        return "Wraps existing statements in a method with a try-catch-finally block using JavaTemplate.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            
            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext context) {
                
                J.MethodDeclaration m = super.visitMethodDeclaration(method, context);
                
                TryFinder tryFinder = new TryFinder();
                tryFinder.visit(m, context);
                
                if ((METHOD_NAME.equals(method.getSimpleName())) && (method.getBody() != null) && tryFinder.getTryStatements().isEmpty()) {

                    List<Statement> existingStatements = m.getBody().getStatements();
                    
                    // Build the statements string manually
                    StringBuilder stmtBuilder = new StringBuilder();
                    for (Statement stmt : existingStatements) {
                        stmtBuilder.append("    ").append(stmt.printTrimmed(getCursor())).append(";\n");
                    }
                    
                    // Create template without parameters
                    JavaTemplate bodyTemplate = JavaTemplate.builder(
                        "try {\n" +
                        stmtBuilder.toString() +
                        "} catch (Exception e) {\n" +
                        "    e.printStackTrace();" +
                        "} finally {\n" +
                        "    System.out.println(\"Processing complete\");" +
                        "}")
                        .contextSensitive()
                        .build();

                    m = bodyTemplate.apply(getCursor(), method.getCoordinates().replaceBody());
                }
                
                return m;
            }
        };
    }
    
    private static class TryFinder extends JavaIsoVisitor<ExecutionContext> {
        private final List<J.Try> tryStatements = new ArrayList<>();
        
        @Override
        public J.Try visitTry(J.Try tryStatement, ExecutionContext ctx) {
            tryStatements.add(tryStatement);
            return super.visitTry(tryStatement, ctx);
        }
        
        public List<J.Try> getTryStatements() {
            return tryStatements;
        }
    }
}