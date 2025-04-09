package org.dev.rewrite.petstore.spring.table;

import lombok.Value;
import org.openrewrite.Column;
import org.openrewrite.DataTable;
import org.openrewrite.Recipe;

public class PackageName extends DataTable<PackageName.Row> {

    public PackageName(Recipe recipe) {
        super(recipe, "Package name", "Record the package names");
    }
    
    @Value
    public static class Row {
        @Column(displayName = "Package name",
                description = "Package name.")
        String packageName;
    }
}
