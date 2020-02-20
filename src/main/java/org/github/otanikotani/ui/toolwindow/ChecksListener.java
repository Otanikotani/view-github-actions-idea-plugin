package org.github.otanikotani.ui.toolwindow;

import git4idea.repo.GitRepository;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

public interface ChecksListener {

    void onGithubAccountChange(GithubAccount githubAccount);

    void onBranchChange(String branchName);

    void onRefresh();
}
