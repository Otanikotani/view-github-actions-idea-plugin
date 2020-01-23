package org.github.otanikotani.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CheckRunPullRequestResponse {

  String url;
  int id;
  CheckRunPullRequestHeadResponse head;

}
