package org.github.otanikotani;

import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.util.Optional;

public interface GithubAccountManager {

    Optional<GithubAccount> getAccountForProject(Project project);

}
