package org.github.otanikotani.ui.toolwindow;

import git4idea.repo.GitRepository;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;


public class WorkflowsLocation {

    public final GitRepository repository;
    public final GithubAccount account;

    public WorkflowsLocation(GitRepository repository,
        GithubAccount account) {
        this.repository = repository;
        this.account = account;
    }
}
