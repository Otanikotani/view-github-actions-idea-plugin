package org.github.otanikotani.api

import com.intellij.util.ThrowableConvertor
import org.apache.commons.io.IOUtils
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiResponse
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipInputStream

class WorkflowRunLogGet(url: String) : GithubApiRequest.Get<String>(url) {
    override fun extractResult(response: GithubApiResponse): String {
        return response.handleBody(ThrowableConvertor<InputStream, String, IOException> {
            var result = ""
            ZipInputStream(it).use {
                while (true) {
                    val entry = it.nextEntry ?: break
                    if (entry.name == "1_build.txt") {
                        result = IOUtils.toString(it, StandardCharsets.UTF_8.toString())
                    }
                }
            }

            result
        })
    }
}