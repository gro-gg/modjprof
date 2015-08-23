package ch.puzzle.modjprof;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Properties;

import org.junit.Test;

public class PropertiesReaderTest {

    @Test
    public void shouldReadPropertiesFile() {
        //given
        String propertiesFile = "target/modjprof.properties";

        //when
        Properties properties = PropertiesReader.readPropertiesFile(propertiesFile);

        //then
        assertThat("Properties file not found!", properties.containsKey("isProfilerEnabled"));
    }

    @Test
    public void shouldNotFindFile() {
        //given
        String propertiesFile = "/foo/bar";

        //when
        AgentProperties properties = PropertiesReader.readPropertiesFile(propertiesFile);

        //then
        assertThat(properties, is(new AgentProperties()));
    }

}
