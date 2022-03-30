//package org.github.otanikotani.workflow
//
//import com.intellij.openapi.Disposable
//import com.intellij.openapi.components.service
//import com.intellij.openapi.diagnostic.Logger
//import com.intellij.openapi.diagnostic.logger
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.util.Disposer
//import com.intellij.util.ui.UIUtil
//import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
//import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
//import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
//import org.jetbrains.plugins.github.ui.util.DisposingWrapper
//import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
//
//class GitHubWorkflowRunExecutorComponent(private val requestExecutorManager: GithubApiRequestExecutorManager,
//                                         private val project: Project,
//                                         private val remoteUrl: GitRemoteUrlCoordinates,
//                                         val account: GithubAccount,
//                                         parentDisposable: Disposable)
//    : DisposingWrapper(parentDisposable) {
//
//    private val componentFactory by lazy(LazyThreadSafetyMode.NONE) { project.service<GitHubWorkflowRunComponentFactory>() }
//
//    private var requestExecutor: GithubApiRequestExecutor? = null
//
//    init {
//        background = UIUtil.getListBackground()
//        update()
//    }
//
//    private fun update() {
//        LOG.debug("Update")
//        if (requestExecutor != null) return
//        val executor = requestExecutorManager.getExecutor(account)
//        requestExecutor = executor
//        val disposable = Disposer.newDisposable()
//        setContent(componentFactory.createComponent(remoteUrl, account, executor, disposable), disposable)
//    }
//
//    companion object {
//        private val LOG = Logger.getInstance("org.github.otanikotani")
//    }
//}