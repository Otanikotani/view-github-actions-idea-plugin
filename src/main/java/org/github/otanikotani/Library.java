package org.github.otanikotani;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.List;
import org.github.otanikotani.api.CheckRunResponse;
import org.github.otanikotani.api.CheckSuiteGetResult;
import org.github.otanikotani.api.GetCheckRunsResponse;
import org.github.otanikotani.api.GetCheckSuiteResponse;
import org.github.otanikotani.api.JacksonUnirestObjectMapper;

public class Library {

  public static final String GITHUB_TOKEN = "Your token";

  static {
    Unirest.setDefaultHeader("Accept", "application/vnd.github.antiope-preview+json");
    Unirest.setDefaultHeader("authorization", "Bearer " + GITHUB_TOKEN);
    Unirest.setObjectMapper(new JacksonUnirestObjectMapper());
  }

  public static void main(String[] args) throws Exception {
    String owner = "otanikotani";
    String repo = "view-github-actions-idea-plugin";
    String branch = "c902cf524675ebb69a2758364b3e2d786daf5287";

    List<CheckSuiteGetResult> suites =
      getCheckSuites(owner, repo, branch)
        .getCheck_suites();
    for (CheckSuiteGetResult suite : suites) {
      GetCheckRunsResponse checkRunsResponse = getCheckRuns(suite);
      for (CheckRunResponse checkRun : checkRunsResponse.getCheck_runs()) {
        System.out.println(checkRun);
      }
    }
  }

  private static GetCheckRunsResponse getCheckRuns(CheckSuiteGetResult suite) throws UnirestException {
    return Unirest
      .get(suite.getCheck_runs_url())
      .asObject(GetCheckRunsResponse.class)
      .getBody();
  }


  public static GetCheckSuiteResponse getCheckSuites(String owner, String repository, String branchName)
    throws UnirestException {
    return Unirest
      .get(String.format("https://api.github.com/repos/%s/%s/commits/%s/check-suites", owner, repository, branchName))
      .asObject(GetCheckSuiteResponse.class)
      .getBody();
  }
}
