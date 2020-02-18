package org.github.otanikotani.ui.toolwindow


import spock.lang.Specification

class ChecksTabContentPanelTest extends Specification {

    def "drawing a stub when user is not logged in"() {
        given:
        def checksPanel = new ChecksTabContentPanel(null, { -> }, false)

        expect:
        checksPanel.getTable() == null && checksPanel.getStub() != null && checksPanel.getComponentCount() == 2
    }

    def "drawing a table when user is logged in"() {
        given:
        def checksPanel = new ChecksTabContentPanel(null, { -> }, true)

        expect:
        checksPanel.getTable() != null && checksPanel.getStub() == null && checksPanel.getComponentCount() == 2
    }

    def "redrawing panel after authorization"() {
        given:
        def checksPanel = new ChecksTabContentPanel(null, { -> }, false)

        when:
        checksPanel.redraw(true)

        then:
        checksPanel.getTable() != null && checksPanel.getStub() == null && checksPanel.getComponentCount() == 2

        when:
        checksPanel.redraw(false)

        then:
        checksPanel.getTable() == null && checksPanel.getStub() != null && checksPanel.getComponentCount() == 2
    }
}
