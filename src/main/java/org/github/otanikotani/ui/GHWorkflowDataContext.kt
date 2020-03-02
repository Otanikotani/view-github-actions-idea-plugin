// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.github.otanikotani.ui

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GithubWorkflow
import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import javax.swing.ListModel

internal class GHWorkflowDataContext(val gitRepositoryCoordinates: GitRemoteUrlCoordinates,
                                     val repositoryCoordinates: GHRepositoryCoordinates,
                                     val account: GithubAccount,
                                     val securityService: GHWorkflowSecurityService,
                                     val requestExecutor: GithubApiRequestExecutor,
                                     val listLoader: GHWorkflowListLoader,
                                     val listModel: ListModel<GithubWorkflow>,
                                     val searchHolder: GithubWorkflowSearchQueryHolder) : Disposable {
    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
