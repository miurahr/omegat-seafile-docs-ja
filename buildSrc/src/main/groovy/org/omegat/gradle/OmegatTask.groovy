package org.omegat.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaExecSpec

/**
 * Created by miurahr on 16/08/01.
 */
class OmegatTask extends DefaultTask {
    @Input String[] options

    @TaskAction
    void start() {
        project.javaexec({ JavaExecSpec javaExecSpec ->
            javaExecSpec.setMain("org.omegat.Main").args(options)
            javaExecSpec.setMaxHeapSize("2048M")
            javaExecSpec.setClasspath(project.configurations.getByName(OmegatPlugin.OMEGAT_CONFIGURATION_NAME))
        });
    }

}
