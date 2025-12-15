import jobs_library.MultibranchPipeline

evaluate(new File("../jobs_library/JobsParams.groovy"))
evaluate(new File("../jobs_library/MultibranchPipeline.groovy"))

String project_name = "simple-java-maven-app"

new MultibranchPipeline().
        project_name(project_name).
        repository_name(project_name).
        build(this)