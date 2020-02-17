package org.github.otanikotani.ui.toolwindow;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.General;
import com.intellij.icons.AllIcons.Ide;
import com.intellij.icons.AllIcons.Process;
import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.table.IconTableCellRenderer;
import org.github.otanikotani.api.GithubCheckRun;
import org.github.otanikotani.ui.toolwindow.ChecksTableModel.Columns;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ocpsoft.prettytime.PrettyTime;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class ChecksTable extends JPanel {

    private static final DefaultTableCellRenderer ICON_RENDERER = new SimpleIconTableCellRenderer();
    private static final PrettyTime PRETTY_TIME = new PrettyTime();

    private ChecksTableModel tableModel;

    public ChecksTable(ChecksTableModel tableModel) {
        this.tableModel = tableModel;
        final JBTable table = new JBTable(tableModel);
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn conclusionColumn = columnModel.getColumn(Columns.Conclusion.index);
        conclusionColumn.setCellRenderer(ICON_RENDERER);
        TableColumn urlColumn = columnModel.getColumn(Columns.Url.index);
        urlColumn.setCellRenderer(new LinkTableCellRenderer());
        table.addMouseListener(new ColumnLinkLabelMouseAdapter(table, Columns.Url.index));
        table.setRowSelectionAllowed(false);
        setLayout(new BorderLayout());
        add(table.getTableHeader(), BorderLayout.PAGE_START);
        add(table, BorderLayout.CENTER);
    }

    @SuppressWarnings("SameParameterValue")
    static String makeTimePretty(Date date, PrettyTime prettyTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);
        return prettyTime.format(Date.from(zonedDateTime.toInstant()));
    }

    static String toUrl(String owner, String repo, GithubCheckRun checkRun) {
        if (checkRun == null || checkRun.getId() == null) {
            return String.format("https://github.com/%s/%s/runs", owner, repo);
        }
        return String.format("https://github.com/%s/%s/runs/%d", owner, repo, checkRun.getId());
    }

    static Icon conclusionToIcons(String conclusion) {
        if (conclusion == null) {
            return Process.Step_mask;
        }
        if (conclusion.equals("in_progress")) {
            return Process.Step_mask;
        } else if (conclusion.equals("success")) {
            return Actions.Checked;
        } else {
            return General.Error;
        }
    }

    public void removeAllRows() {
        tableModel.removeAllRows();
    }

    public void addRows(String owner, String repo, List<? extends GithubCheckRun> checkRuns) {
        for (GithubCheckRun checkRun : checkRuns) {
            String name = checkRun.getName();
            Icon conclusion = conclusionToIcons(checkRun.getConclusion());
            String startedAt = makeTimePretty(checkRun.getStarted_at(), PRETTY_TIME);
            String completedAt = makeTimePretty(checkRun.getCompleted_at(), PRETTY_TIME);

            String url = toUrl(owner, repo, checkRun);
            LinkLabel<?> urlLabel = new LinkLabel<>(
                url,
                Ide.External_link_arrow,
                (_0, _1) -> BrowserUtil.browse(url));
            tableModel.addRow(Arrays.asList(name, conclusion, startedAt, completedAt, urlLabel));
        }
    }

    public void refresh(String owner, String repo, List<? extends GithubCheckRun> checkRuns) {
        removeAllRows();
        addRows(owner, repo, checkRuns);
    }

    static class SimpleIconTableCellRenderer extends IconTableCellRenderer<Icon> {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                                                       int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, false, focus, row, column);
            Color bg = selected ? table.getSelectionBackground() : table.getBackground();
            component.setBackground(bg);
            ((JLabel) component).setText("");
            return component;
        }

        @Nullable
        @Override
        protected Icon getIcon(@NotNull Icon value, JTable table, int row) {
            return value;
        }

        @Override
        protected boolean isCenterAlignment() {
            return true;
        }
    }

    static class LinkTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus,
                                                       int row, int column) {
            return (LinkLabel<?>) value;
        }
    }
}
