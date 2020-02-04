package org.github.otanikotani.api;

import java.util.List;

public class GithubCheckRuns {

  int total_count;
  List<GithubCheckRun> check_runs;

  public int getTotal_count() {
    return total_count;
  }

  public void setTotal_count(int total_count) {
    this.total_count = total_count;
  }

  public List<GithubCheckRun> getCheck_runs() {
    return check_runs;
  }

  public void setCheck_runs(List<GithubCheckRun> check_runs) {
    this.check_runs = check_runs;
  }
}
