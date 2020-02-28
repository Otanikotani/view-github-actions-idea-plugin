package org.github.otanikotani.ui.toolwindow

import javax.swing.Icon
import spock.lang.Specification

class WorkflowsTableModelSpec extends Specification {

    def "adding a row of a wrong size throws"() {
        given:
        def tableModel = new WorkflowRunsTableModel()

        when:
        tableModel.addRow([])

        then:
        thrown(ArrayIndexOutOfBoundsException)
    }

    def "class of the conclusion column should be Icon"() {
        expect:
        new WorkflowRunsTableModel().getColumnClass(WorkflowRunsTableModel.Columns.Conclusion.index) == Icon
    }
}
