# hara

[![Join the chat at https://gitter.im/zcaudate/hara](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/zcaudate/hara?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/zcaudate/hara.png?branch=master)](https://travis-ci.org/zcaudate/hara)
[![Clojars](https://img.shields.io/clojars/v/im.chit/hara.svg)](https://clojars.org/im.chit/hara)

code patterns and utilities - visit the [hara website](http://docs.caudate.me/hara/)

![Logo](http://docs.caudate.me/hara/img/logo.png)

## Welcome

### What is It?

[hara](https://github.com/zcaudate/hara) is a set of libraries extending `clojure.core`, a little bit like [boost](http://www.boost.org) but without the compilation time.

### Why was it made?

Apart from [clojure](http://www.clojure.org) itself, the language lacks a big, monolithic, kitchen-sink type codebase. [hara](https://github.com/zcaudate/hara) aims to fill that gap.

### Who is this targeting?

All JVM Clojure developers.

### When was this started?

The first pieces of the current codebase was committed in 2012. Since then, the project has grown organically based on need and interest.

### Where has it been used?

In many places. Mostly to write libraries that keep this maintainable. 

### How to begin?

That's a good question. It really depends on what is being built. There's a fair amount of documentation already so please jump in and start exploring.

### Okay, lets go!

List of current libraries:

- [hara.class](http://docs.caudate.me/hara/hara-class.html) - functions for reasoning about classes
- [hara.common](http://docs.caudate.me/hara/hara-common.html) - primitives declarations and functions
- [hara.component](http://docs.caudate.me/hara/hara-component.html) - constructing composable systems
- [hara.concurrent](http://docs.caudate.me/hara/hara-concurrent.html) - methods and datastructures for concurrency
- [hara.concurrent.ova](http://docs.caudate.me/hara/hara-concurrent-ova.html) - shared mutable state
- [hara.concurrent.procedure](http://docs.caudate.me/hara/hara-concurrent.procedure.html) - controllable execution
- [hara.data](http://docs.caudate.me/hara/hara-data.html) - maps and representations of data
- [hara.event](http://docs.caudate.me/hara/hara-event.html) - event signalling and conditional restart
- [hara.expression](http://docs.caudate.me/hara/hara-expression.html) - interchange between code and data
- [hara.extend](http://docs.caudate.me/hara/hara-extend.html) - macros for extensible objects
- [hara.function](http://docs.caudate.me/hara/hara-function.html) - reasoning about functions
- [hara.group](http://docs.caudate.me/hara/hara-group.html) - generic typed collections
- [hara.io](http://docs.caudate.me/hara/hara-io.html) - tools for files and io operations
- [hara.io.file](http://docs.caudate.me/hara/hara-io-file.html) - tools for the file system
- [hara.io.scheduler](http://docs.caudate.me/hara/hara-io-scheduler.html) - easy and intuitive task scheduling
- [hara.io.watch](http://docs.caudate.me/hara/hara-io-watch.html) - watch for filesystem changes
- [hara.namespace](http://docs.caudate.me/hara/hara-namespace.html) - utilities for namespace manipulation
- [hara.object](http://docs.caudate.me/hara/hara-object.html) - think data, escape encapsulation
- [hara.reflect](http://docs.caudate.me/hara/hara-reflect.html) - java reflection made easy
- [hara.sort](http://docs.caudate.me/hara/hara-sort.html) - micellaneous sorting functions
- [hara.string](http://docs.caudate.me/hara/hara-string.html) - methods for string manipulation
- [hara.test](http://docs.caudate.me/hara/hara-test.html) - easy to use test framework
- [hara.time](http://docs.caudate.me/hara/hara-time.html) - time as a clojure map
- [hara.zip](http://docs.caudate.me/hara/hara-zip.html) - data traversal in style

## License

Copyright © 2016 Chris Zheng

Distributed under the MIT License
