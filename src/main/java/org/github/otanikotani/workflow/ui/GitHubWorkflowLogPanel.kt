package org.github.otanikotani.workflow.ui

import com.intellij.ui.components.JBTextArea
import net.miginfocom.layout.CC
import net.miginfocom.layout.LC
import net.miginfocom.swing.MigLayout
import org.jetbrains.plugins.github.ui.util.SingleValueModel
import javax.swing.JPanel

class GitHubWorkflowLogPanel(private val model: SingleValueModel<String?>)
    : JPanel() {

    private val log = JBTextArea()

    init {
        isOpaque = false
        layout = MigLayout(LC()
            .fillX()
            .gridGap("0", "0")
            .insets("0", "0", "0", "0"))

        if (model.value.isNullOrBlank()) {
            log.text = "NO LOG"
        } else {
            log.text = model.value
        }

        model.addValueChangedListener {
            if (model.value.isNullOrBlank()) {
                log.text = "NO LOG"
            } else {
                log.text = model.value
            }
        }

        add(log, CC()
            .minWidth("0")
            .spanX(2).growX()
            .wrap())

    }
}
