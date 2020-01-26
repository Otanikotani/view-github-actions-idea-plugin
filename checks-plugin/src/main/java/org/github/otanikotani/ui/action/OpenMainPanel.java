package org.github.otanikotani.ui.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.github.otanikotani.dto.CheckSuiteResultDto;
import org.github.otanikotani.dto.RepoDetailsDto;
import org.github.otanikotani.service.CheckSuiteService;
import org.github.otanikotani.ui.dialog.ChecksDialogWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

public class OpenMainPanel extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (isNull(project)) {
            return;
        }
        final CheckSuiteService checkSuiteService = CheckSuiteService.getInstance(project);
        checkSuiteService.setRepoDetails(new RepoDetailsDto());

        final ChecksDialogWrapper checksDialogWrapper = new ChecksDialogWrapper();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            checkSuiteService.run(checksResults -> {
                for (CheckSuiteResultDto checksResult : checksResults) {
                    checksDialogWrapper.addRow(checksResult.getName(), checksResult.getStatus(),
                            checksResult.getStartedAt());
                }
            });
            executorService.shutdown();
        });
        checksDialogWrapper.show();
    }
}
