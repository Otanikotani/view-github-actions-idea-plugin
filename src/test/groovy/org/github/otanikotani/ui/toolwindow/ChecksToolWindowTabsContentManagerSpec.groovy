package org.github.otanikotani.ui.toolwindow

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.BranchChangeListener
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import git4idea.repo.GitRepository
import org.github.otanikotani.GithubAccountManager
import org.github.otanikotani.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import spock.lang.Specification

class ChecksToolWindowTabsContentManagerSpec extends Specification {

    SimpleChecksContext context = new SimpleChecksContext()

    Project project = Stub(Project)
    GitRepository repository = Stub(GitRepository)
    MessageBusConnection messageBusConnection
    GithubApiRequestExecutor executor = Mock(GithubApiRequestExecutor)

    ChecksToolWindowTabsContentManager manager
    private Content content = Mock(Content)


    def setup() {
        context.project = Mock(Project)
        context.actionManager = Optional.ofNullable(Mock(ActionManager))
        context.changesViewContentManager = Mock(ChangesViewContentI)
        context.application = Mock(Application)
        context.application.isDispatchThread() >> true
        context.githubAccountManager = Mock(GithubAccountManager)
        context.contentFactory = Mock(ContentFactory)
        context.contentFactory.createContent(_, _, _) >> content
        context.executor = executor

        manager = new ChecksToolWindowTabsContentManager(context, repository)
    }

    def "on account change - update task is queued"() {
        when:
        manager.onGithubAccountChange(Mock(GithubAccount))

        then:
        1 * executor.queue(_)
    }
}
