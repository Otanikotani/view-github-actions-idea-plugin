package org.github.otanikotani.api;

import java.util.List;
import java.util.Objects;

public class GithubWorkflowRuns {

    int total_count;
    List<GithubWorkflowRun> workflow_runs;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<GithubWorkflowRun> getWorkflow_runs() {
        return workflow_runs;
    }

    public void setWorkflow_runs(List<GithubWorkflowRun> workflow_runs) {
        this.workflow_runs = workflow_runs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GithubWorkflowRuns that = (GithubWorkflowRuns) o;
        return total_count == that.total_count &&
            Objects.equals(workflow_runs, that.workflow_runs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total_count, workflow_runs);
    }

    @Override
    public String toString() {
        return "GithubWorkflowRuns{" +
            "total_count=" + total_count +
            ", workflow_runs=" + workflow_runs +
            '}';
    }
}
