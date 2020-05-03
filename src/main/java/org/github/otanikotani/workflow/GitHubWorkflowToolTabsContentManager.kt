package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentProvider
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.annotations.Nls
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import javax.swing.JPanel

class GitHubWorkflowToolTabsContentManager(private val project: Project,
                                           private val viewContentManager: ChangesViewContentI) {

    @CalledInAwt
    internal fun addTab(remoteUrl: GitRemoteUrlCoordinates, onDispose: Disposable) {
        viewContentManager.addContent(createContent(remoteUrl, onDispose))
    }

    @CalledInAwt
    fun focusTab(remoteUrl: GitRemoteUrlCoordinates) {
        val content = viewContentManager.findContents { it.remoteUrl == remoteUrl }.firstOrNull() ?: return
        ToolWindowManager.getInstance(project).getToolWindow(ChangesViewContentManager.TOOLWINDOW_ID)?.show {
            viewContentManager.setSelectedContent(content, true)
        }
    }

    @CalledInAwt
    internal fun removeTab(remoteUrl: GitRemoteUrlCoordinates) {
        val content = viewContentManager.findContents { it.remoteUrl == remoteUrl }.firstOrNull() ?: return
        viewContentManager.removeContent(content)
    }

    private fun createContent(remoteUrl: GitRemoteUrlCoordinates, onDispose: Disposable): Content {
        val disposable = Disposer.newDisposable()
        Disposer.register(disposable, onDispose)

        val content = ContentFactory.SERVICE.getInstance().createContent(JPanel(null), GROUP_PREFIX, false)
        content.isCloseable = true
        content.setDisposer(disposable)
        content.description = GROUP_PREFIX
        content.remoteUrl = remoteUrl
        content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY, ChangesViewContentManager.TabOrderWeight.LAST.weight)
        content.putUserData(ChangesViewContentManager.CONTENT_PROVIDER_SUPPLIER_KEY) {
            object : ChangesViewContentProvider {
                override fun initContent(): GitHubWorkflowRunAccountsComponent {
                    return GitHubWorkflowRunAccountsComponent(GithubAuthenticationManager.getInstance(), project, remoteUrl, disposable)
                }

                override fun disposeContent() = Disposer.dispose(disposable)
            }
        }
        return content
    }

    private var Content.remoteUrl
        get() = getUserData(REMOTE_URL)
        set(value) {
            putUserData(REMOTE_URL, value)
        }

    companion object {
        @Nls
        private const val GROUP_PREFIX = "Workflows"

        private val REMOTE_URL = Key<GitRemoteUrlCoordinates>("GHWORKFLOW_REMOTE_URL")
    }
}