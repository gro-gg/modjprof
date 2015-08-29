package ch.puzzle.modjprof;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.Set;

import org.junit.Test;

import ch.puzzle.modjprof.config.AgentProperties;

public class AgentPropertiesTest {

    private static final String ARGUMENTS = "enable=true\nconfig=/tmp/modjprof.properties";

    @Test
    public void shouldReturnPropertyAsSet() throws Exception {
        //given
        String key = "set";
        AgentProperties properties = AgentProperties.parsePropertiesString(key + "=a,b,c");

        //when
        Set<String> set = properties.getPropertyAsSet(key);

        //then
        assertThat("Property 'enable' not found!", properties.containsKey(key));
        assertThat(set, contains("a", "b", "c"));
    }

    @Test
    public void shouldReturnEmptySet() throws Exception {
        //given
        String key = "set";
        AgentProperties properties = AgentProperties.parsePropertiesString("");

        //when
        Set<String> set = properties.getPropertyAsSet(key);

        //then
        assertThat(set, is(empty()));
    }


    @Test
    public void shouldGetEnableProperty() throws Exception {
        //given
        String key = "enable";

        //when
        AgentProperties properties = AgentProperties.parsePropertiesString(ARGUMENTS);

        //then
        assertThat("Property 'enable' not found!", properties.containsKey(key));
        assertThat(properties.getProperty(key), is("true"));
    }

    @Test
    public void shouldGetConfigLocation() throws Exception {
        //given
        String key = "config";

        //when
        AgentProperties properties = AgentProperties.parsePropertiesString(ARGUMENTS);

        //then
        assertThat("Property 'config' not found!", properties.containsKey(key));
        assertThat(properties.getProperty(key), is("/tmp/modjprof.properties"));
    }
}
