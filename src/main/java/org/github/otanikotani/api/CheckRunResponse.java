package org.github.otanikotani.api;

import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CheckRunResponse {

  Long id;
  String name;
  String url;
  String conclusion;
  Date completed_at;
  Date started_at;
  String head_sha;
  String status;
}
