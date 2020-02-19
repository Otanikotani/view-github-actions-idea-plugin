package org.github.otanikotani.ui.toolwindow

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.BranchChangeListener
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import spock.lang.Specification

class ChecksToolWindowTabsContentManagerSpec extends Specification {

    SimpleChecksContext context = new SimpleChecksContext()

    Project project = Stub(Project)
    ChangesViewContentI viewContentManager = Stub(ChangesViewContentI)
    MessageBusConnection messageBusConnection

    ChecksToolWindowTabsContentManager manager
    private ActionManager actionManager = Stub(ActionManager)


    def setup() {
        MessageBus messageBus = Stub(MessageBus)
        messageBusConnection = Mock(MessageBusConnection)
        project.getMessageBus() >> messageBus
        messageBus.connect() >> this.messageBusConnection
    }

    def "account changed subscription"() {
        when:
        manager = new ChecksToolWindowTabsContentManager(context)
        
        then:
        1 * messageBusConnection.subscribe(ChecksToolWindowTabsContentManager.ACCOUNT_CHANGED_TOPIC, _)
    }

    def "branch change subscription"() {
        when:
        manager = new ChecksToolWindowTabsContentManager(context)

        then:
        1 * messageBusConnection.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, _)
    }

    def "checks refreshed subscription"() {
        when:
        manager = new ChecksToolWindowTabsContentManager(context)

        then:
        1 * messageBusConnection.subscribe(ChecksRefresher.ChecksRefreshedListener.CHECKS_REFRESHED, _)
    }
}
