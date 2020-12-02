package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.UIUtil
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.authentication.ui.GithubChooseAccountDialog
import org.jetbrains.plugins.github.ui.util.DisposingWrapper
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import org.jetbrains.plugins.github.util.GithubUIUtil

internal class GitHubWorkflowRunAccountsComponent(private val authManager: GithubAuthenticationManager,
                                                  private val project: Project,
                                                  private val remoteUrl: GitRemoteUrlCoordinates,
                                                  parentDisposable: Disposable)
    : DisposingWrapper(parentDisposable) {

    private val requestExecutorManager by lazy(LazyThreadSafetyMode.NONE) { GithubApiRequestExecutorManager.getInstance() }
    private var selectedAccount: GithubAccount? = null

    init {
        LOG.debug("init GitHubWorkflowRunAccountsComponent")
        background = UIUtil.getListBackground()
        update()
    }

    private fun update() {
        LOG.debug("update")

        if (selectedAccount != null) {
            LOG.debug("No account selected - do nothing")
            return
        }

        val accounts = authManager.getAccounts().filter { it.server.matches(remoteUrl.url) }

        if (accounts.size == 1) {
            LOG.debug("Set actual account to the account using the ${remoteUrl.url}")
            setActualContent(accounts.single())
            return
        }

        val defaultAccount = accounts.find { it == authManager.getDefaultAccount(project) }
        if (defaultAccount != null) {
            LOG.debug("Set actual account to default account")
            setActualContent(defaultAccount)
            return
        }

        if (accounts.isNotEmpty()) {
            showChooseAccountPanel(accounts)
        } else {
            showLoginPanel()
        }
    }

    private fun showLoginPanel() {
        LOG.debug("Show login panel")
        setCenteredContent(GithubUIUtil.createNoteWithAction(::update).apply {
            append("Open Pull Requests tab to Log in and then ", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            append("refresh", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, Runnable { update() })
        })
    }

    private fun showChooseAccountPanel(accounts: List<GithubAccount>) {
        LOG.debug("Show choose account panel")
        setCenteredContent(GithubUIUtil.createNoteWithAction { chooseAccount(accounts) }.apply {
            append("Select", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, Runnable { chooseAccount(accounts) })
            append(" GitHub account to view workflows", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        })
    }

    private fun chooseAccount(accounts: List<GithubAccount>) {
        LOG.debug("Choose account")
        val dialog = GithubChooseAccountDialog(project, null, accounts, null, true, true)
        if (dialog.showAndGet()) {
            setActualContent(dialog.account)
            IdeFocusManager.getInstance(project).requestFocusInProject(this@GitHubWorkflowRunAccountsComponent, project)
        }
    }

    private fun setActualContent(account: GithubAccount) {
        selectedAccount = account
        val disposable = Disposer.newDisposable()
        setContent(GitHubWorkflowRunExecutorComponent(requestExecutorManager, project, remoteUrl, account, disposable), disposable)
    }

    companion object {
        private val LOG = Logger.getInstance("org.github.otanikotani")
    }
}