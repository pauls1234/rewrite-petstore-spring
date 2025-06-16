Maven commands:

mvn -U org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.recipeArtifactCoordinates=org.dev:rewrite-petstore-spring:0.0.1-SNAPSHOT -Drewrite.activeRecipes=org.dev.rewrite.petstore.spring.CreateAppLoggerClass
