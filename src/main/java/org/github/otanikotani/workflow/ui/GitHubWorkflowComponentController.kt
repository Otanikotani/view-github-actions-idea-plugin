package org.github.otanikotani.workflow.ui

import com.intellij.openapi.util.Key
import org.jetbrains.plugins.github.pullrequest.ui.toolwindow.GHPRViewComponentController

interface GitHubWorkflowComponentController {
    fun selectCommit(oid: String)

    fun selectChange(oid: String?, filePath: String)

    companion object {
        val KEY = Key.create<GitHubWorkflowComponentController>("Github.PullRequests.View.Controller")
    }
}