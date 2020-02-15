package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.regex.Pattern;
import one.util.streamex.StreamEx;
import org.github.otanikotani.api.CheckRuns;
import org.github.otanikotani.api.CheckSuites;
import org.github.otanikotani.api.GithubCheckRun;
import org.github.otanikotani.api.GithubCheckRuns;
import org.github.otanikotani.api.GithubCheckSuites;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

public class GettingCheckSuites extends Backgroundable {

  private static final Pattern DOT_GIT_PATTERN = Pattern.compile("\\.git$");

  private final ChecksPanel checksPanel;
  private final GitRepository repository;
  private final GithubAccount account;
  private final WithTokenAuth executor;
  private String owner;
  private String repo;
  private List<? extends GithubCheckRun> checkRuns;

  GettingCheckSuites(Project project, ChecksPanel checksPanel, GitRepository repository,
    GithubAccount account, WithTokenAuth executor) {
    super(project, "Getting Check Suites...");
    this.checksPanel = checksPanel;
    this.repository = repository;
    this.account = account;
    this.executor = executor;
  }

  @Override
  public void run(@NotNull ProgressIndicator indicator) {
    String remoteUrl = StreamEx.of(repository.getRemotes()).map(GitRemote::getFirstUrl)
      .findFirst()
      .map(url -> DOT_GIT_PATTERN.matcher(url).replaceFirst(""))
      .orElseThrow(() -> new RuntimeException("Failed to find a remote url"));

    String[] parts = remoteUrl.split("/");
    repo = parts[parts.length - 1];
    owner = parts[parts.length - 2];

    GithubApiRequest<GithubCheckSuites> request = new CheckSuites()
      .get(account.getServer(), owner, repo, repository.getCurrentBranchName());
    GithubCheckSuites suites;
    try {
      suites = executor.execute(indicator, request);
    } catch (IOException e) {
      throw new UncheckedIOException("Expected failure", e);
    }

    checkRuns = StreamEx.of(suites.getCheck_suites())
      .flatMap(it -> {

        GithubApiRequest<GithubCheckRuns> checkRunsRequest = new CheckRuns().get(it.getCheck_runs_url());
        try {
          return executor.execute(indicator, checkRunsRequest).getCheck_runs().stream();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      })
      .toList();
  }

  @Override
  public void onSuccess() {
    checksPanel.refresh(owner, repo, checkRuns);
    ChecksRefreshedListener publisher = myProject.getMessageBus()
      .syncPublisher(ChecksRefreshedListener.CHECKS_REFRESHED);
    publisher.checksRefreshed();
  }
}
