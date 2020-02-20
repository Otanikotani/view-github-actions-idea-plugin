package org.github.otanikotani;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.util.Optional;

public interface ChecksContext {

    @Nullable
    Project getProject();

    Optional<ActionManager> getActionManager();

    @NotNull
    ChangesViewContentI getChangesViewContentManager();

    @NotNull
    Application getApplication();

    GithubAccountManager getGithubAccountManager();

    ContentFactory getContentFactory();

    Optional<GithubApiRequestExecutor> getGithubApiExecutor(GithubAccount account, Project project);
}
