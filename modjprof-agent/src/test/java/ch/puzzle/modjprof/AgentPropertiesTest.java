package ch.puzzle.modjprof;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class AgentPropertiesTest {

    private static final String ARGUMENTS = "enable=true\nconfig=/tmp/modjprof.properties";

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
