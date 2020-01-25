package org.github.otanikotani;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.concurrent.CompletableFuture;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.repository.CheckSuites;

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

      System.out.println(checkSuites);
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }
}
