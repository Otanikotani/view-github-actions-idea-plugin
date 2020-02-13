package org.github.otanikotani.api

import org.jetbrains.plugins.github.api.GithubServerPath
import spock.lang.Specification

class CheckSuitesSpec extends Specification {

  def "check suites url suffix is /repos"() {
    expect:
    new CheckSuites().urlSuffix == '/repos'
  }

  def "check suites github api request has the experimental header"() {
    given:
    def serverPath = new GithubServerPath("github.com")

    when:
    def request = new CheckSuites().get(serverPath, "trinidad", "tobago", "rum")

    then:
    request.acceptMimeType == "application/vnd.github.antiope-preview+json"
  }

  def "check suites github api request has operation name"() {
    given:
    def serverPath = new GithubServerPath("github.com")

    when:
    def request = new CheckSuites().get(serverPath, "trinidad", "tobago", "rum")

    then:
    request.operationName == "Get Check Suites..."
  }

  def "check suites github api request has the correct url"() {
    given:
    def serverPath = new GithubServerPath("github.com")

    when:
    def request = new CheckSuites().get(serverPath, "trinidad", "tobago", "rum")

    then:
    request.url == "https://api.github.com/repos/trinidad/tobago/commits/rum/check-suites"
  }
}
