package org.github.otanikotani.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import one.util.streamex.StreamEx;
import org.github.otanikotani.api.CheckRunsResponse;
import org.github.otanikotani.api.CheckSuiteResponse;
import org.github.otanikotani.api.CheckSuitesResponse;
import org.github.otanikotani.api.JacksonUnirestObjectMapper;

public class CheckSuiteRepository {

  public static final String GITHUB_API_BASE = "https://api.github.com";
  public static final String GITHUB_API_REPO_BASE = GITHUB_API_BASE + "/repos";

  private final UnirestInstance unirest;

  public CheckSuiteRepository(String githubToken) {
    unirest = Unirest.spawnInstance();
    Runtime.getRuntime().addShutdownHook(new Thread(unirest::shutDown));

    unirest.config()
      .automaticRetries(true)
      .setDefaultHeader("Accept", "application/vnd.github.antiope-preview+json")
      .setDefaultHeader("authorization", "Bearer " + githubToken)
      .setObjectMapper(new JacksonUnirestObjectMapper());
  }

  public CompletableFuture<CheckSuites> getCheckSuites(String owner, String repo, String ref) {
    String requestUrl = String.format(GITHUB_API_REPO_BASE + "/%s/%s/commits/%s/check-suites", owner, repo, ref);
    CompletableFuture<HttpResponse<CheckSuitesResponse>> checkSuiteResponse = unirest
      .get(requestUrl)
      .asObjectAsync(CheckSuitesResponse.class);

    return checkSuiteResponse
      .thenApply(response -> {
        if (!response.isSuccess()) {
          throw new ChecksException(requestUrl, response.getStatus(), response.getStatusText(),
            response.getParsingError().orElse(null));
        }
        return response.mapBody(this::toCheckSuites);
      });
  }

  private CheckSuites toCheckSuites(CheckSuitesResponse checkSuitesResponse) {
    return new CheckSuites(StreamEx.of(checkSuitesResponse.getCheck_suites())
      .parallel()
      .map(checkSuiteResponse -> new CheckSuite(
        checkSuiteResponse.getId(),
        checkSuiteResponse.getUrl(),
        checkSuiteResponse.getConclusion(),
        checkSuiteResponse.getStatus(),
        getCheckRuns(checkSuiteResponse)
      ))
      .toList());
  }

  private List<CheckRun> getCheckRuns(CheckSuiteResponse checkSuiteResponse) {
    CheckRunsResponse checkRunsResponse = unirest
      .get(checkSuiteResponse.getCheck_runs_url())
      .asObject(CheckRunsResponse.class)
      .getBody();
    return StreamEx.of(checkRunsResponse.getCheck_runs()).map(checkRunResponse -> new CheckRun(
      checkRunResponse.getId(),
      checkRunResponse.getName(),
      checkRunResponse.getConclusion(),
      checkRunResponse.getCompleted_at(),
      checkRunResponse.getStarted_at(),
      checkRunResponse.getHead_sha(),
      checkRunResponse.getStatus())).toList();
  }
}
