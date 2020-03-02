package org.github.otanikotani.workflow.toolwindow

import com.intellij.ui.components.labels.LinkLabel
import java.awt.event.MouseEvent
import javax.swing.JTable
import spock.lang.Specification

class ColumnLinkLabelMouseAdapterSpec extends Specification {

    def "does nothing on mouse click when not the given column selected"() {
        given:
        def table = new JTable(10, 10)
        table.selectionModel.setSelectionInterval(0, 0)
        def adapter = new ColumnLinkLabelMouseAdapter(table, 5)
        def mouseClick = Stub(MouseEvent)

        when:
        adapter.mouseClicked(mouseClick)

        then:
        noExceptionThrown()
    }

    def "does nothing on mouse click when not the value at the given column is not a link"() {
        given:
        def table = new JTable(10, 10)
        table.setColumnSelectionInterval(1, 1)
        table.setRowSelectionInterval(0, 1)
        def adapter = new ColumnLinkLabelMouseAdapter(table, 1)
        def mouseClick = Stub(MouseEvent)

        when:
        adapter.mouseClicked(mouseClick)

        then:
        noExceptionThrown()
    }

    def "clicks the label on selection"() {
        given:
        def table = new JTable(10, 10)
        table.setColumnSelectionInterval(1, 1)
        table.setRowSelectionInterval(0, 1)
        def adapter = new ColumnLinkLabelMouseAdapter(table, 1)
        def mouseClick = Stub(MouseEvent)
        def clicked = false
        LinkLabel linkLabel = new LinkLabel() {
            @Override
            void doClick() {
                clicked = true
            }
        }

        table.setValueAt(linkLabel, 0, 1)

        when:
        adapter.mouseClicked(mouseClick)

        then:
        clicked
    }
}
