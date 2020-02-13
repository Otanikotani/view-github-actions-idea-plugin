package org.github.otanikotani.action

import spock.lang.Specification

class RefreshActionSpec extends Specification {

  def "refresh action has proper text and description"() {
    expect:
    def action = new RefreshAction({})
    action.templatePresentation.text == 'Refresh'
    action.templatePresentation.description == 'Refreshes checks'
  }
}
