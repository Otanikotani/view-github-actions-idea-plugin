package org.github.otanikotani.ui.toolwindow

import javax.swing.JComponent
import javax.swing.JPanel
import spock.lang.Specification

class WorkflowsTabContentPanelSpec extends Specification {

    static final JComponent EMPTY_COMPONENT = new JPanel(null)

    def "drawing a stub when user is not logged in"() {
        given:
        def panel = new WorkflowsTabContentPanel(EMPTY_COMPONENT, false)

        expect:
        panel.table == null
        panel.stub != null
        panel.componentCount == 2
    }

    def "drawing a table when user is logged in"() {
        given:
        def panel = new WorkflowsTabContentPanel(EMPTY_COMPONENT, true)

        expect:
        panel.table != null
        panel.stub == null
        panel.componentCount == 2
    }

    def "redrawing panel with authorization"() {
        given:
        def panel = new WorkflowsTabContentPanel(EMPTY_COMPONENT, false)

        when:
        panel.redraw(true)

        then:
        panel.table != null
        panel.stub == null
        panel.componentCount == 2
    }

    def "redrawing panel when no authorization"() {
        given:
        def panel = new WorkflowsTabContentPanel(EMPTY_COMPONENT, false)

        when:
        panel.redraw(false)

        then:
        panel.table == null
        panel.stub != null
        panel.componentCount == 2
    }
}
