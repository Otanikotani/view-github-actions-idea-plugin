package org.github.otanikotani.api;

import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequest.Get;
import org.jetbrains.plugins.github.api.GithubApiRequests.Entity;
import org.jetbrains.plugins.github.api.GithubServerPath;

public class CheckSuites extends Entity {

  public CheckSuites() {
    super("/repos");
  }

  public GithubApiRequest<GithubCheckSuites> get(GithubServerPath server, String owner, String repo, String ref) {
    String url = String.format("%s%s/%s/%s/commits/%s/check-suites",
      server.toApiUrl(), getUrlSuffix(), owner, repo, ref);
    return new Get.Json<>(url, GithubCheckSuites.class, "application/vnd.github.antiope-preview+json")
      .withOperationName("Get Check Suites...");
  }
}
