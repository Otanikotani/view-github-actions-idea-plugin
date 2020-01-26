package org.github.otanikotani.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.github.otanikotani.dto.CheckSuiteResultDto;
import org.github.otanikotani.dto.RepoDetailsDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface CheckSuiteService {
    static CheckSuiteService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, CheckSuiteService.class);
    }

    void setRepoDetails(@NotNull RepoDetailsDto repoDetails);

    void run(Consumer<List<CheckSuiteResultDto>> consumer);
}
