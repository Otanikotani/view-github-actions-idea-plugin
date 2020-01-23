package org.github.otanikotani;

import java.util.concurrent.CompletableFuture;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.repository.CheckSuites;

public class Library {

  public static final String GITHUB_TOKEN = "Your token";

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
