package org.github.otanikotani.workflow

import net.miginfocom.layout.CC
import net.miginfocom.layout.LC
import net.miginfocom.swing.MigLayout
import org.jetbrains.plugins.github.ui.util.SingleValueModel
import javax.swing.JLabel
import javax.swing.JPanel

class GitHubWorkflowLogPanel(private val model: SingleValueModel<String?>)
    : JPanel() {

    private val log = JLabel()

    init {
        isOpaque = false
        layout = MigLayout(LC()
            .fillX()
            .gridGap("0", "0")
            .insets("0", "0", "0", "0"))

        log.text = model.value ?: "NO LOG"

        model.addValueChangedListener {
            log.text = model.value ?: "NO LOG"
        }

        add(log, CC()
            .minWidth("0")
            .spanX(2).growX()
            .wrap())

    }
}
