package org.github.otanikotani.ui.toolwindow

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI
import com.intellij.ui.content.ContentFactory
import org.github.otanikotani.ChecksContext
import org.github.otanikotani.GithubAccountManager
import org.github.otanikotani.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount

class SimpleChecksContext implements ChecksContext {

    Project project
    Optional<ActionManager> actionManager
    ChangesViewContentI changesViewContentManager
    Application application
    GithubAccountManager githubAccountManager
    ContentFactory contentFactory
    GithubApiRequestExecutor executor

    @Override
    Optional<GithubApiRequestExecutor> getGithubApiExecutor(GithubAccount account, Project project) {
        return Optional.ofNullable(this.executor)
    }
}
