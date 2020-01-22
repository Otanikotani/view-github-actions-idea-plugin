package org.github.otanikotani

import spock.lang.Specification

class LibrarySpec extends Specification {
  def "someLibraryMethod returns true"() {
    setup:
    def lib = new Library()

    when:
    def result = lib.someLibraryMethod()

    then:
    result
  }
}
