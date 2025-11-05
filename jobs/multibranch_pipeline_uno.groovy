String project_name = "multibranch-pipeline-uno"

new MultibranchPipeline().
        project_name(project_name).
        repository_name(project_name).
        build(this)