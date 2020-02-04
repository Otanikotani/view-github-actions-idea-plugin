package org.github.otanikotani.ui.toolwindow;

import java.util.Objects;

public class GithubCheckSuite {

  Long id;
  String url;
  String check_runs_url;
  String status;
  String conclusion;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getCheck_runs_url() {
    return check_runs_url;
  }

  public void setCheck_runs_url(String check_runs_url) {
    this.check_runs_url = check_runs_url;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getConclusion() {
    return conclusion;
  }

  public void setConclusion(String conclusion) {
    this.conclusion = conclusion;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GithubCheckSuite that = (GithubCheckSuite) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(url, that.url) &&
      Objects.equals(check_runs_url, that.check_runs_url) &&
      Objects.equals(status, that.status) &&
      Objects.equals(conclusion, that.conclusion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, url, check_runs_url, status, conclusion);
  }
}
