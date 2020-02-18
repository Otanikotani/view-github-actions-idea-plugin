package org.github.otanikotani.api;

import java.util.List;
import java.util.Objects;

public class GithubCheckSuites {

    int total_count;
    List<GithubCheckSuite> check_suites;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<GithubCheckSuite> getCheck_suites() {
        return check_suites;
    }

    public void setCheck_suites(List<GithubCheckSuite> check_suites) {
        this.check_suites = check_suites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GithubCheckSuites that = (GithubCheckSuites) o;
        return total_count == that.total_count &&
            Objects.equals(check_suites, that.check_suites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total_count, check_suites);
    }
}
