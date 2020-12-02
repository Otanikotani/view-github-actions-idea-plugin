package org.github.otanikotani.api

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.ThrowableConvertor
import org.apache.commons.io.IOUtils
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiResponse
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.ZipInputStream

class WorkflowRunLogGet(url: String) : GithubApiRequest.Get<String>(url) {
    override fun extractResult(response: GithubApiResponse): String {
        return response.handleBody(ThrowableConvertor {
            var content = TreeMap<String, String>()
            var result = "Logs are unavailable"
            ZipInputStream(it).use {
                while (true) {
                    val entry = it.nextEntry ?: break
                    val name = entry.name
                    if (name.startsWith("build/") && name.endsWith(".txt")) {
                        val fileContent = IOUtils.toString(it, StandardCharsets.UTF_8.toString())
                        val contentKey = name.substring("build/".length, name.length - ".txt".length - 1)
                        val contentKeyParts = contentKey.split("_")
                        val betterSorted = contentKeyParts[0].padStart(4, '0')
                        val stepName = contentKeyParts.drop(1).joinToString("")

                        content["${betterSorted}_${stepName}"] = fileContent
                    }
                }
                if (result == "Logs are unavailable") {
                    LOG.debug("Failed to extract results from the response to: $url")
                }
            }
            LOG.debug("Found ${content.size} steps")

            if (!content.isEmpty()) {
                result = ""
                content.forEach { k, v ->
                    val name = k.split("_")[1]
                    result += "---------- ${name} ----------\n"
                    result += v + "\n"
                }
            }
            result
        })
    }

    companion object {
        private val LOG = Logger.getInstance("org.github.otanikotani")
    }
}