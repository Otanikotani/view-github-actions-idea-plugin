package org.github.otanikotani.api;

import java.util.List;
import java.util.Objects;

public class GithubWorkflows {

    int total_count;
    List<GithubWorkflow> workflows;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<GithubWorkflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<GithubWorkflow> workflows) {
        this.workflows = workflows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GithubWorkflows that = (GithubWorkflows) o;
        return total_count == that.total_count &&
            Objects.equals(workflows, that.workflows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total_count, workflows);
    }

    @Override
    public String
    toString() {
        return "GithubWorkflows{" +
            "total_count=" + total_count +
            ", workflows=" + workflows +
            '}';
    }
}
