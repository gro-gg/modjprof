package ch.puzzle.modjprof;

import static org.hamcrest.MatcherAssert.assertThat;

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

}
