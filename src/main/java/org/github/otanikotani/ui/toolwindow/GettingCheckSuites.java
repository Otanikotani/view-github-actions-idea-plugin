package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import git4idea.repo.GitRemote;
import one.util.streamex.StreamEx;
import org.github.otanikotani.api.CheckRuns;
import org.github.otanikotani.api.CheckSuites;
import org.github.otanikotani.api.GithubCheckRun;
import org.github.otanikotani.api.GithubCheckRuns;
import org.github.otanikotani.api.GithubCheckSuites;
import org.github.otanikotani.ui.toolwindow.ChecksRefresher.ChecksRefreshedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor;
import org.jetbrains.plugins.github.exceptions.GithubStatusCodeException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.regex.Pattern;

public class GettingCheckSuites extends Backgroundable {

    private static final Pattern DOT_GIT_PATTERN = Pattern.compile("\\.git$");

    private final ChecksTable checksTable;
    private final ChecksLocation location;
    private final GithubApiRequestExecutor.WithTokenAuth executor;
    private String owner;
    private String repo;
    private List<? extends GithubCheckRun> checkRuns;

    GettingCheckSuites(ChecksLocation location, ChecksTable checksTable,
        GithubApiRequestExecutor.WithTokenAuth executor) {
        super(location.repository.getProject(), "Getting Check Suites...");
        this.location = location;
        this.checksTable = checksTable;
        this.executor = executor;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        String remoteUrl = StreamEx.of(location.repository.getRemotes()).map(GitRemote::getFirstUrl)
            .findFirst()
            .map(url -> DOT_GIT_PATTERN.matcher(url).replaceFirst(""))
            .orElseThrow(() -> new RuntimeException("Failed to find a remote url"));

        String[] parts = remoteUrl.split("/");
        repo = parts[parts.length - 1];
        owner = parts[parts.length - 2];

        GithubApiRequest<GithubCheckSuites> request = new CheckSuites()
            .get(location.account.getServer(), owner, repo, location.repository.getCurrentBranchName());
        GithubCheckSuites suites;
        try {
            suites = executor.execute(indicator, request);
        } catch (GithubStatusCodeException e) {
            if (e.getStatusCode() == 404) {
                suites = new GithubCheckSuites();
            } else {
                throw new UncheckedIOException("Unexpected failure", e);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unexpected failure", e);
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
        checksTable.refresh(owner, repo, checkRuns);
        ChecksRefreshedListener publisher = myProject.getMessageBus()
            .syncPublisher(ChecksRefreshedListener.CHECKS_REFRESHED);
        publisher.checksRefreshed();
    }
}
