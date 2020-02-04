package org.github.otanikotani.ui.toolwindow;

import com.intellij.ui.table.JBTable;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import lombok.Getter;
import org.github.otanikotani.api.GithubCheckRun;
import org.ocpsoft.prettytime.PrettyTime;

@Getter
public class ChecksPanel extends JPanel {

  private ChecksTableModel tableModel;

  public ChecksPanel() {
    super(new GridLayout(1, 0));
    tableModel = new ChecksTableModel();
    final JBTable table = new JBTable(tableModel);
    table.setRowSelectionAllowed(false);
    setLayout(new BorderLayout());
    add(table.getTableHeader(), BorderLayout.PAGE_START);
    add(table, BorderLayout.CENTER);
  }

  public void removeAllRows() {
    tableModel.removeAllRows();
  }

  public void addRows(List<? extends GithubCheckRun> checkRuns) {
    PrettyTime prettyTime = new PrettyTime();
    for (GithubCheckRun checkRun : checkRuns) {
      String id = String.valueOf(checkRun.getId());
      String name = checkRun.getName();
      String conclusion = conclusionToIcons(checkRun.getConclusion());
      String startedAt = prettyTime.format(checkRun.getStarted_at());
      String completedAt = prettyTime.format(checkRun.getCompleted_at());
      tableModel.addRow(Arrays.asList(id, name, conclusion, startedAt, completedAt));
    }
  }

  private String conclusionToIcons(String conclusion) {
    if (conclusion == null) {
      return "⌛";
    }
    if (conclusion.equals("in_progress")) {
      return "⌛";
    } else if (conclusion.equals("success")) {
      return "✓";
    } else {
      return "❌";
    }
  }
}
