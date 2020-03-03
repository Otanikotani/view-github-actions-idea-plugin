package org.github.otanikotani.workflow

import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates

interface GitHubWorkflowActionDataContext {
    val account: GithubAccount

    val requestExecutor: GithubApiRequestExecutor

    val gitRepositoryCoordinates: GitRemoteUrlCoordinates
    val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates

    //    val avatarIconsProviderFactory: CachingGithubAvatarIconsProvider.Factory

    fun resetAllData()
}