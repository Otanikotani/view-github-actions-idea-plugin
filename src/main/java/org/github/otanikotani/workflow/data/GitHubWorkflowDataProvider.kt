package org.github.otanikotani.workflow.data

import org.github.otanikotani.api.GitHubWorkflow
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor

interface GitHubWorkflowDataProvider {
    val url: String

    fun getWorkflow(requestExecutor: GithubApiRequestExecutor): GitHubWorkflow
}