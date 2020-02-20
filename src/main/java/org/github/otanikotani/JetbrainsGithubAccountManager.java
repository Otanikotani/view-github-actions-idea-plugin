package org.github.otanikotani;

import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.util.Optional;

public class JetbrainsGithubAccountManager implements GithubAccountManager {

    private final GithubAuthenticationManager manager;

    public JetbrainsGithubAccountManager(GithubAuthenticationManager manager) {
        this.manager = manager;
    }

    @Override
    public Optional<GithubAccount> getAccountForProject(Project project) {
        return Optional.ofNullable(manager.getSingleOrDefaultAccount(project));
    }
}
