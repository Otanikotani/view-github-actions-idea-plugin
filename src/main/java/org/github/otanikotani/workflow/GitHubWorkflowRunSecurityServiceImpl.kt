package org.github.otanikotani.workflow

import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.api.data.GithubUser

class GitHubWorkflowRunSecurityServiceImpl(override val currentUser: GHUser) : GitHubWorkflowRunSecurityService {
    override fun isCurrentUser(user: GithubUser) = user.nodeId == currentUser.id
}