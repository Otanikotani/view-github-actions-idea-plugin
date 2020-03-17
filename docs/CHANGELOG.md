# [1.1.0](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.0.0...1.1.0) (2020-03-17)


### Features

* **workflow:** Add Open In Browser action ([6ea7ebd](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/6ea7ebd34a8b27e97c7e235bcef4663aadfd54c2))

# [1.0.0](https://github.com/Otanikotani/view-github-checks-idea-plugin/compare/0.1.0...1.0.0) (2020-03-17)


* Brand new workflows-based plugin (#30) ([fc2a791](https://github.com/Otanikotani/view-github-checks-idea-plugin/commit/fc2a79151d5696d8474bb76788ecaea58ce3505b)), closes [#30](https://github.com/Otanikotani/view-github-checks-idea-plugin/issues/30)


### BREAKING CHANGES

* This is a completely different API than the Checks API

* chore: rename checks to worfklows

* More renaming

* Split layout

* fix: proportion keys

* feat: copy-pasta from PRs

* feat: copy-pasta from PRs

* feat: copy-pasta from PRs

* fix(account): Login only on PR

Since GITHUB_ACCOUNT_TOKEN_CHANGED is private we have no chances to listen to it and thus no chances to react to the account change directly. Thus - just ask the user to go to the Pull Requests tab and login there and then refresh

* fix(account): Login title fixed

* feat(account): Finally showing something in the UI

* feat(datacontext): Simplification of the data context

* fix(workflow): workflows list related objects in a single place

* fix(workflow): simplify workflow list classes

* fix(api): use data classes

* fix(workflow): load just once

* chore(workflow): single also block

* chore(workflow): show workflow runs

* Change text in the action.

* Only workflow runs

* fix(runs): look of workflow run list items

* fix(runs): look of workflow run list items

* fix(runs): list looks ok

* feat(log): first build log (just fake data)

* feat(log): first build log (just fake data)

* feat(log): first build log (just fake data)

* feat(log): fetching data from github

* feat(log): fetching the real log

* fix(naming): rename everything to say WorkflowRun instead of Workflow

* chore(naming): remove those ugly Impl one interface only classes

* chore(naming): remove those ugly Impl one interface only classes

* feat(log): print via console element

* feat(log): colored log output

* feat(workflows): add popup for github workflow run list

Add reload

* feat(reload): add reload actions for workflows and workflow log

* chore(reload): remove unnecessary action

* feat(workflow): Add refresh buttons

* feat(list): Color improvements

* feat(list): Fix date and workflow name fields

* feat(list): Add statuses and icons

* feat(list): Icons size fix

Co-authored-by: Tofik Mamishov <mamishov.tofik@gmail.com>

# [0.1.0](https://github.com/Otanikotani/view-github-checks-idea-plugin/compare/v0.0.5...0.1.0) (2020-02-28)


### Features

* add gradle properties for versioning ([d7f4794](https://github.com/Otanikotani/view-github-checks-idea-plugin/commit/d7f4794ce93dbf247634f7c7a104943b4dbebf1d))
* add semantic release ([528acf7](https://github.com/Otanikotani/view-github-checks-idea-plugin/commit/528acf76eb8f281d9218bbd85e2602122c2e3c19))
