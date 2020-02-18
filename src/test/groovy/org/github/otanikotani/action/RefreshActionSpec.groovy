package org.github.otanikotani.action

import com.intellij.openapi.actionSystem.AnActionEvent
import spock.lang.Specification

class RefreshActionSpec extends Specification {

    def "refresh action has proper text and description"() {
        expect:
        def action = new RefreshAction({})
        action.templatePresentation.text == 'Refresh Checks'
        action.templatePresentation.description == 'Refreshes checks'
    }

    def "refresh action executes the given runnable on action performed"() {
        given:
        boolean executed = false
        def action = new RefreshAction({ executed = true })
        AnActionEvent event = Stub(AnActionEvent)

        when:
        action.actionPerformed(event)

        then:
        executed
    }
}
