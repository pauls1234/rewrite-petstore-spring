package org.dev.rewrite.petstore.spring;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.java.Assertions.java;

import org.dev.rewrite.petstore.spring.table.PackageName;

class FindPackagesTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new FindPackages());
    }
    
    @Test
    void findPackages() {
        rewriteRun(
            spec -> spec.dataTable(PackageName.Row.class,
              rows -> assertThat(rows).containsOnly(
                 new PackageName.Row("org.openrewrite.test.one"),
                 new PackageName.Row("org.openrewrite.test.two"))),   
            java(
              """
                package org.openrewrite.test.one;
                class TestOne {
                    int a;
                }
                """
            ),
            java(
              """
                package org.openrewrite.test.one;
                class TestOneA {
                    int a;
                }
                """
            ),
            java(
              """
                package org.openrewrite.test.two;
                class TestTwo {
                    int a;
                }
                """
            )
        );
    }
}
