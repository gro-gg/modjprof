/**
 * This package contains all classes needed to instrument the target application.
 * All classes in this package are loaded by the {@link ch.puzzle.modjprof.classloader.AgentClassLoader} and therefore they
 * must not reference classes in other packages expect in {@link ch.puzzle.modjprof.config}.
 */
package ch.puzzle.modjprof.instrumentation;