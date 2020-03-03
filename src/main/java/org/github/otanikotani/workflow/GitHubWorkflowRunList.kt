package org.github.otanikotani.workflow

import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.ui.ListUtil
import com.intellij.ui.ScrollingUtil
import com.intellij.ui.components.JBList
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ListUiUtil
import com.intellij.util.ui.UIUtil
import net.miginfocom.layout.CC
import net.miginfocom.layout.LC
import net.miginfocom.swing.MigLayout
import org.github.otanikotani.api.GitHubWorkflow
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.action.GitHubWorkflowActionKeys
import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.*

class GitHubWorkflowRunList(model: ListModel<GitHubWorkflowRun>)
    : JBList<GitHubWorkflowRun>(model), DataProvider {

    init {
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION

        val renderer = WorkflowRunsListCellRenderer()
        cellRenderer = renderer
        UIUtil.putClientProperty(this, UIUtil.NOT_IN_HIERARCHY_COMPONENTS, listOf(renderer))

        ScrollingUtil.installActions(this)
    }

    override fun getToolTipText(event: MouseEvent): String? {
        val childComponent = ListUtil.getDeepestRendererChildComponentAt(this, event.point)
        if (childComponent !is JComponent) return null
        return childComponent.toolTipText
    }

    override fun getData(dataId: String): Any? = when {
        PlatformDataKeys.COPY_PROVIDER.`is`(dataId) -> this
        GitHubWorkflowActionKeys.SELECTED_WORKFLOW.`is`(dataId) -> selectedValue
        else -> null
    }

    private inner class WorkflowRunsListCellRenderer : ListCellRenderer<GitHubWorkflowRun>, JPanel() {

        private val title = JLabel()
        private val info = JLabel()

        init {
            border = JBUI.Borders.empty(5, 8)

            layout = MigLayout(LC().gridGap("0", "0")
                .insets("0", "0", "0", "0")
                .fillX())

            val gapAfter = "${JBUI.scale(5)}px"
            add(title, CC()
                .minWidth("pref/2px")
                .gapAfter(gapAfter))
            add(info, CC()
                .minWidth("0px")
                .skip(1)
                .spanX(2))
        }

        override fun getListCellRendererComponent(list: JList<out GitHubWorkflowRun>,
                                                  value: GitHubWorkflowRun,
                                                  index: Int,
                                                  isSelected: Boolean,
                                                  cellHasFocus: Boolean): Component {
            UIUtil.setBackgroundRecursively(this, ListUiUtil.WithTallRow.background(list, isSelected, list.hasFocus()))
            val primaryTextColor = ListUiUtil.WithTallRow.foreground(isSelected, list.hasFocus())
            val secondaryTextColor = ListUiUtil.WithTallRow.secondaryForeground(list, isSelected)

            title.apply {
                text = value.workflow_url
                foreground = primaryTextColor
            }
            info.apply {
                text = "#${value.status} ${value.html_url}"
                foreground = secondaryTextColor
            }
            return this
        }
    }
}