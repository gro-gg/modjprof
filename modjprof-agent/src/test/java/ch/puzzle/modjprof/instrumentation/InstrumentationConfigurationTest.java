package ch.puzzle.modjprof.instrumentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.Set;

import org.junit.Test;

public class InstrumentationConfigurationTest {

    @Test
    public void shouldInitialize() throws Exception {
        //given
        InstrumentationConfiguration instance = InstrumentationConfiguration.getInstance();

        //when
        instance.initialize(new File("target/modjprof.properties"));
        Set<String> packagesToInstrument = instance.getPackagesToInstrument();

        //then
        assertThat(packagesToInstrument, hasItems("ch.puzzle", "org.jboss.as.quickstarts.greeter.web"));
        assertThat(packagesToInstrument.size(), is(2));
    }

}
