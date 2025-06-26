package org.dev.rewrite.petstore.spring;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;

class AddTryBlockToMethodTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new AddTryBlockToMethod())
            .parser(JavaParser.fromJavaVersion())
            .typeValidationOptions(TypeValidation.none());
    }

    @Test
    @DocumentExample
    void addTryBlockToSimpleMethod() {
        rewriteRun(
            java(
                """
                package com.example;
                
                public class TestService {
                    public void processData() {
                        System.out.println("Processing data");
                        doSomeWork();
                    }
                    
                    private void doSomeWork() {
                        // some work
                    }
                }
                """,
                """
                package com.example;
                
                public class TestService {
                    public void processData() {
                        try {
                            System.out.println("Processing data");
                            doSomeWork();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("Processing complete");
                        }
                    }
                    
                    private void doSomeWork() {
                        // some work
                    }
                }
                """
            )
        );
    }
}