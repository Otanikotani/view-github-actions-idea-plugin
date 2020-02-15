package org.github.otanikotani.ui.toolwindow

import spock.lang.Specification

import javax.swing.Icon

class ChecksTableModelSpec extends Specification {

  def "adding a row of a wrong size throws"() {
    given:
    def tableModel = new ChecksTableModel()

    when:
    tableModel.addRow([])
    
    then:
    thrown(ArrayIndexOutOfBoundsException)
  }

  def "class of the conclusion column is Icon"() {
    expect:
    new ChecksTableModel().getColumnClass(ChecksTableModel.Columns.Conclusion.index) == Icon
  }
}
