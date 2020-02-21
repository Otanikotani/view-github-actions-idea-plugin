package org.github.otanikotani.ui.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import git4idea.repo.GitRepository
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import spock.lang.Specification

class ChecksRefresherSpec extends Specification {

    SpyChecksListener spyChecksListener = new SpyChecksListener()
    ChecksLocation location

    def setup() {
        GitRepository gitRepository = Mock(GitRepository)
        Project project = Mock(Project)
        MessageBus messageBus = Mock(MessageBus)
        MessageBusConnection messageBusConnection = Mock(MessageBusConnection)
        messageBus.connect() >> messageBusConnection
        project.getMessageBus() >> messageBus
        gitRepository.getProject() >> project
        GithubAccount account = Mock(GithubAccount)
        location = new ChecksLocation(gitRepository, account)
    }

    def "on account change refresh is triggered"() {
        given:
        ChecksRefresher refresher = new ChecksRefresher(spyChecksListener, location)

    }

    def "on branch change refresh is triggered"() {

    }

    static class SpyChecksListener implements ChecksListener {

        ChecksLocation coordinates

        @Override
        void onRefresh(ChecksLocation coordinates) {
            this.coordinates = coordinates
        }
    }
}
