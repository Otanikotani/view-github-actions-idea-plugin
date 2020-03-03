package org.github.otanikotani.api;

import java.util.Date;
import java.util.Objects;

public class GithubWorkflow {

    long id;
    String node_id;
    String name;
    String path;
    String state;
    String completed_at;
    String updated_at;
    String url;
    String html_url;
    String badge_url;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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

    public String getBadge_url() {
        return badge_url;
    }

    public void setBadge_url(String badge_url) {
        this.badge_url = badge_url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GithubWorkflow that = (GithubWorkflow) o;
        return id == that.id &&
            Objects.equals(node_id, that.node_id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(path, that.path) &&
            Objects.equals(state, that.state) &&
            Objects.equals(completed_at, that.completed_at) &&
            Objects.equals(updated_at, that.updated_at) &&
            Objects.equals(url, that.url) &&
            Objects.equals(html_url, that.html_url) &&
            Objects.equals(badge_url, that.badge_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, node_id, name, path, state, completed_at, updated_at, url, html_url, badge_url);
    }

    @Override
    public String toString() {
        return "GithubWorkflow{" +
            "id=" + id +
            ", node_id='" + node_id + '\'' +
            ", name='" + name + '\'' +
            ", path='" + path + '\'' +
            ", state='" + state + '\'' +
            ", completed_at=" + completed_at +
            ", updated_at=" + updated_at +
            ", url='" + url + '\'' +
            ", html_url='" + html_url + '\'' +
            ", badge_url='" + badge_url + '\'' +
            '}';
    }
}
