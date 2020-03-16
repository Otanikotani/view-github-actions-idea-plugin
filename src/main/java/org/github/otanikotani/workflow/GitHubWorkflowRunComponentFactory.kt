package org.github.otanikotani.workflow

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.DataManager
import com.intellij.ide.actions.RefreshAction
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.*
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.jetbrains.rd.util.string.println
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.action.GitHubWorkflowRunActionKeys
import org.github.otanikotani.workflow.data.GitHubWorkflowRunDataProvider
import org.github.otanikotani.workflow.ui.GitHubWorkflowRunList
import org.github.otanikotani.workflow.ui.GitHubWorkflowRunListLoaderPanel
import org.github.otanikotani.workflow.ui.GitHubWorkflowRunLogConsole
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.pullrequest.config.GithubPullRequestsProjectUISettings
import org.jetbrains.plugins.github.pullrequest.ui.GHCompletableFutureLoadingModel
import org.jetbrains.plugins.github.pullrequest.ui.GHLoadingModel
import org.jetbrains.plugins.github.pullrequest.ui.GHLoadingPanel
import org.jetbrains.plugins.github.ui.util.SingleValueModel
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import org.jetbrains.plugins.github.util.LazyCancellableBackgroundProcessValue
import java.awt.BorderLayout
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JComponent
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.event.ListSelectionEvent

@Service
internal class GitHubWorkflowRunComponentFactory(private val project: Project) {

    private val progressManager = ProgressManager.getInstance()
    private val actionManager = ActionManager.getInstance()
    private val copyPasteManager = CopyPasteManager.getInstance()

    private val projectUiSettings = GithubPullRequestsProjectUISettings.getInstance(project)
    private val dataContextRepository = GitHubWorkflowDataContextRepository.getInstance(project)

    @CalledInAwt
    fun createComponent(remoteUrl: GitRemoteUrlCoordinates, account: GithubAccount, requestExecutor: GithubApiRequestExecutor,
                        parentDisposable: Disposable): JComponent {

        val contextDisposable = Disposer.newDisposable()
        val contextValue = object : LazyCancellableBackgroundProcessValue<GitHubWorkflowRunDataContext>(progressManager) {
            override fun compute(indicator: ProgressIndicator) =
                dataContextRepository.getContext(account, requestExecutor, remoteUrl).also {
                    Disposer.register(contextDisposable, it)
                }
        }
        Disposer.register(parentDisposable, contextDisposable)
        Disposer.register(parentDisposable, Disposable { contextValue.drop() })

        val uiDisposable = Disposer.newDisposable()
        Disposer.register(parentDisposable, uiDisposable)

        val loadingModel = GHCompletableFutureLoadingModel<GitHubWorkflowRunDataContext>()
        val contentContainer = JBPanelWithEmptyText(null).apply {
            background = UIUtil.getListBackground()
        }
        loadingModel.addStateChangeListener(object : GHLoadingModel.StateChangeListener {
            override fun onLoadingCompleted() {
                val dataContext = loadingModel.result
                if (dataContext != null) {
                    var content = createContent(dataContext, uiDisposable)
                    if (Registry.`is`("show.log.as.editor.tab")) {
                        content = patchContent(content)
                    }

                    with(contentContainer) {
                        layout = BorderLayout()
                        add(content, BorderLayout.CENTER)
                        validate()
                        repaint()
                    }
                }
            }
        })
        loadingModel.future = contextValue.value

        return GHLoadingPanel(loadingModel, contentContainer, uiDisposable,
            GHLoadingPanel.EmptyTextBundle.Simple("", "Can't load data from GitHub")).apply {
            resetHandler = ActionListener {
                contextValue.drop()
                loadingModel.future = contextValue.value
            }
        }
    }

    private fun patchContent(content: JComponent): JComponent {
        var patchedContent = content
        val onePixelSplitter = patchedContent as OnePixelSplitter
        val splitter = onePixelSplitter.secondComponent as Splitter
        patchedContent = splitter.secondComponent

        onePixelSplitter.secondComponent = splitter.firstComponent
        return patchedContent
    }

    private fun createContent(context: GitHubWorkflowRunDataContext, disposable: Disposable): JComponent {
        val listSelectionHolder = GitHubWorkflowRunListSelectionHolder()
        val workflowRunsList = createWorkflowRunsListComponent(context, listSelectionHolder, disposable)

        val dataProviderModel = createDataProviderModel(context, listSelectionHolder, disposable)

        val logLoadingModel = createLogLoadingModel(dataProviderModel, disposable)
        val logModel = createValueModel(logLoadingModel)

        val logPanel = createLogPanel(context, logModel, disposable)
        val logLoadingPanel = GHLoadingPanel(logLoadingModel, logPanel, disposable,
            GHLoadingPanel.EmptyTextBundle.Simple("Select workflow run to see the log",
                "Can't load log")).apply {
//            errorHandler = GHLoadingErrorHandlerImpl() { dataProviderModel.value?.reloadWorkflowRun() }
        }

        val selectionDataContext = GitHubWorkflowRunSelectionContext(context, listSelectionHolder)

        return OnePixelSplitter("GitHub.Workflows.Component", 0.5f).apply {
            background = UIUtil.getListBackground()
            isOpaque = true
            isFocusCycleRoot = true
            firstComponent = workflowRunsList
            secondComponent = logLoadingPanel
//                .also {
//                (actionManager.getAction("GitHub.Workflow.Details.Reload") as RefreshAction).registerCustomShortcutSet(it, disposable)
//            }
        }.also {
            //            changesBrowser.diffAction.registerCustomShortcutSet(it, disposable)
            DataManager.registerDataProvider(it) { dataId ->
                if (Disposer.isDisposed(disposable)) null
                else when {
                    GitHubWorkflowRunActionKeys.ACTION_DATA_CONTEXT.`is`(dataId) -> selectionDataContext
                    else -> null
                }

            }
        }
    }

    private fun createLogPanel(context: GitHubWorkflowRunDataContext, logModel: SingleValueModel<String?>, disposable: Disposable): JBPanelWithEmptyText {
        val console = GitHubWorkflowRunLogConsole(project, logModel, disposable)

        val panel = JBPanelWithEmptyText(BorderLayout()).apply {
            isOpaque = false
            add(console.component, BorderLayout.CENTER)
        }
        logModel.addValueChangedListener {
            panel.validate()
        }
        return panel
    }

    private fun createWorkflowRunsListComponent(context: GitHubWorkflowRunDataContext,
                                                listSelectionHolder: GitHubWorkflowRunListSelectionHolder,
                                                disposable: Disposable): JComponent {

        val list = GitHubWorkflowRunList(context.listModel).apply {
            emptyText.clear()
        }.also {
            it.addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent?) {
                    if (it.selectedIndex < 0 && !it.isEmpty) it.selectedIndex = 0
                }

                override fun focusLost(e: FocusEvent?) {}
            })

            installPopup(it)
            installWorkflowRunSelectionSaver(it, listSelectionHolder)
        }

        //Cannot seem to have context menu, when right click, why?
        val listReloadAction = actionManager.getAction("Github.Workflow.Run.List.Reload") as RefreshAction

        return GitHubWorkflowRunListLoaderPanel(context.listLoader, listReloadAction, list).apply {
            errorHandler = GitHubLoadingErrorHandler {
                context.listLoader.reset()
            }
        }.also {
            listReloadAction.registerCustomShortcutSet(it, disposable)
            Disposer.register(disposable, Disposable {
                Disposer.dispose(it)
            })
        }
    }

//    private fun createDetailsPanel(dataContext: GHWorkflowDataContext,
//                                   detailsModel: SingleValueModel<GHPullRequest?>,
//                                   avatarIconsProviderFactory: CachingGithubAvatarIconsProvider.Factory,
//                                   parentDisposable: Disposable): JBPanelWithEmptyText {
//        val metaPanel = GHPRMetadataPanel(project, detailsModel,
//            dataContext.securityService,
//            dataContext.busyStateTracker,
//            dataContext.metadataService,
//            avatarIconsProviderFactory).apply {
//            border = JBUI.Borders.empty(4, 8, 4, 8)
//        }.also {
//            Disposer.register(parentDisposable, it)
//        }
//
//        val descriptionPanel = GHPRDescriptionPanel(detailsModel).apply {
//            border = JBUI.Borders.empty(4, 8, 8, 8)
//        }
//
//        val scrollablePanel = ScrollablePanel(VerticalFlowLayout(0, 0)).apply {
//            isOpaque = false
//            add(metaPanel)
//            add(descriptionPanel)
//        }
//        val scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true).apply {
//            viewport.isOpaque = false
//            isOpaque = false
//        }.also {
//            val actionGroup = actionManager.getAction("Github.PullRequest.Details.Popup") as ActionGroup
//            PopupHandler.installPopupHandler(it, actionGroup, ActionPlaces.UNKNOWN, actionManager)
//        }
//
//        scrollPane.isVisible = detailsModel.value != null
//
//        detailsModel.addValueChangedListener {
//            scrollPane.isVisible = detailsModel.value != null
//        }
//
//        return JBPanelWithEmptyText(BorderLayout()).apply {
//            isOpaque = false
//
//            add(scrollPane, BorderLayout.CENTER)
//        }
//    }

    private fun installWorkflowRunSelectionSaver(list: GitHubWorkflowRunList, listSelectionHolder: GitHubWorkflowRunListSelectionHolder) {
        var savedSelection: GitHubWorkflowRun? = null

        list.selectionModel.addListSelectionListener { e: ListSelectionEvent ->
            if (!e.valueIsAdjusting) {
                val selectedIndex = list.selectedIndex
                if (selectedIndex >= 0 && selectedIndex < list.model.size) {
                    listSelectionHolder.selection = list.model.getElementAt(selectedIndex)
                    savedSelection = null
                }
            }
        }

        list.model.addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) {
                if (e.type == ListDataEvent.INTERVAL_ADDED)
                    (e.index0..e.index1).find { list.model.getElementAt(it) == savedSelection }
                        ?.run { ApplicationManager.getApplication().invokeLater { ScrollingUtil.selectItem(list, this) } }
            }

            override fun contentsChanged(e: ListDataEvent) {}
            override fun intervalRemoved(e: ListDataEvent) {
                if (e.type == ListDataEvent.INTERVAL_REMOVED) savedSelection = listSelectionHolder.selection
            }
        })
    }

    private fun installPopup(list: GitHubWorkflowRunList) {
        val popupHandler = object : PopupHandler() {
            override fun invokePopup(comp: java.awt.Component, x: Int, y: Int) {
                    val popupMenu = actionManager
                        .createActionPopupMenu("GithubWorkflowListPopup",
                            actionManager.getAction("Github.Workflow.ToolWindow.List.Popup") as ActionGroup)
                    popupMenu.setTargetComponent(list)
                    popupMenu.component.show(comp, x, y)
            }
        }
        list.addMouseListener(popupHandler)
    }

    private fun createDataProviderModel(context: GitHubWorkflowRunDataContext,
                                        listSelectionHolder: GitHubWorkflowRunListSelectionHolder,
                                        parentDisposable: Disposable): SingleValueModel<GitHubWorkflowRunDataProvider?> {
        val model: SingleValueModel<GitHubWorkflowRunDataProvider?> = SingleValueModel(null)

        fun setNewProvider(provider: GitHubWorkflowRunDataProvider?) {
            val oldValue = model.value
            if (oldValue != null && provider != null && oldValue.url != provider.url) {
                model.value = null
            }
            model.value = provider
        }
        Disposer.register(parentDisposable, Disposable {
            model.value = null
        })

        listSelectionHolder.addSelectionChangeListener(parentDisposable) {
            val provider = listSelectionHolder.selection?.let { context.dataLoader.getDataProvider(it.logs_url) }
            setNewProvider(provider)
        }

        context.dataLoader.addInvalidationListener(parentDisposable) {
            val selection = listSelectionHolder.selection
            if (selection != null && selection.logs_url == it) {
                setNewProvider(context.dataLoader.getDataProvider(selection.logs_url))
            }
        }

        return model
    }

    private fun createLogLoadingModel(dataProviderModel: SingleValueModel<GitHubWorkflowRunDataProvider?>,
                                      parentDisposable: Disposable): GHCompletableFutureLoadingModel<String> {
        val model = GHCompletableFutureLoadingModel<String>()

        var listenerDisposable: Disposable? = null

        dataProviderModel.addValueChangedListener {
            val provider = dataProviderModel.value
            model.future = provider?.logRequest

            listenerDisposable = listenerDisposable?.let {
                Disposer.dispose(it)
                null
            }

            if (provider != null) {
                val disposable = Disposer.newDisposable().apply {
                    Disposer.register(parentDisposable, this)
                }
                provider.addRequestsChangesListener(disposable, object : GitHubWorkflowRunDataProvider.WorkflowRunChangedListener {
                    override fun logChanged() {
                        model.future = provider.logRequest
                    }
                })

                listenerDisposable = disposable
            }
        }
        return model
    }

    private fun <T> createValueModel(loadingModel: GHCompletableFutureLoadingModel<T>): SingleValueModel<T?> {
        val model = SingleValueModel<T?>(null)
        loadingModel.addStateChangeListener(object : GHLoadingModel.StateChangeListener {
            override fun onLoadingCompleted() {
                model.value = loadingModel.result
            }

            override fun onReset() {
                model.value = loadingModel.result
            }
        })
        return model
    }



}
