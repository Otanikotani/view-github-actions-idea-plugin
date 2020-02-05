package org.github.otanikotani.ui.toolwindow;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class ChecksTableModel extends DefaultTableModel {

  private static final List<String> COLUMN_NAMES = Arrays.asList("Id", "Name", "Conclusion", "Started At", "Completed At");

  @SuppressWarnings("unchecked")
  public ChecksTableModel() {
    columnIdentifiers.addAll(COLUMN_NAMES);
  }

  public void addRow(List<String> row) {
    if (row.size() != COLUMN_NAMES.size()) {
      throw new ArrayIndexOutOfBoundsException("Row " + row + " should have " + COLUMN_NAMES.size() +
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
  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
