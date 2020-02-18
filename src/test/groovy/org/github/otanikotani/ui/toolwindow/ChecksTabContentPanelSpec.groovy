package org.github.otanikotani.ui.toolwindow

import javax.swing.JComponent
import javax.swing.JPanel
import spock.lang.Specification

class ChecksTabContentPanelSpec extends Specification {

    static final JComponent EMPTY_COMPONENT = new JPanel(null)

    def "drawing a stub when user is not logged in"() {
        given:
        def checksPanel = new ChecksTabContentPanel(EMPTY_COMPONENT, false)

        expect:
        checksPanel.table == null
        checksPanel.stub != null
        checksPanel.componentCount == 2
    }

    def "drawing a table when user is logged in"() {
        given:
        def checksPanel = new ChecksTabContentPanel(EMPTY_COMPONENT, true)

        expect:
        checksPanel.table != null
        checksPanel.stub == null
        checksPanel.componentCount == 2
    }

    def "redrawing panel with authorization"() {
        given:
        def checksPanel = new ChecksTabContentPanel(EMPTY_COMPONENT, false)

        when:
        checksPanel.redraw(true)

        then:
        checksPanel.table != null
        checksPanel.stub == null
        checksPanel.componentCount == 2
    }

    def "redrawing panel when no authorization"() {
        given:
        def checksPanel = new ChecksTabContentPanel(EMPTY_COMPONENT, false)

        when:
        checksPanel.redraw(false)

        then:
        checksPanel.table == null
        checksPanel.stub != null
        checksPanel.componentCount == 2
    }
}
