## [1.1.9](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.8...1.1.9) (2020-12-02)


### Bug Fixes

* **feat:** Fix the log parsing, fix logger ([87cf7f8](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/87cf7f8ecb9eb26fc3539157ac086b0c587e3d47))

## [1.1.8](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.7...1.1.8) (2020-11-17)


### Bug Fixes

* **feat:** Bump to support latest IDEA ([fb4d646](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/fb4d646df208fe0891736dc65bcda4b27d741e7e))

## [1.1.7](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.6...1.1.7) (2020-11-17)


### Bug Fixes

* **feat:** Bump to support latest IDEA ([2690ea8](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/2690ea830ba2d2e2c2e652623f156edc54073729))

## [1.1.6](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.5...1.1.6) (2020-08-23)


### Bug Fixes

* **feat:** Bump to support 2020.2 ([d08a407](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/d08a407703f5958223879a0963e26a1da5305932))
* **feat:** Bump to support build verify check 2020.2 ([5f8c851](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/5f8c8512134c91ff4bc7109a4e1bf38e8dcbbb90))

## [1.1.5](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.4...1.1.5) (2020-07-11)


### Bug Fixes

* **log:** Add debug logging ([49c998b](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/49c998bdeeefe4f6d2bebbbb0ce6cc9248575ebb))

## [1.1.4](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.3...1.1.4) (2020-05-03)


### Bug Fixes

* **build:** Fix indentation ([a25a393](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/a25a39306ca665997a2002f97c768eef8c63b3e0))
* **feat:** Bump to support 2020.1 ([07f0ac0](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/07f0ac029246440188c825106281bf59eb86e22d))

## [1.1.3](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.2...1.1.3) (2020-04-11)


### Bug Fixes

* **doc:** Update plugin xml documentation ([ad3886e](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/ad3886e8a8d6dac9cb217a7496334a55ea987a52))
* **plugin:** Bump versions ([fb316e2](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/fb316e2fa6689b7fd903d5a831d9b826524904d4))

## [1.1.2](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.1...1.1.2) (2020-03-24)


### Bug Fixes

* **log:** Add a message about log unavailability ([0c304c1](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/0c304c1a2b4e8c65ce47c99d87f0f58b3c1bd025))

## [1.1.1](https://github.com/Otanikotani/view-github-actions-idea-plugin/compare/1.1.0...1.1.1) (2020-03-17)


### Bug Fixes

* **log:** Unzip the correct file ([ba9c06f](https://github.com/Otanikotani/view-github-actions-idea-plugin/commit/ba9c06ff096f511aabd89ecece2582224404018b))

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
