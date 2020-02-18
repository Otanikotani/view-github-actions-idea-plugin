package org.github.otanikotani.api


import spock.lang.Specification

class CheckRunsSpec extends Specification {
    def "check runs github api request has the experimental header"() {
        when:
        def request = new CheckRuns().get("https://github.com/some_url")

        then:
        request.acceptMimeType == "application/vnd.github.antiope-preview+json"
    }

    def "check runs github api request has operation name"() {
        when:
        def request = new CheckRuns().get("https://github.com/some_url")

        then:
        request.operationName == "Get Check Runs..."
    }

    def "check runs uses the given url for building the request"() {
        when:
        def request = new CheckRuns().get("https://github.com/some_url")

        then:
        request.url == "https://github.com/some_url"
    }
}
