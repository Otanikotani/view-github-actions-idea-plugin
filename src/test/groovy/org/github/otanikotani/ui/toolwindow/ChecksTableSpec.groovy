package org.github.otanikotani.ui.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ui.components.labels.LinkLabel
import java.awt.Color
import java.awt.Component
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JTable
import org.github.otanikotani.api.GithubCheckRun
import spock.lang.Specification

class ChecksTableSpec extends Specification {

    def "removing all rows is delegated to the model"() {
        given:
        ChecksTableModel model = new ChecksTableModel()
        model.addRow(["foo", AllIcons.General.Error, "started", "completed", new LinkLabel("any label", AllIcons.General.Error)])
        ChecksTable panel = new ChecksTable(model)

        when:
        panel.removeAllRows()

        then:
        model.rowCount == 0
    }

    def "adding a row"() {
        given:
        ChecksTableModel model = new ChecksTableModel()
        ChecksTable panel = new ChecksTable(model)
        def checkRun = new GithubCheckRun()
        checkRun.name = "my run"
        checkRun.conclusion = "unknown"
        checkRun.started_at = new Date()
        checkRun.completed_at = new Date()
        checkRun.id = 123L

        when:
        panel.addRows("trinidata", "tabahamas", [checkRun])

        then:
        model.rowCount == 1
        model.getValueAt(0, 0) == "my run"
    }

    def "to url returns check run url when check is given"() {
        given:
        def checkRun = new GithubCheckRun()
        checkRun.name = "my run"
        checkRun.conclusion = "unknown"
        checkRun.started_at = new Date()
        checkRun.completed_at = new Date()
        checkRun.id = 123L

        when:
        String url = ChecksTable.toUrl("trini", "taba", checkRun)

        then:
        url == "https://github.com/trini/taba/runs/123"
    }

    def "to url returns check run url when checkrun id is not present"() {
        given:
        def checkRun = new GithubCheckRun()
        checkRun.name = "my run"
        checkRun.conclusion = "unknown"
        checkRun.started_at = new Date()
        checkRun.completed_at = new Date()
        checkRun.id = null

        when:
        String url = ChecksTable.toUrl("trini", "taba", checkRun)

        then:
        url == "https://github.com/trini/taba/runs"
    }

    def "conclusions to icons converts string conclusion to the icon"(String conclusion, Icon icon) {
        expect:
        ChecksTable.conclusionToIcons(conclusion) == icon

        where:
        conclusion    | icon
        "in_progress" | AllIcons.Process.Step_mask
        "success"     | AllIcons.Actions.Checked
        null          | AllIcons.Process.Step_mask
        "unknown"     | AllIcons.General.Error
    }

    def "refresh replace current rows with the new ones"() {
        given:
        ChecksTableModel model = new ChecksTableModel()
        ChecksTable panel = new ChecksTable(model)
        def checkRun = new GithubCheckRun()
        checkRun.name = "my run"
        checkRun.conclusion = "unknown"
        checkRun.started_at = new Date()
        checkRun.completed_at = new Date()
        checkRun.id = 123L

        when:
        panel.addRows("trinidata", "tabahamas", [checkRun])
        panel.refresh("anoth", "er", [checkRun])

        then:
        model.rowCount == 1
        model.getValueAt(0, 0) == "my run"
    }

    def "icon table cell renderer is aligned to center"() {
        expect:
        new ChecksTable.SimpleIconTableCellRenderer().centerAlignment
    }

    def "icon table cell renderer uses the given value as an icon"() {
        given:
        Icon icon = AllIcons.General.Error
        JTable table = new JTable(0, 0)

        when:
        def result = new ChecksTable.SimpleIconTableCellRenderer().getIcon(icon, table, 1)

        then:
        result == icon
    }

    def "icon table cell renderer component sets the background of the component to the selection background when selected"() {
        given:
        Icon value = AllIcons.General.Error
        JTable table = new JTable(0, 0)
        table.setSelectionBackground(Color.BLACK)

        when:
        Component result = new ChecksTable.SimpleIconTableCellRenderer().getTableCellRendererComponent(table, value, true, false, 0, 0)

        then:
        result.getBackground() == Color.BLACK
    }

    def "table cell cell renderer component renders cells with no text"() {
        given:
        Icon value = AllIcons.General.Error
        JTable table = new JTable(0, 0)

        when:
        Component result = new ChecksTable.SimpleIconTableCellRenderer().getTableCellRendererComponent(table, value, true, false, 0, 0)

        then:
        result instanceof JLabel
        ((JLabel) result).text == ""
    }

    def "link table cell renderer is a simple proxy returning given value"() {
        given:
        def linkLabel = new LinkLabel()
        JTable table = new JTable(0, 0)


        when:
        Component result = new ChecksTable.LinkTableCellRenderer().getTableCellRendererComponent(table, linkLabel, true, false, 0, 0)

        then:
        result == linkLabel
    }
}
