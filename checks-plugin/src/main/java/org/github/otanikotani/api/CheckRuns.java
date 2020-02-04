package org.github.otanikotani.api;

import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequest.Get.Json;

public class CheckRuns {

  public GithubApiRequest<GithubCheckRuns> get(String url) {
    return new Json<>(
      url,
      GithubCheckRuns.class,
      "application/vnd.github.antiope-preview+json")
      .withOperationName("Get Check Runs...");
  }

}
