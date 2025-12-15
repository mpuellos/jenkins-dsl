String project_name = "simple-java-maven-app"


new MultibranchPipeline().
        project_name(project_name).
        repository_name(project_name).
        build(this)