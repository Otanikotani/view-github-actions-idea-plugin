package org.github.otanikotani.repository;

import java.util.Date;
import java.util.List;
import lombok.Value;

@Value
public class CheckSuite {

  Long id;
  String url;
  String conclusion;
  String status;
  List<CheckRun> checkRuns;
}
