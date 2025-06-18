package org.dev.rewrite.petstore.spring;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.J;

/**
 * OpenRewrite recipe that adds a finally block with a logging statement
 * to try blocks that don't already have a finally block.
 */
public class AddFinallyBlockWithLogging extends Recipe {

    @Override
    public String getDisplayName() {
        return "Add finally block with logging";
    }

    @Override
    public String getDescription() {
        return "Adds a finally block with a logging statement to try blocks that don't have one.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new AddFinallyBlockVisitor();
    }

    private static class AddFinallyBlockVisitor extends JavaIsoVisitor<ExecutionContext> {
        
        private final String finallyStatement = "System.out.println(\"In finally block\");";
        
        private final JavaTemplate finallyTemplate = JavaTemplate.builder(
                "{ " + finallyStatement + " }"
        ).contextSensitive().build();

        @Override
        public J.Try visitTry(J.Try tryStatement, ExecutionContext ctx) {

            J.Try t = super.visitTry(tryStatement, ctx);
            
            // Only add finally block if one doesn't already exist
            if (t.getFinally() == null) {

                // Create the finally block
                J.Block finallyBlock = finallyTemplate.apply(
                    updateCursor(tryStatement),
                    tryStatement.getCoordinates().replace()
                );

                return t.withFinally(finallyBlock);
            }
            else {
                return t;
            }
        }
    }
}