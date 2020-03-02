package org.github.otanikotani.workflow.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import git4idea.repo.GitRepository
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import spock.lang.Specification

class WorkflowsRefresherSpec extends Specification {

    SpyWorkflowsListener listener = new SpyWorkflowsListener()
    WorkflowsLocation location

    def setup() {
        GitRepository gitRepository = Mock(GitRepository)
        Project project = Mock(Project)
        MessageBus messageBus = Mock(MessageBus)
        MessageBusConnection messageBusConnection = Mock(MessageBusConnection)
        messageBus.connect() >> messageBusConnection
        project.getMessageBus() >> messageBus
        gitRepository.getProject() >> project
        GithubAccount account = Mock(GithubAccount)
        location = new WorkflowsLocation(gitRepository, account)
    }

    def "on branch change refresh is triggered"() {

    }

    static class SpyWorkflowsListener implements WorkflowsListener {

        WorkflowsLocation coordinates

        @Override
        void onLocationChange(WorkflowsLocation coordinates) {
            this.coordinates = coordinates
        }
    }
}
