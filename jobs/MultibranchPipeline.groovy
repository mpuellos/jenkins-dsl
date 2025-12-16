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
                            repositoryUrl('')
                            configuredByUrl(false)

                            traits {
                                headWildcardFilter {
                                    includes(this.branches)
                                    excludes('')
                                }
                                gitHubBranchDiscovery {
                                    strategyId(1)    // Exclude branches that are also filed as PRs
                                }
                                gitHubPullRequestDiscovery {
                                    strategyId(1)    // Merge con la target
                                }
                            }


                        }

                    }
                }


            }
            configure { node ->
                // 1️⃣ Agregar el trait para PRs desde forks
                def traitsNode = node / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
                traitsNode << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
                    strategyId(1) // 1 = "Merging the pull request with the current target branch revision"
                    trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
                }

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