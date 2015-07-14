# Modular Java Profiler (modjprof)
Summary: This is a small, modular java profiler.

## JBoss WildFly Startup Settings

### Domain mode
TODO

### Standalone mode
1. Copy the agent and all its dependencies to `/tmp`:
        cp target/*.jar /tmp/
1. Add this at the bottom of `$JBOSS_HOME/bin/standalone.conf`:
        JAVA_OPTS="$JAVA_OPTS -javaagent:/tmp/modjprof-agent.jar -Xbootclasspath/p:/tmp/modjprof-agent.jar -Djboss.modules.system.pkgs=ch.puzzle.modjprof"
