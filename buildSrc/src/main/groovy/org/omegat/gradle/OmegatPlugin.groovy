package org.omegat.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.tasks.Delete


class OmegatPlugin implements Plugin<Project> {
    static final String OMEGAT_CONFIGURATION_NAME = "omegat"
    static final String TASK_BUILD_NAME = "translate"
    static final String TASK_RUN_NAME = "omegat"
    static final String TASK_CLEAN_NAME = 'clean'
    private Project project

    @Override
    def void apply(Project project) {
        this.project = project

        Configuration config = project.configurations.create(OMEGAT_CONFIGURATION_NAME)
            .setVisible(true).setTransitive(true)
            .setDescription('The OmegaT configuration for this project.')

        project.with {
            tasks.create(name: TASK_BUILD_NAME, type: OmegatTask) {
                description = "Generate translations into OmegaT target directory."
                options = [project.getRootDir().toString(), "--mode=console-translate",
                           "--disable-project-locking", "--quiet"]
            }
            tasks.create(name: TASK_RUN_NAME, type: OmegatTask) {
                description = "Run OmegaT application with GUI."
                options = [project.getRootDir().toString()]
            }
            tasks.create(name: TASK_CLEAN_NAME, type: Delete) {
                new File(project.getRootDir(), "target").listFiles()
                        .findAll { it.isDirectory() || !(it.name.startsWith('.')) }.each {
                    setDelete(it)
                }
            }

            afterEvaluate {
                if (project.repositories.isEmpty()) {
                    project.repositories.jcenter()
                }

                //
                // FIXME: how to add lib/*.jar files as dependency?
                //  The following expression is not working on Gradle 2.14.1
                //    addDependency(config, fileTree("lib"))
                //  We use work-around to add following configuration on root project
                //    dependencies {
                //          omegat fileTree("buildSrc/lib")
                //    }

                addDependency(config, 'org.languagetool:languagetool-core:3.3')
                // Temporary exclude gosen-ipadic
                // see https://sourceforge.net/p/omegat/bugs/814/
                addDependency(config, 'org.languagetool:language-all:3.3', 'lucene-gosen-ipadic')
                addDependency(config, 'org.languagetool:hunspell-native-libs:2.9')
                addDependency(config, 'org.apache.lucene:lucene-analyzers-common:5.2.1')
                addDependency(config, 'org.apache.lucene:lucene-analyzers-kuromoji:5.2.1')
                addDependency(config, 'org.apache.lucene:lucene-analyzers-smartcn:5.2.1')
                addDependency(config, 'org.apache.lucene:lucene-analyzers-stempel:5.2.1')
                addDependency(config, 'org.eclipse.jgit:org.eclipse.jgit:4.2.0.201601211800-r')
                addDependency(config, 'com.jcraft:jsch.agentproxy.jsch:0.0.9')
                addDependency(config, 'com.jcraft:jsch.agentproxy.connector-factory:0.0.9')
                addDependency(config, 'com.jcraft:jsch.agentproxy.svnkit-trilead-ssh2:0.0.9')
                addDependency(config, 'org.tmatesoft.svnkit:svnkit:1.8.12')
                addDependency(config, 'org.apache.pdfbox:pdfbox:2.0.0')
                addDependency(config, 'net.loomchild:maligna:3.0.0')
                addDependency(config, 'commons-io:commons-io:2.4')
                addDependency(config, 'commons-lang:commons-lang:2.6')
                addDependency(config, 'org.slf4j:slf4j-jdk14:1.7.21')
                addDependency(config, 'org.dict.zip:dictzip-lib:0.8.1')
                addDependency(config, 'com.github.takawitter:trie4j:0.9.2')
                addDependency(config, 'org.madlonkay.supertmxmerge:supertmxmerge:2.0.1')
                addDependency(config, 'org.omegat:vldocking:3.0.5')
                addDependency(config, 'org.omegat:juniversalchardet:1.0.4')
                addDependency(config, 'org.codehaus.groovy:groovy-all:2.4.6')
                addDependency(config, 'com.fifesoft:rsyntaxtextarea:2.5.8')
                addDependency(config, 'com.fifesoft:rstaui:2.5.7')
                addDependency(config, 'com.fifesoft:languagesupport:2.5.8', 'rhino')
                addDependency(config, 'com.fifesoft:autocomplete:2.5.8')
            }
        }
    }

    private ModuleDependency addDependency(Configuration configuration, Object notation) {
        ModuleDependency dependency = project.dependencies.create(notation) as ModuleDependency
        configuration.dependencies.add(dependency)
        dependency
    }
    private ModuleDependency addDependency(Configuration configuration, String notation,
                                           String exception) {
        ModuleDependency dependency = project.dependencies.create(notation) as ModuleDependency
        dependency.exclude(module: exception)
        configuration.dependencies.add(dependency)
        dependency
    }
}
