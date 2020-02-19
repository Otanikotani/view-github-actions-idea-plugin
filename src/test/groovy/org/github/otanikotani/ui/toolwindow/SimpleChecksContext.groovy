package org.github.otanikotani.ui.toolwindow

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI
import com.intellij.ui.content.ContentFactory
import org.github.otanikotani.ChecksContext
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager

class SimpleChecksContext implements ChecksContext {

    Project project
    Optional<ActionManager> actionManager
    ChangesViewContentI changesViewContentManager
    Application application
    GithubApiRequestExecutorManager githubApiRequestExecutorManager
    GithubAuthenticationManager githubAuthenticationManager
    ContentFactory contentFactory
}
