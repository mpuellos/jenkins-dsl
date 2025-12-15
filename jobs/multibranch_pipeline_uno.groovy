String project_name = "simple-java-maven-app"

println "Tipo de this: ${this.class}"
println "Es DslFactory?: ${this instanceof javaposse.jobdsl.dsl.DslFactory}"

new MultibranchPipeline().
        project_name(project_name).
        repository_name(project_name).
        build(this)