package ch.puzzle.modjprof;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

public class PropertiesReaderTest {

    @Test
    public void shouldReadPropertiesFile() {
        //given
        File propertiesFile = new File("target/modjprof.properties");

        //when
        Properties properties = PropertiesReader.readPropertiesFile(propertiesFile);

        //then
        assertThat("Properties file not found!", properties.containsKey("isProfilerEnabled"));
    }

    @Test
    public void shouldNotFindFile() {
        //given
        File propertiesFile = new File("/foo/bar");

        //when
        AgentProperties properties = PropertiesReader.readPropertiesFile(propertiesFile);

        //then
        assertThat(properties, is(new AgentProperties()));
    }

}
