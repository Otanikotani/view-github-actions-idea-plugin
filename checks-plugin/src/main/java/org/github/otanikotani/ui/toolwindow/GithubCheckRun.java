package org.github.otanikotani.ui.toolwindow;

import java.util.Date;

public class GithubCheckRun {

  Long id;
  String name;
  String conclusion;
  Date completed_at;
  Date started_at;
  String head_sha;
  String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getConclusion() {
    return conclusion;
  }

  public void setConclusion(String conclusion) {
    this.conclusion = conclusion;
  }

  public Date getCompleted_at() {
    return completed_at;
  }

  public void setCompleted_at(Date completed_at) {
    this.completed_at = completed_at;
  }

  public Date getStarted_at() {
    return started_at;
  }

  public void setStarted_at(Date started_at) {
    this.started_at = started_at;
  }

  public String getHead_sha() {
    return head_sha;
  }

  public void setHead_sha(String head_sha) {
    this.head_sha = head_sha;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

