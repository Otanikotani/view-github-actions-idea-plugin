package org.github.otanikotani.workflow

class GitHubWorkflowListSelectionActionDataContext internal constructor(private val dataContext: GitHubWorkflowDataContext)
    : GitHubWorkflowActionDataContext {

    override val account = dataContext.account

    override val gitRepositoryCoordinates = dataContext.gitRepositoryCoordinates
    override val repositoryCoordinates = dataContext.repositoryCoordinates

    override val securityService = dataContext.securityService
//  override val busyStateTracker = dataContext.busyStateTracker
//  override val stateService = dataContext.stateService
//  override val reviewService = dataContext.reviewService
//  override val commentService = dataContext.commentService

    override val requestExecutor = dataContext.requestExecutor

    override val currentUser = dataContext.securityService.currentUser

    override fun resetAllData() {
//    dataContext.metadataService.resetData()
//    dataContext.listLoader.reset()
//    dataContext.dataLoader.invalidateAllData()
    }

//  override val pullRequest: Long?
//    get() = selectionHolder.selectionNumber
//
//  override val pullRequestDetails: GHPullRequestShort?
//    get() = pullRequest?.let { dataContext.listLoader.findData(it) }
//
//  override val pullRequestDataProvider: GHPRDataProvider?
//    get() = pullRequest?.let { dataContext.dataLoader.getDataProvider(it) }
}