package com.test.one

import org.gradle.api.Plugin
import org.gradle.api.Project


class MyPlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("========================");
        System.out.println("自定义Plugin的第二种方式 本工程内可以依赖访问!");
        System.out.println("========================");
    }
}
