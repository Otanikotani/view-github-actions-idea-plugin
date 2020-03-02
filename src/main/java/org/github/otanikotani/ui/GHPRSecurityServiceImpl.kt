package org.github.otanikotani.ui

import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.api.data.GithubUser

class GHWorkflowSecurityServiceImpl(override val currentUser: GHUser) : GHWorkflowSecurityService {
    override fun isCurrentUser(user: GithubUser) = user.nodeId == currentUser.id
}