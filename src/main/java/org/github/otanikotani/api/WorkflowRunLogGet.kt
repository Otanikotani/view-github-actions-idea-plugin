package org.github.otanikotani.api

import com.intellij.openapi.diagnostic.logger
import com.intellij.util.ThrowableConvertor
import org.apache.commons.io.IOUtils
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiResponse
import java.nio.charset.StandardCharsets
import java.util.zip.ZipInputStream

class WorkflowRunLogGet(url: String) : GithubApiRequest.Get<String>(url) {
    override fun extractResult(response: GithubApiResponse): String {
        return response.handleBody(ThrowableConvertor {
            var result = "Logs are unavailable"
            ZipInputStream(it).use {
                while (true) {
                    val entry = it.nextEntry ?: break
                    val name = entry.name
                    if (name != null && name.startsWith("1_") && name.endsWith(".txt") && !name.contains('(')) {
                        result = IOUtils.toString(it, StandardCharsets.UTF_8.toString())
                        break
                    }
                }
                if (result == "Logs are unavailable") {
                    LOG.debug("Failed to extract results from the response to: $url")
                }
            }

            result
        })
    }

    companion object {
        private val LOG = logger("org.github.otanikotani")
    }
}