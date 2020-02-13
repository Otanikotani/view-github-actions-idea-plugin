package org.github.otanikotani.ui.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ui.components.labels.LinkLabel
import spock.lang.Specification

import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JTable
import java.awt.Color
import java.awt.Component


class ChecksPanelSpec extends Specification {



  def "icon table cell renderer is aligned to center"() {
    expect:
    new ChecksPanel.SimpleIconTableCellRenderer().centerAlignment
  }

  def "icon table cell renderer uses the given value as an icon"() {
    given:
    Icon icon = AllIcons.General.Error
    JTable table = new JTable(0, 0)

    when:
    def result = new ChecksPanel.SimpleIconTableCellRenderer().getIcon(icon, table, 1)

    then:
    result == icon
  }

  def "icon table cell renderer component sets the background of the component to the selection background when selected"() {
    given:
    Icon value = AllIcons.General.Error
    JTable table = new JTable(0, 0)
    table.setSelectionBackground(Color.BLACK)

    when:
    Component result = new ChecksPanel.SimpleIconTableCellRenderer().getTableCellRendererComponent(table, value, true, false, 0, 0)

    then:
    result.getBackground() == Color.BLACK
  }

  def "table cell cell renderer component renders cells with no text"() {
    given:
    Icon value = AllIcons.General.Error
    JTable table = new JTable(0, 0)

    when:
    Component result = new ChecksPanel.SimpleIconTableCellRenderer().getTableCellRendererComponent(table, value, true, false, 0, 0)

    then:
    result instanceof JLabel
    ((JLabel)result).text == ""
  }

  def "link table cell renderer is a simple proxy returning given value"() {
    given:
    def linkLabel = new LinkLabel()
    JTable table = new JTable(0, 0)


    when:
    Component result = new ChecksPanel.LinkTableCellRenderer().getTableCellRendererComponent(table, linkLabel, true, false, 0, 0)

    then:
    result == linkLabel
  }

}
