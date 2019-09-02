package com.plugin.custom

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import com.android.build.gradle.api.BaseVariant

class PrintPlugin implements Plugin<Project> {
    void apply(Project project) {
        System.out.println("========================");
        System.out.println("自定义Plugin的第三种方式 用Maven上传仓库方式访问!");

        project.extensions.create("printbean",PrintBean)

        Task compile = project.tasks.create("generatorTask",GeneratorTask)
        registerTask()

        System.out.println("name=${project.printbean.name} sex=${ project.printbean.sex} age=${ project.printbean.age}");

        System.out.println("========================");
    }

    def registerTask(){
    }

}

