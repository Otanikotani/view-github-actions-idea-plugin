package org.github.otanikotani.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import one.util.streamex.StreamEx;
import org.github.otanikotani.dto.CheckSuiteResultDto;
import org.github.otanikotani.dto.RepoDetailsDto;
import org.github.otanikotani.repository.CheckRun;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.repository.CheckSuites;
import org.github.otanikotani.ui.dialog.ChecksDialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

public class OpenMainPanel extends AnAction {

    public static final RepoDetailsDto REPO_DETAILS = new RepoDetailsDto();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (isNull(project)) {
            return;
        }

        final ChecksDialogWrapper checksDialogWrapper = new ChecksDialogWrapper();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            run(result ->
                    checksDialogWrapper.addRow(result.getName(), result.getStatus(), result.getStartedAt())
            );
            executorService.shutdown();
        });
        checksDialogWrapper.show();
    }

    public void run(Consumer<CheckSuiteResultDto> consumer) {
        try {
            CompletableFuture<CheckSuites> checkSuitesFuture = new CheckSuiteRepository(REPO_DETAILS.getToken())
                    .getCheckSuites(REPO_DETAILS.getOwner(), REPO_DETAILS.getRepo(), REPO_DETAILS.getBranch());

            CheckSuites checkSuites = checkSuitesFuture.get();

            List<CheckRun> checkRuns = StreamEx.of(checkSuites.getCheckSuites()).flatMap(it -> it.getCheckRuns().stream())
                    .sorted((l, r) -> r.getStarted_at().compareTo(l.getStarted_at()))
                    .toList();
            PrettyTime prettyTime = new PrettyTime();
            for (CheckRun checkRun : checkRuns) {
                String name = checkRun.getName();
                String status = statusToIcons(checkRun.getStatus());
                String startedAt = prettyTime.format(checkRun.getStarted_at());
                CheckSuiteResultDto result = new CheckSuiteResultDto(name, status, startedAt);
                consumer.accept(result);
            }
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
