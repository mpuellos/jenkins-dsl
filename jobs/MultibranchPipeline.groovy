import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import javaposse.jobdsl.dsl.DslFactory

@Builder(builderStrategy = SimpleStrategy, prefix = '')

class MultibranchPipeline extends JobsParams {

    String project_name
    String repository_name
    String branches = 'main fix/*'
    String enterprise = 'mpuellos'


    void build(DslFactory dslFactory) {
        dslFactory.multibranchPipelineJob(this.project_name) {
            branchSources {
                branchSource {
                    source {
                        github {
                            id(this.project_name)
                            repoOwner(this.enterprise)
                            repository(this.repository_name)
                            includes(this.branches)


                        }

                    }
                }


            }
            configure { node ->
                def factory = node / 'factory'
                if (factory == null) {
                    factory = node.appendNode('factory')
                }

                def definition = factory.children().find { it.name() == 'definition' }
                if (definition == null) {
                    definition = factory.appendNode('definition')
                }

                def scm = definition.children().find { it.name() == 'scm' }
                if (scm == null) {
                    scm = definition.appendNode('scm')
                }

                def extensions = scm.children().find { it.name() == 'extensions' }
                if (extensions == null) {
                    extensions = scm.appendNode('extensions')
                }

                extensions.appendNode('hudson.plugins.git.extensions.impl.WipeWorkspace')
            }
            orphanedItemStrategy {
                discardOldItems {
                    numToKeep(this.orphan_items_keep)
                }
            }
        }
    }
}