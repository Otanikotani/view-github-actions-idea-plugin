package org.github.otanikotani.api

import com.intellij.util.ThrowableConvertor
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiResponse

class DownloadUrlWorkflowRunLogGet(url: String) : GithubApiRequest.Get<String>(url) {
    override fun extractResult(response: GithubApiResponse): String {
        return response.handleBody(ThrowableConvertor {
            LogExtractor().extractFromStream(it) ?: "Logs are unavailable"
        })
    }
}