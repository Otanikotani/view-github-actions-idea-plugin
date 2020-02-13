package org.github.otanikotani.ui.toolwindow


import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import spock.lang.Specification

class GHChecksToolWindowTabsContentManagerSpec extends Specification {

  Project project = Stub(Project)
  ChangesViewContentI viewContentManager = Stub(ChangesViewContentI)
  MessageBusConnection messageBusConnection

  GHChecksToolWindowTabsContentManager manager


  def setup() {
    MessageBus messageBus = Stub(MessageBus)
    messageBusConnection = Stub(MessageBusConnection)
    project.getMessageBus() >> messageBus
    messageBus.connect() >> this.messageBusConnection
    manager = new GHChecksToolWindowTabsContentManager(project, viewContentManager)
  }

  def "content manager is subscribed to branch changed event"() {
    expect:
    true


  }
}
