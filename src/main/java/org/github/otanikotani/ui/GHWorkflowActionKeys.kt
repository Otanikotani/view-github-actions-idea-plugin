package org.github.otanikotani.ui

import com.intellij.openapi.actionSystem.DataKey
import org.github.otanikotani.api.GithubWorkflow
import org.jetbrains.plugins.github.api.data.pullrequest.GHPullRequestShort

object GHWorkflowActionKeys {
  @JvmStatic
  val ACTION_DATA_CONTEXT = DataKey.create<GHWorkflowActionDataContext>("org.jetbrains.plugins.github.pullrequest.datacontext")

  @JvmStatic
  internal val SELECTED_PULL_REQUEST = DataKey.create<GithubWorkflow>("org.jetbrains.plugins.github.pullrequest.list.selected")
}