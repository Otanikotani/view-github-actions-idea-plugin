package org.github.otanikotani.api;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CheckRunsResponse {
  List<CheckRunResponse> check_runs;
}
