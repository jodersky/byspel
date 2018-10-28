# Byspel

An example project that showcases how Akka HTTP, Slick and Sqitch can
be used to create a simple web application, and how it can be
*natively* packaged for multiple platforms, including Debian systems
and Docker.

## Building

## Local development

Run `sbt start` to compile sources and run the project. Once started, the
sample website can be viewed at <http://localhost:8080>.

Note that in case schema definitions are changed (via `sqitch
deploy`), table definitions need to be regenerated with `sbt
dbTables`.

## Basic Linux distribution

Run `sbt fhsDist` to copy all project artifacts into a standalone root
filesystem that can be used for platform-specific packaging. Note that
this step also bundles sqitch migrations, compiles a native utility to
change process name, and creates a launcher script.

Check out `tree target/dist/` for a hierarchical representation of all
files and directories created.

## Docker image

A docker image can be created by running

```
docker build -t byspel .
```

Note that this requires that `sbt fhsDist` has been run before. Once
built, `byspel` can be run with `docker run -p 8555:8555 byspel`, and
will be available at <http://localhost:8555>.

## Debian package

One of the main goals of this project was to explore packaging Scala
apps for debian natively. As a result, this project is also a Debian
*source* package, and can be built with any apropriate utility.

For example,
```
debuild
```
will invoke various `dpkg-*` utilities to create a final binary
packages in the parent directory.

Unfortunately, due to sbt's reliance on internet connectivity, it is
difficult to ensure that this package can be built in isolation, as is
done with Debian's official packages. Hence, any packages following
this example project could probably only ever be part of the `contrib`
collection of packages and never in `main`.

## Copying
This project is released under the terms of the 3-clause BSD
license. See LICENSE for details.
