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


## Java Agent (modjprof-agent)
### Application Startup with Java Agent
To start your application with the modjprof java agent use the following JVM options:

        java -javaagent:<modjprof-agent.jar>[=config=<modjprof.properties>]

If no properties file is configured, the file `modjprof.properties` should be in the same directory as the agent. You will find a example config file in the Maven build directory.

### Application Server Startup Settings:
#### JBoss WildFly (Standalone mode)
<!-- ### Domain mode-->
1. Copy the agent and all its dependencies to `/tmp`:

        cp modjprof-agent/target/*.jar /tmp/
        cp modjprof-agent/target/*.properties /tmp/

1. Add this at the bottom of `$JBOSS_HOME/bin/standalone.conf`:

        # configure java agent
        JAVA_OPTS="$JAVA_OPTS -javaagent:/tmp/modjprof-agent.jar"
        JAVA_OPTS="$JAVA_OPTS -Xbootclasspath/p:/tmp/modjprof-agent.jar"
        # configure logging
        JAVA_OPTS="$JAVA_OPTS -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
        JAVA_OPTS="$JAVA_OPTS -Xbootclasspath/p:modules/system/layers/base/org/jboss/logmanager/main/jboss-logmanager-2.0.0.Final.jar"
        # add agent and logmanager to JBOSS_MODULES_SYSTEM_PKGS
        pkgs="ch.puzzle.modjprof.agent,org.jboss.logmanager"
        if [ "x$JBOSS_MODULES_SYSTEM_PKGS" != "x" ]; then
          JBOSS_MODULES_SYSTEM_PKGS="${JBOSS_MODULES_SYSTEM_PKGS},"
        fi
        JBOSS_MODULES_SYSTEM_PKGS="${JBOSS_MODULES_SYSTEM_PKGS}${pkgs}"
        JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=${JBOSS_MODULES_SYSTEM_PKGS}"

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

### Troubleshooting
You could set the log level to FINE (debug) by adding the path to the `logging.properties` as JVM argument:

    mvn clean verify -Djava.util.logging.config.file=modjprof-agent/target/logging.properties

If you get errors while running the integration tests, try to manually run the failing test. You can find the correct command in the Maven output:

    -------------------------------------------------------
    T E S T S
    -------------------------------------------------------
    Running ch.puzzle.modjprof.instrumentation.InstrumentationIT
    ...
    INFORMATION: executing command: java -javaagent:/home/user/modjprof/modjprof-agent/target/modjprof-agent.jar -jar /home/user/modjprof/sample-application/target/sample-application-jar-with-dependencies.jar

Try to run this command with higher log level:

    java -Djava.util.logging.config.file=/home/user/modjprof/modjprof-agent/target/logging.properties -javaagent:/home/user/modjprof/modjprof-agent/target/modjprof-agent.jar -jar /home/user/modjprof/sample-application/target/sample-application-jar-with-dependencies.jar

or try to run the sample application with the integrated Exec Maven Plugin:

    cd modjprof-agent/
    mvn clean package exec:exec
