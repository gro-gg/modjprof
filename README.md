# Modular Java Profiler (modjprof)
## Summary
This is a small, modular java profiler. It will instrument the methods of your application to calculate the execution time for each method. The profiler consists of diffrent parts that may be used to profile your application.

- **modjprof-agent**: is the Java Agent that instruments your application
- **modjprof-trc2txt**: is a tool to convert the trace file into a human readable format
- **modjprof-control**: is an optional Servlet to control the Java Agent (start, stop, ...)
- **modjprof-filter**: is an optional Request Filter to enable the Java Agent for a single request
- _sample-application_: is a sample application, used for the integration tests
- _sample-servlet_: is a sample servlet, written for testing purposes

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
        cp modjprof-agent/target/*.properties /tmp/

1. Add the following line to `$TOMCAT_HOME/bin/setenv.sh`. You might need to create this file first:

        CATALINA_OPTS="$CATALINA_OPTS -javaagent:/tmp/modjprof-agent.jar"

## Trace File Analysis (modjprof-trc2txt)
The tool modjprof-trc2txt is copied from rsjprof, a profiler from Puzzle ITC GmbH written in C. modjprof-trc2txt reads standard input, groups same calls, summarizes the execution time and writes the result in a human readable format to standard output.

 1. Locate the converter and copy it to `/tmp/`

        find -name modjprof-trc2txt -type f -exec cp '{}' /tmp \;

 1. Convert the trace file (replace `<thread-id>` with the correct thread id)

        cd /tmp
        ./modjprof-trc2txt < modjprof_<thread-id>.trc > modjprof_<thread-id>.txt

 1. Anaylze the `.txt` file with a editor with folding support (e.g. vim). You could find how to open close the folds with `vim` on the following page: `http://vim.wikia.com/wiki/Folding`
 (`zM` will close all filds, `za` will toggle the fold on the cursor level, ...)

        vim modjprof_<thread-id>.txt

Now you will find something like the following:

        total: 8.043607 (100%)  self: 2.426052  count: 1  avg: 8.043607  ch.puzzle.sample.ClassUnderTest.main(java.lang.String[])
            total: 5.617555 (100%)  self: 2.777667  count: 2  avg: 2.808777  ch.puzzle.sample.ClassA.run()
                total: 2.396781 (84%)  self: 1.733192  count: 6  avg: 0.399463  ch.puzzle.sample.ClassB.run()
                    total: 0.663589 (100%)  self: 0.663589  count: 6  avg: 0.110598  ch.puzzle.sample.ClassC.run(java.lang.String, int, long)
                total: 0.443107 (16%)  self: 0.443107  count: 4  avg: 0.110776  ch.puzzle.sample.AbstractClassB.concreteMethodInAbstractClass()

 - Each line represents one or multiple calls to the method at the end of the line.
 - The vertical levels represents the call stack. An indented line means, that the method was called in the method from the line above.
 - The value `total` defines how many milliseconds [ms] spent in total in the whole method.
 - The percent value means how many time of the current method was spent in this method.
 - The value `self` defines how many milliseconds [ms] spent in the method without the calls to other methods.
 - The `count` defines how many times the method was called within the current method.
 - The value `avg` defines how many milliseconds [ms] spent in average per call (total / count).

## Control Servlet (modjprof-control)
The Control Servlet (modjprof-control) can be used to control the agent at runtime.

### Deployment
1.  Deploy the file `modjprof-control/target/modjprof-control.war` to your Tomcat / WildFly server.

1. Start the Servlet by opening the URL `http://localhost:8080/modjprof-control/` in your browser.

### Usage
You can send command to the agent by appending the command to the servlet URL
Actually there are the following commands implemented:

 - **/start**	will start the profiler
 - **/stop**	will stop the profiler
 - **/list**  will list all stored trace files. Further you can download each file.
 - **/delete** will delete all stored trace files

The Control Servlet will also print a usage page containing links to the commands.

## Request Filter (modjprof-filter)
The Request Filter (modjprof-filter) can be used to enable the profiler for a single browser request, even if the profiler is actually disabled.

### Deployment
Because it is not possible to filter a request in another deployment, you have to add the file ModjprofServletFilter.java to your application. The Request Filter is also licensed under the Apache License 2.0 to allow this in proprietary applications.

### Usage
 1. Disable profiling by setting `isProfilerEnabled = false` in your agent config file or stop a running profiler with the _Control Servlet (modjprof-control)_.

 1. Add `modjprof` to the User Agent string of your browser. You could use a brower extension like _User Agent Switcher_ for Firefox.
 https://addons.mozilla.org/en-US/firefox/addon/user-agent-switcher/

 1. Open your application with your modified user agent browser

## Troubleshooting
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
