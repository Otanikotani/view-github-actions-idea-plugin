package org.github.otanikotani.api

import org.jetbrains.plugins.github.api.GithubServerPath
import spock.lang.Specification

class WorkflowsSpec extends Specification {

    GithubServerPath server = new GithubServerPath("github.com")
    Workflows workflows = new Workflows()

    def "get workflow runs by branch creates an expected url"() {
        when:
        def request = workflows.getWorkflowRunsByBranch(server, "banana", "cat", "develop")

        then:
        request.url == "https://api.github.com/repos/banana/cat/actions/runs?branch=develop"

    }
}
