package org.github.otanikotani.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CheckSuiteResponse {

  Long id;
  String url;
  String check_runs_url;
  String status;
  String conclusion;
}
