package org.github.otanikotani;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.github.otanikotani.api.CheckRunResponse;
import org.github.otanikotani.api.CheckSuiteResponse;
import org.github.otanikotani.api.CheckRunsResponse;
import org.github.otanikotani.api.CheckSuitesResponse;
import org.github.otanikotani.api.JacksonUnirestObjectMapper;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.repository.CheckSuites;

public class Library {

  public static final String GITHUB_TOKEN = "";

  static {
    Unirest.config()
      .automaticRetries(true)
      .setDefaultHeader("Accept", "application/vnd.github.antiope-preview+json")
      .setDefaultHeader("authorization", "Bearer " + GITHUB_TOKEN)
      .setObjectMapper(new JacksonUnirestObjectMapper());
  }

  public static void main(String[] args) throws Exception {
    String owner = "otanikotani";
    String repo = "view-github-actions-idea-plugin";
    String branch = "c902cf524675ebb69a2758364b3e2d786daf5287";

    CompletableFuture<CheckSuites> checkSuitesFuture = new CheckSuiteRepository(GITHUB_TOKEN)
      .getCheckSuites(owner, repo, branch);

    CheckSuites checkSuites = checkSuitesFuture.get();

    System.out.println(checkSuites);

    System.exit(0);
  }
}
