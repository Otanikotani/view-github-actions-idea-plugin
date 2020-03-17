# GitHub Actions

![](https://github.com/otanikotani/view-github-checks-idea-plugin/workflows/Check/badge.svg) [![codecov](https://codecov.io/gh/otanikotani/view-github-checks-idea-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/otanikotani/view-github-checks-idea-plugin) [![GitHub Checks](https://img.shields.io/badge/JB%20Repository-GitHub%20Checks-brightgreen.svg)](https://plugins.jetbrains.com/plugin/13793-github-checks "JetBrains Repo: GitHub Checks Plugin")

A plugin for Intellij IDEA to display statuses of GitHub Actions of the repository. This plugin is a good alternative to alt-tabbing for every time you push some changes to the branch and want to see whether the repository's checks are passing on your changes.

[Jetbrains Repository Plugin page](https://plugins.jetbrains.com/plugin/13793-github-checks)

![Checks Plugin](img/checks-plugin-screenshot-3.png)

### Use cases:
- Imagine having some GitHub Workflows to set up to run on pull requests that must pass before you can merge the pull request. With this plugin you can see the status and the build log of the workflow
- You did a push into some branch and you want to see how your GitHub Actions that were triggered by that push are doing. You can monitor the status of the actions via this plugin.

### Features:

- Adds a new tab - Checks - to the Version control tool window that shows the [checks](https://developer.github.com/v3/checks/) of the currently checked out branch of the GitHub repository.

## Team

| [![Tofik Mamishov](https://github.com/tofik-mamishov.png?size=100)](https://github.com/tofik-mamishov) | [![Andrey Artyukhov](https://github.com/otanikotani.png?size=100)](https://github.com/otanikotani)  |
|---------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| [Tofik Mamishov](https://github.com/tofik-mamishov)                                              | [Andrey Artyukhov](https://github.com/otanikotani)                                                |
