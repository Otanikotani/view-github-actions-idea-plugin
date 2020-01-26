package org.github.otanikotani.service.impl;

import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;
import org.github.otanikotani.dto.CheckSuiteResultDto;
import org.github.otanikotani.dto.RepoDetailsDto;
import org.github.otanikotani.repository.CheckRun;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.repository.CheckSuites;
import org.github.otanikotani.service.CheckSuiteService;
import com.intellij.openapi.project.Project;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
@Setter
public class CheckSuiteServiceImpl implements CheckSuiteService {

    private RepoDetailsDto repoDetails;

    public CheckSuiteServiceImpl(Project project) {
    }

    @Override
    public void run(Consumer<List<CheckSuiteResultDto>> consumer) {
        try {
            CompletableFuture<CheckSuites> checkSuitesFuture = new CheckSuiteRepository(repoDetails.getToken())
                    .getCheckSuites(repoDetails.getOwner(), repoDetails.getRepo(), repoDetails.getBranch());

            CheckSuites checkSuites = checkSuitesFuture.get();

            List<CheckRun> checkRuns = StreamEx.of(checkSuites.getCheckSuites()).flatMap(it -> it.getCheckRuns().stream())
                    .sorted((l, r) -> r.getStarted_at().compareTo(l.getStarted_at()))
                    .toList();
            PrettyTime prettyTime = new PrettyTime();
            List<CheckSuiteResultDto> results = new ArrayList<>();
            for (CheckRun checkRun : checkRuns) {
                String name = checkRun.getName();
                String status = statusToIcons(checkRun.getStatus());
                String startedAt = prettyTime.format(checkRun.getStarted_at());
                CheckSuiteResultDto result = new CheckSuiteResultDto(name, status, startedAt);
                results.add(result);
            }
            consumer.accept(results);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String statusToIcons(String status) {
        if (status.equals("in_progress")) {
            return "⌛";
        } else if (status.equals("completed")) {
            return "✓";
        } else {
            return "❌";
        }
    }
}
