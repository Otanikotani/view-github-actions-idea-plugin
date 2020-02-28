package org.github.otanikotani.api;

import java.util.Date;
import java.util.Objects;

public class GithubWorkflowRun {

    long id;
    String node_id;
    String head_branch;
    String head_sha;
    int run_number;
    String event;
    String status;
    String conclusion;
    String url;
    String html_url;
    Date created_at;
    Date updated_at;
    String jobs_url;
    String logs_url;
    String check_suite_url;
    String artifacts_url;
    String cancel_url;
    String rerun_url;
    String workflow_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getHead_branch() {
        return head_branch;
    }

    public void setHead_branch(String head_branch) {
        this.head_branch = head_branch;
    }

    public String getHead_sha() {
        return head_sha;
    }

    public void setHead_sha(String head_sha) {
        this.head_sha = head_sha;
    }

    public int getRun_number() {
        return run_number;
    }

    public void setRun_number(int run_number) {
        this.run_number = run_number;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getJobs_url() {
        return jobs_url;
    }

    public void setJobs_url(String jobs_url) {
        this.jobs_url = jobs_url;
    }

    public String getLogs_url() {
        return logs_url;
    }

    public void setLogs_url(String logs_url) {
        this.logs_url = logs_url;
    }

    public String getCheck_suite_url() {
        return check_suite_url;
    }

    public void setCheck_suite_url(String check_suite_url) {
        this.check_suite_url = check_suite_url;
    }

    public String getArtifacts_url() {
        return artifacts_url;
    }

    public void setArtifacts_url(String artifacts_url) {
        this.artifacts_url = artifacts_url;
    }

    public String getCancel_url() {
        return cancel_url;
    }

    public void setCancel_url(String cancel_url) {
        this.cancel_url = cancel_url;
    }

    public String getRerun_url() {
        return rerun_url;
    }

    public void setRerun_url(String rerun_url) {
        this.rerun_url = rerun_url;
    }

    public String getWorkflow_url() {
        return workflow_url;
    }

    public void setWorkflow_url(String workflow_url) {
        this.workflow_url = workflow_url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GithubWorkflowRun that = (GithubWorkflowRun) o;
        return id == that.id &&
            run_number == that.run_number &&
            Objects.equals(node_id, that.node_id) &&
            Objects.equals(head_branch, that.head_branch) &&
            Objects.equals(head_sha, that.head_sha) &&
            Objects.equals(event, that.event) &&
            Objects.equals(status, that.status) &&
            Objects.equals(conclusion, that.conclusion) &&
            Objects.equals(url, that.url) &&
            Objects.equals(html_url, that.html_url) &&
            Objects.equals(created_at, that.created_at) &&
            Objects.equals(updated_at, that.updated_at) &&
            Objects.equals(jobs_url, that.jobs_url) &&
            Objects.equals(logs_url, that.logs_url) &&
            Objects.equals(check_suite_url, that.check_suite_url) &&
            Objects.equals(artifacts_url, that.artifacts_url) &&
            Objects.equals(cancel_url, that.cancel_url) &&
            Objects.equals(rerun_url, that.rerun_url) &&
            Objects.equals(workflow_url, that.workflow_url);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(id, node_id, head_branch, head_sha, run_number, event, status, conclusion, url, html_url, created_at,
                updated_at, jobs_url, logs_url, check_suite_url, artifacts_url, cancel_url, rerun_url, workflow_url);
    }

    @Override
    public String toString() {
        return "GithubWorkflowRun{" +
            "id=" + id +
            ", node_id='" + node_id + '\'' +
            ", head_branch='" + head_branch + '\'' +
            ", head_sha='" + head_sha + '\'' +
            ", run_number=" + run_number +
            ", event='" + event + '\'' +
            ", status='" + status + '\'' +
            ", conclusion='" + conclusion + '\'' +
            ", url='" + url + '\'' +
            ", html_url='" + html_url + '\'' +
            ", created_at=" + created_at +
            ", updated_at=" + updated_at +
            ", jobs_url='" + jobs_url + '\'' +
            ", logs_url='" + logs_url + '\'' +
            ", check_suite_url='" + check_suite_url + '\'' +
            ", artifacts_url='" + artifacts_url + '\'' +
            ", cancel_url='" + cancel_url + '\'' +
            ", rerun_url='" + rerun_url + '\'' +
            ", workflow_url='" + workflow_url + '\'' +
            '}';
    }
}
