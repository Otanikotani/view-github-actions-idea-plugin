package org.github.otanikotani.repository;

import java.util.Date;
import lombok.Value;

@Value
public class CheckRun {

  Long id;
  String name;
  String conclusion;
  Date completed_at;
  Date started_at;
  String head_sha;
  String status;
}
