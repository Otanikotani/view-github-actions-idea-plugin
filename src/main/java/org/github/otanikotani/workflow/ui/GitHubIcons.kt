package org.github.otanikotani.workflow.ui

import com.intellij.icons.AllIcons
import com.intellij.ui.IconManager
import javax.swing.Icon

object GitHubIcons {
    private fun load(path: String): Icon? {
        return IconManager.getInstance().getIcon(path, GitHubIcons::class.java)
    }


    val Check = load("/icons/check.svg")
    val PrimitiveDot = load("/icons/primitive-dot.svg")
    val Watch = load("/icons/watch.svg")
    val Workflow = load("/icons/workflow.svg")
    val WorkflowAll = load("/icons/workflow-all.svg")
    val X = load("/icons/x.svg")
}