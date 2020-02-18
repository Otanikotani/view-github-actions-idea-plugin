package org.github.otanikotani.ui.toolwindow

import javax.swing.Icon
import spock.lang.Specification

class ChecksTableModelSpec extends Specification {

    def "adding a row of a wrong size throws"() {
        given:
        def tableModel = new ChecksTableModel()

        when:
        tableModel.addRow([])

        then:
        thrown(ArrayIndexOutOfBoundsException)
    }

    def "class of the conclusion column should be Icon"() {
        expect:
        new ChecksTableModel().getColumnClass(ChecksTableModel.Columns.Conclusion.index) == Icon
    }
}
