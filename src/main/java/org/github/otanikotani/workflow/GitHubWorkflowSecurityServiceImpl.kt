package org.github.otanikotani.workflow

import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.api.data.GithubUser

class GitHubWorkflowSecurityServiceImpl(override val currentUser: GHUser) : GitHubWorkflowSecurityService {
    override fun isCurrentUser(user: GithubUser) = user.nodeId == currentUser.id
}