package org.github.otanikotani.workflow

import org.jetbrains.plugins.github.api.GHRepositoryPath
import org.jetbrains.plugins.github.api.GithubServerPath

data class GitHubRepositoryCoordinates(val serverPath: GithubServerPath, val repositoryPath: GHRepositoryPath) {
    fun toUrl(): String {
        return serverPath.toUrl() + "/" + repositoryPath
    }

    override fun toString(): String {
        return "$serverPath/$repositoryPath"
    }
}
