# Modular Java Profiler (modjprof)
Summary: This is a small, modular java profiler.

## System requirements
 1. This profiler is build to run on **GNU/Linux** systems. Feel free to modify it to support multiple platforms.

 1. *Java* <!-- TODO version (1.6 or newer) --> to run Maven and the profiler

 1. *Maven* <!-- TODO version --> to build the profiler

## Build and Package the Profiler
Build and package the application and run all integration tests:

        mvn clean verify

## Java Agent
### Application Server Startup Settings: JBoss WildFly (Standalone mode)
<!-- ### Domain mode-->
1. Copy the agent and all its dependencies to `/tmp`:

        cp target/*.jar /tmp/

1. Add this at the bottom of `$JBOSS_HOME/bin/standalone.conf`:

        JAVA_OPTS="$JAVA_OPTS -javaagent:/tmp/modjprof-agent.jar -Xbootclasspath/p:/tmp/modjprof-agent.jar -Djboss.modules.system.pkgs=ch.puzzle.modjprof"
