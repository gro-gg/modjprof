/**
 * This package contains all classes of the java agent.
 * All classes in this package are loaded by the SystemClassLoader and therefore they must not reference classes in
 * the package {@link ch.puzzle.modjprof.instrumentation} which are loaded by the
 * {@link ch.puzzle.modjprof.classloader.AgentClassLoader}.
 */
package ch.puzzle.modjprof.agent;