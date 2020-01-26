package org.github.otanikotani.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckSuiteResultDto {
    private String name;
    private String status;
    private String startedAt;
}
