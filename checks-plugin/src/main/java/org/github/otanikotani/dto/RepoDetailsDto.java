package org.github.otanikotani.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepoDetailsDto {
    private String token = "token";
    private String owner = "otanikotani";
    private String repo = "view-github-actions-idea-plugin";
    private String branch = "heads/feature/mvp";
}
