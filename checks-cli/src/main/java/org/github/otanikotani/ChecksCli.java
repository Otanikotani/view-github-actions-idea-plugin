package org.github.otanikotani;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import one.util.streamex.StreamEx;
import org.github.otanikotani.repository.CheckRun;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.repository.CheckSuites;
import org.ocpsoft.prettytime.PrettyTime;

public class ChecksCli {

  @Parameter(names = {"--token", "-t"})
  String token;

  @Parameter(names = {"--owner", "-o"})
  String owner;

  @Parameter(names = {"--repo", "-r"})
  String repo;

  @Parameter(names = {"--branch", "-b"})
  String branch;

  public static void main(String[] args) {
    ChecksCli cli = new ChecksCli();
    JCommander.newBuilder()
      .addObject(cli)
      .build()
      .parse(args);
    cli.run();
  }

  private void run() {
    try {
      CompletableFuture<CheckSuites> checkSuitesFuture = new CheckSuiteRepository(token)
        .getCheckSuites(owner, repo, branch);

      CheckSuites checkSuites = checkSuitesFuture.get();

      List<CheckRun> checkRuns = StreamEx.of(checkSuites.getCheckSuites()).flatMap(it -> it.getCheckRuns().stream())
        .sorted((l, r) -> r.getStarted_at().compareTo(l.getStarted_at()))
        .toList();
      PrettyTime prettyTime = new PrettyTime();

      //header
      String header = String.format("%20s | %s | %20s |%n", "Name", Clr.colorlessRightPad("Status", 6), "Started At");
      System.out.print(Clr.bold(header));
      for (CheckRun checkRun : checkRuns) {
        String name = checkRun.getName();
        String status = Clr.colorlessRightPad(statusToIcons(checkRun.getStatus()), 6);
        String startedAt = prettyTime.format(checkRun.getStarted_at());
        System.out.printf("%20s | %s | %20s |%n", name, status, startedAt);
      }
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  private String statusToIcons(String status) {
    if (status.equals("in_progress")) {
      return Clr.accent("⌛");
    } else if (status.equals("completed")) {
      return Clr.success("✓");
    } else {
      return Clr.error("❌");
    }
  }
}
