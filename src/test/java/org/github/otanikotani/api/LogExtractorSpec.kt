package org.github.otanikotani.api

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import java.io.InputStream

class LogExtractorSpec : StringSpec({
    "Extract from a workflow with a single job" {
        val archiveStream = readResource("single.zip")

        val text = LogExtractor().extractFromStream(archiveStream)

        assertSoftly {
            text shouldNotBe null
            text?.let {
                it shouldContain "---------- Set up job ----------"
                it shouldContain "---------- Run local action ----------"
                it shouldContain "========== test =========="
                it.indexOf("Set up job") shouldBeLessThan it.indexOf("Run local action")
            }
        }
    }

    "Extract from a workflow with multiple jobs prints out all jobs output" {
        val archiveStream = readResource("multi.zip")

        val text = LogExtractor().extractFromStream(archiveStream)

        assertSoftly {
            text shouldNotBe null
            text?.let {
                it shouldContain "test"
                it.indexOf("========== abc3") shouldBeLessThan it.indexOf("========== abc2")
            }
        }
    }
})

fun readResource(name: String): InputStream {
    return LogExtractorSpec::class.java.getResourceAsStream(name)!!
}

