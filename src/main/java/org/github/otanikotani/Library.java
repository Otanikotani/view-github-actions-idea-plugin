package org.github.otanikotani;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class Library {
    public boolean someLibraryMethod() {
        return true;
    }

    public static void main(String[] args) throws Exception {
        GitHub github = GitHub.connect();
        GHOrganization org = github.getOrganization("trilogy-group");
        GHRepository repo = org.getRepository("zAAAA");
    }
}
