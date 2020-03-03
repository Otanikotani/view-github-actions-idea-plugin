package org.github.otanikotani.workflow

import com.intellij.ide.DataManager
import com.intellij.ide.actions.RefreshAction
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.*
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.util.ui.UIUtil
import org.github.otanikotani.api.GithubWorkflow
import org.github.otanikotani.workflow.action.GitHubWorkflowActionKeys
import org.github.otanikotani.workflow.data.GitHubWorkflowListLoaderImpl
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.pullrequest.config.GithubPullRequestsProjectUISettings
import org.jetbrains.plugins.github.pullrequest.ui.GHCompletableFutureLoadingModel
import org.jetbrains.plugins.github.pullrequest.ui.GHLoadingModel
import org.jetbrains.plugins.github.pullrequest.ui.GHLoadingPanel
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
internal class GitHubWorkflowComponentFactory(private val project: Project) {

    private val progressManager = ProgressManager.getInstance()
    private val actionManager = ActionManager.getInstance()
    private val copyPasteManager = CopyPasteManager.getInstance()

    private val projectUiSettings = GithubPullRequestsProjectUISettings.getInstance(project)
    private val dataContextRepository = GitHubWorkflowDataContextRepository.getInstance(project)

    @CalledInAwt
    fun createComponent(remoteUrl: GitRemoteUrlCoordinates, account: GithubAccount, requestExecutor: GithubApiRequestExecutor,
                        parentDisposable: Disposable): JComponent {

        val contextDisposable = Disposer.newDisposable()
        val contextValue = object : LazyCancellableBackgroundProcessValue<GitHubWorkflowDataContext>(progressManager) {
            override fun compute(indicator: ProgressIndicator) =
                dataContextRepository.getContext(account, requestExecutor, remoteUrl).also {
                    Disposer.register(contextDisposable, it)
                }
        }
        Disposer.register(parentDisposable, contextDisposable)
        Disposer.register(parentDisposable, Disposable { contextValue.drop() })

        val uiDisposable = Disposer.newDisposable()
        Disposer.register(parentDisposable, uiDisposable)

        val loadingModel = GHCompletableFutureLoadingModel<GitHubWorkflowDataContext>()
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

    private fun createContent(context: GitHubWorkflowDataContext, disposable: Disposable): JComponent {
        val list = createWorkflowListComponent(context, disposable)

//        val dataProviderModel = createDataProviderModel(dataContext, listSelectionHolder, disposable)
//
//        val detailsLoadingModel = createDetailsLoadingModel(dataProviderModel, disposable)
//        val detailsModel = createValueModel(detailsLoadingModel)
//
//        val detailsPanel = createDetailsPanel(dataContext, detailsModel, avatarIconsProviderFactory, disposable)
//        val detailsLoadingPanel = GHLoadingPanel(detailsLoadingModel, detailsPanel, disposable,
//            GHLoadingPanel.EmptyTextBundle.Simple("Select pull request to view details",
//                "Can't load details")).apply {
//            errorHandler = GHLoadingErrorHandlerImpl() { dataProviderModel.value?.reloadDetails() }
//        }
//
//        val changesModel = GHPRChangesModelImpl(project)
//        val diffHelper = GHPRChangesDiffHelperImpl(project, dataContext.reviewService,
//            avatarIconsProviderFactory, dataContext.securityService.currentUser)
//        val changesLoadingModel = createChangesLoadingModel(changesModel, diffHelper,
//            dataProviderModel, projectUiSettings, disposable)
//        val changesBrowser = GHPRChangesBrowser(changesModel, diffHelper, project)
//
//        val changesLoadingPanel = GHLoadingPanel(changesLoadingModel, changesBrowser, disposable,
//            GHLoadingPanel.EmptyTextBundle.Simple("Select pull request to view changes",
//                "Can't load changes",
//                "Pull request does not contain any changes")).apply {
//            errorHandler = GHLoadingErrorHandlerImpl() { dataProviderModel.value?.reloadChanges() }
//        }

        val actionDataContext = GitHubWorkflowListSelectionActionDataContext(context)

        return OnePixelSplitter("GitHub.Workflows.Component", 0.33f).apply {
            background = UIUtil.getListBackground()
            isOpaque = true
            isFocusCycleRoot = true
            firstComponent = list
            secondComponent = OnePixelSplitter("GitHub.Workflows.Preview.Component", 0.5f).apply {
                firstComponent = JBPanelWithEmptyText(null).apply {
                    background = UIUtil.getListBackground()
                }
                secondComponent = JBPanelWithEmptyText(null).apply {
                    background = UIUtil.getListBackground()
                }
            }
//                .also {
//                (actionManager.getAction("GitHub.Workflow.Details.Reload") as RefreshAction).registerCustomShortcutSet(it, disposable)
//            }
        }.also {
            //            changesBrowser.diffAction.registerCustomShortcutSet(it, disposable)
            DataManager.registerDataProvider(it) { dataId ->
                if (Disposer.isDisposed(disposable)) null
                else when {
                    GitHubWorkflowActionKeys.ACTION_DATA_CONTEXT.`is`(dataId) -> actionDataContext
                    else -> null
                }

            }
        }
    }

    private fun createWorkflowListComponent(context: GitHubWorkflowDataContext,
                                            disposable: Disposable): JComponent {

        val listSelectionHolder = GitHubWorkflowsListSelectionHolderImpl()
        val listModel = CollectionListModel<GithubWorkflow>()

        val list = GitHubWorkflowList(listModel).apply {
            emptyText.clear()
        }.also {
            it.addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent?) {
                    if (it.selectedIndex < 0 && !it.isEmpty) it.selectedIndex = 0
                }

                override fun focusLost(e: FocusEvent?) {}
            })

            installSelectionSaver(it, listSelectionHolder)
        }

        val listLoader = GitHubWorkflowListLoaderImpl(ProgressManager.getInstance(), context.requestExecutor,
            context.gitHubRepositoryCoordinates,
            listModel)

        val listReloadAction = actionManager.getAction("Github.Workflow.List.Reload") as RefreshAction

        return GitHubWorkflowListLoaderPanel(listLoader, listReloadAction, list).apply {
            errorHandler = GitHubLoadingErrorHandlerImpl {
                listLoader.reset()
            }
        }.also {
            listReloadAction.registerCustomShortcutSet(it, disposable)
        }.also {
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

    private fun installSelectionSaver(list: GitHubWorkflowList, listSelectionHolder: GitHubWorkflowsListSelectionHolder) {
        var savedSelectionNumber: Long? = null

        list.selectionModel.addListSelectionListener { e: ListSelectionEvent ->
            if (!e.valueIsAdjusting) {
                val selectedIndex = list.selectedIndex
                if (selectedIndex >= 0 && selectedIndex < list.model.size) {
                    listSelectionHolder.selectionId = list.model.getElementAt(selectedIndex).id
                    savedSelectionNumber = null
                }
            }
        }

        list.model.addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) {
                if (e.type == ListDataEvent.INTERVAL_ADDED)
                    (e.index0..e.index1).find { list.model.getElementAt(it).id == savedSelectionNumber }
                        ?.run { ApplicationManager.getApplication().invokeLater { ScrollingUtil.selectItem(list, this) } }
            }

            override fun contentsChanged(e: ListDataEvent) {}
            override fun intervalRemoved(e: ListDataEvent) {
                if (e.type == ListDataEvent.INTERVAL_REMOVED) savedSelectionNumber = listSelectionHolder.selectionId
            }
        })
    }

//    private fun createChangesLoadingModel(changesModel: GHPRChangesModel,
//                                          diffHelper: GHPRChangesDiffHelper,
//                                          dataProviderModel: SingleValueModel<GHPRDataProvider?>,
//                                          uiSettings: GithubPullRequestsProjectUISettings,
//                                          disposable: Disposable): GHPRChangesLoadingModel {
//        val model = GHPRChangesLoadingModel(changesModel, diffHelper, uiSettings.zipChanges)
//        projectUiSettings.addChangesListener(disposable) { model.zipChanges = projectUiSettings.zipChanges }
//
//        val requestChangesListener = object : GHPRDataProvider.RequestsChangedListener {
//            override fun commitsRequestChanged() {
//                model.dataProvider = model.dataProvider
//            }
//        }
//        dataProviderModel.addValueChangedListener {
//            model.dataProvider?.removeRequestsChangesListener(requestChangesListener)
//            model.dataProvider = dataProviderModel.value?.apply {
//                addRequestsChangesListener(disposable, requestChangesListener)
//            }
//        }
//        return model
//    }

//    private fun createDetailsLoadingModel(dataProviderModel: SingleValueModel<GHPRDataProvider?>,
//                                          parentDisposable: Disposable): GHCompletableFutureLoadingModel<GHPullRequest> {
//        val model = GHCompletableFutureLoadingModel<GHPullRequest>()
//
//        var listenerDisposable: Disposable? = null
//
//        dataProviderModel.addValueChangedListener {
//            val provider = dataProviderModel.value
//            model.future = provider?.detailsRequest
//
//            listenerDisposable = listenerDisposable?.let {
//                Disposer.dispose(it)
//                null
//            }
//
//            if (provider != null) {
//                val disposable = Disposer.newDisposable().apply {
//                    Disposer.register(parentDisposable, this)
//                }
//                provider.addRequestsChangesListener(disposable, object : GHPRDataProvider.RequestsChangedListener {
//                    override fun detailsRequestChanged() {
//                        model.future = provider.detailsRequest
//                    }
//                })
//
//                listenerDisposable = disposable
//            }
//        }
//
//        return model
//    }

//    private fun <T> createValueModel(loadingModel: GHSimpleLoadingModel<T>): SingleValueModel<T?> {
//        val model = SingleValueModel<T?>(null)
//        loadingModel.addStateChangeListener(object : GHLoadingModel.StateChangeListener {
//            override fun onLoadingCompleted() {
//                model.value = loadingModel.result
//            }
//
//            override fun onReset() {
//                model.value = loadingModel.result
//            }
//        })
//        return model
//    }

//    private fun createDataProviderModel(dataContext: GHWorkflowDataContext,
//                                        listSelectionHolder: GithubWorkflowsListSelectionHolder,
//                                        parentDisposable: Disposable): SingleValueModel<GHWorkflowDataProvider?> {
//        val model: SingleValueModel<GHWorkflowDataProvider?> = SingleValueModel(null)
//
//        fun setNewProvider(provider: GHWorkflowDataProvider?) {
//            val oldValue = model.value
//            if (oldValue != null && provider != null && oldValue.number != provider.number) {
//                model.value = null
//            }
//            model.value = provider
//        }
//        Disposer.register(parentDisposable, Disposable {
//            model.value = null
//        })
//
//        listSelectionHolder.addSelectionChangeListener(parentDisposable) {
//            setNewProvider(listSelectionHolder.selectionNumber?.let(dataContext.dataLoader::getDataProvider))
//        }
//
//        dataContext.dataLoader.addInvalidationListener(parentDisposable) {
//            val selection = listSelectionHolder.selectionNumber
//            if (selection != null && selection == it) {
//                setNewProvider(dataContext.dataLoader.getDataProvider(selection))
//            }
//        }
//
//        return model
//    }
}
