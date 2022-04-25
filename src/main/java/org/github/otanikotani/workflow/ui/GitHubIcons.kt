package org.github.otanikotani.workflow.ui

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object GitHubIcons {
    private fun load(path: String): Icon {
        return IconLoader.getIcon(path, GitHubIcons::class.java)
    }

    @JvmField
    val Check = load("/icons/check.svg")

    @JvmField
    val PrimitiveDot = load("/icons/primitive-dot.svg")

    @JvmField
    val Watch = load("/icons/watch.svg")

    @JvmField
    val Workflow = load("/icons/workflow.svg")

    @JvmField
    val WorkflowAll = load("/icons/workflow-all.svg")

    @JvmField
    val WorkflowAllToolbar = load("/icons/workflow-all-toolbar.svg")

    @JvmField
    val X = load("/icons/x.svg")
}