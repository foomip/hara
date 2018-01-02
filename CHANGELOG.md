# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## Versions

## 2.4.7
- updated docs
- fixes to scheduler, procedure
- added function and tests for zip

## 2.4.6
- integrated hara.time.joda
- fix for hara.io.watch - https://github.com/zcaudate/hara/issues/27 
- fix for hara.time - https://github.com/zcaudate/hara/issues/28

## 2.4.5
- added hara.zip
- added hara.string.prose
- added hara.io.project
- updated docs

## 2.4.2
- added hara.test
- added hara.io.file


## 2.3

#### 2.3.3
- reworked `hara.object` (incompatible with version `2.2.*``)

## 2.2

#### 2.2.17
- new: `hara.time`, `hara.io.environment`
- updated: `hara.io.scheduler`, `hara.concurrent.procedure`

#### 2.2.15
- `hara.sort.hierarchical`
- `hara.concurrent.pipe`

#### 2.2.13
- `hara.concurrent.procedure`
- documentation for `hara.object`

#### 2.2.11
- fixes for `hara.object`
- new package: `hara.group`

#### 2.2.7
- `hara.object`

#### 2.2.3
- new packages: `hara.io.scheduler`, `hara.event`
- brand new website

#### 2.1.11
- bugfix for `hara.reflect`, added `hara.object` namespace

#### 2.1.10
- Fixed all reflection warnings

#### 2.1.8
- Reworked `hara.reflect` to use only functions, moved helper macros into vinyasa

#### 2.1.5
- Fix for `hara.component` to work with none record-based components

#### 2.1.4

- Moved [iroh](http://github.com/zcaudate/iroh) to `zcaudate/hara.reflect`
- Added initialisers for `hara.component`
