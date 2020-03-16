package org.github.otanikotani.workflow.ui

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.AnsiEscapeDecoder
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import org.jetbrains.plugins.github.ui.util.SingleValueModel

class GitHubWorkflowRunLogConsole(project: Project,
                                  logModel: SingleValueModel<String?>,
                                  disposable: Disposable) : ConsoleViewImpl(project, true), AnsiEscapeDecoder.ColoredTextAcceptor {

    init {
        val myTextAnsiEscapeDecoder = AnsiEscapeDecoder()
        logModel.addValueChangedListener {
            this.clear()
            if (logModel.value.isNullOrBlank()) {
                this.print("NO LOG", ConsoleViewContentType.NORMAL_OUTPUT)
            } else {
                myTextAnsiEscapeDecoder.escapeText(logModel.value!!, ProcessOutputTypes.STDOUT, this)
            }
        }
        Disposer.register(disposable, Disposable {
            Disposer.dispose(this)
        })
    }

    override fun coloredTextAvailable(text: String, attributes: Key<*>) {
        this.print(text, ConsoleViewContentType.getConsoleViewType(attributes))
    }
}
