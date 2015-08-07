# Modular Java Profiler (modjprof)
## Summary
This is a small, modular java profiler. It consists of diffrent parts that may be used to profile your application.

- **modjprof-agent**: is the Java Agent that instruments your application
- **modjprof-control**: is a optional Servler to control the Java Agent (start, stop, ...)


## System requirements
 1. This profiler is build to run on **GNU/Linux** systems. Feel free to modify it to support multiple platforms.

 1. *Java* to run Maven and the profiler
 <!-- TODO version (1.6 or newer) -->

 1. *Maven* to build the profiler

## Build and Package the Profiler
Build and package the application and run all integration tests:

        mvn clean verify

If you get errors in the integration tests, try to run the sample application with the integrated  Exec Maven Plugin:

        mvn clean package exec:exec

## Java Agent (modjprof-agent)
### Application Server Startup Settings:
#### JBoss WildFly (Standalone mode)
<!-- ### Domain mode-->
1. Copy the agent and all its dependencies to `/tmp`:

        cp target/*.jar /tmp/

1. Add this at the bottom of `$JBOSS_HOME/bin/standalone.conf`:

        JAVA_OPTS="$JAVA_OPTS -javaagent:/tmp/modjprof-agent.jar -Xbootclasspath/p:/tmp/modjprof-agent.jar -Djboss.modules.system.pkgs=ch.puzzle.modjprof"

#### Tomcat
1. Copy the agent and all its dependencies to `/tmp`:

        cp target/*.jar /tmp/

1. Add the following line to `$TOMCAT_HOME/bin/setenv.sh`. You might need to create this file first:

        CATALINA_OPTS="$CATALINA_OPTS -javaagent:/tmp/modjprof-agent.jar"

## Control Servlet (modjprof-control)
The control Servlet actually only works on Tomcat.

1.  Deploy the file `modjprof-control.jar` to your Tomcat.

2. Start the Servlet by opening the URL `http://localhost:8080/modjprof-control/` in your browser.
