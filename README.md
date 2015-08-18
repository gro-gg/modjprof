# Modular Java Profiler (modjprof)
## Summary
This is a small, modular java profiler. It consists of diffrent parts that may be used to profile your application.

- **modjprof-agent**: is the Java Agent that instruments your application
- **modjprof-control**: is a optional Servler to control the Java Agent (start, stop, ...)


## System requirements
 1. This profiler is build to run on **GNU/Linux** systems. Feel free to modify it to support multiple platforms.

 1. **Java 1.6 or newer** to run Maven and the profiler

 1. **Maven** to build the profiler

 1. An application server or servlet container to run the Control Servlet. The profiler is tested with **Tomcat 8** and **WildFly 9**, but you could use any other server with possible changes in configuration.

## Build and Package the Profiler
Build and package the application and run all integration tests:

    mvn clean verify

If you get errors in the integration tests, set log level to DEBUG:

    mvn clean verify -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

or try to run the sample application with the integrated  Exec Maven Plugin:

    cd modjprof-agent/
    mvn clean package exec:exec

## Java Agent (modjprof-agent)
### Application Server Startup Settings:
#### JBoss WildFly (Standalone mode)
<!-- ### Domain mode-->
1. Copy the agent and all its dependencies to `/tmp`:

        cp modjprof-agent/target/*.jar /tmp/

1. Add this at the bottom of `$JBOSS_HOME/bin/standalone.conf`:

        JAVA_OPTS="$JAVA_OPTS -javaagent:/tmp/modjprof-agent.jar -Xbootclasspath/p:/tmp/modjprof-agent.jar -Djboss.modules.system.pkgs=ch.puzzle.modjprof.agent"

#### Tomcat
1. Copy the agent and all its dependencies to `/tmp`:

        cp modjprof-agent/target/*.jar /tmp/

1. Add the following line to `$TOMCAT_HOME/bin/setenv.sh`. You might need to create this file first:

        CATALINA_OPTS="$CATALINA_OPTS -javaagent:/tmp/modjprof-agent.jar"

## Control Servlet (modjprof-control)
The Control Servlet (modjprof-control) can be used to control the agent at runtime.

### Deployment
1.  Deploy the file `modjprof-control/target/modjprof-control.war` to your Tomcat / WildFly server.

2. Start the Servlet by opening the URL `http://localhost:8080/modjprof-control/` in your browser.

### Usage
You can send command to the agent by appending the command to the servlet URL
Actually there are the following commands implemented:

 - **/start**	will start the profiler
 - **/stop**	will stop the profiler

The Control Servlet will also print a usage page containing links to the commands.
