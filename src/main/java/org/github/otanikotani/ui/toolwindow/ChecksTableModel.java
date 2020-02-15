package org.github.otanikotani.ui.toolwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;

public class ChecksTableModel extends DefaultTableModel {

  @SuppressWarnings("unchecked")
  public ChecksTableModel() {
    columnIdentifiers.addAll(Columns.getColumns());
  }

  public void addRow(List<Object> row) {
    if (row.size() != Columns.getColumns().size()) {
      throw new ArrayIndexOutOfBoundsException("Row " + row + " should have " + Columns.getColumns().size() +
        " elements, but got " + row.size());
    }
    super.addRow(new Vector<>(row));
  }

  public void removeAllRows() {
    while (getRowCount() > 0) {
      removeRow(getRowCount() - 1);
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == Columns.Conclusion.index) {
      return Icon.class;
    }
    return super.getColumnClass(columnIndex);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  enum Columns {
    Name,
    Conclusion,
    StartedAt("Started At"),
    CompletedAt("Completed At"),
    Url;

    final int index;
    final String name;

    Columns() {
      this.index = ordinal();
      this.name = name();
    }

    Columns(String name) {
      this.index = ordinal();
      this.name = name;
    }

    static List<String> getColumns() {
      List<String> result = new ArrayList<>(values().length);
      for (Columns value : values()) {
        result.add(value.name);
      }
      return result;
    }
  }
}
