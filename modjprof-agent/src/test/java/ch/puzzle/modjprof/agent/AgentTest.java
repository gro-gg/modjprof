/**
 * Copyright 2015 The modjprof Project Developers. See the COPYRIGHT file at the top-level directory of this distribution and at
 * https://github.com/gro-gg/modjprof/blob/master/COPYRIGHT.
 *
 * This file is part of modjprof Project. It is subject to the license terms in the LICENSE file found in the top-level directory
 * of this distribution and at https://github.com/gro-gg/modjprof/blob/master/LICENSE. No part of modjprof Project, including this
 * file, may be copied, modified, propagated, or distributed except according to the terms contained in the LICENSE file.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See LICENSE file for more details.
 */
package ch.puzzle.modjprof.agent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AgentTest {

    private static final String ARGUMENTS = "enable=true\nconfig=/tmp/modjprof.properties";
    private static final String AGENTLOCATION = "/home/user/modjprof/modjprof-agent/target/modjprof-agent.jar";

    @Spy
    Agent agent = new Agent();

    @Test
    public void shouldGetJavaagentWithArguments() throws Exception {
        List<String> ret = buildVmArguments(AGENTLOCATION, ARGUMENTS);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        String javaagent = agent.getJavaagentWithArgumentsFromVmArguments();

        //then
        assertThat(javaagent, is(AGENTLOCATION + "=" + ARGUMENTS));
    }

    @Test
    public void shouldGetJavaagentUrlFromVmArguments() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        URL agentUrl = agent.getJavaagentUrlFromVmArguments();

        //then
        assertThat(agentUrl, Matchers.is(new URL("file:" + AGENTLOCATION)));
    }

    @Test
    public void shouldGetJavaagentUrlFromVmArgumentsWithAgentArguments() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION, ARGUMENTS);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        URL agentUrl = agent.getJavaagentUrlFromVmArguments();

        //then
        assertThat(agentUrl, is(new URL("file:" + AGENTLOCATION)));
    }

    @Test
    public void shouldGetNoJavaagentUrlFromVmArguments() throws Exception {
        //given
        List<String> ret = buildVmArguments(null);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        URL agentUrl = agent.getJavaagentUrlFromVmArguments();

        //then
        assertThat(agentUrl, is(nullValue()));
    }

    @Test
    public void shouldGetJavaagentArguments() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION, ARGUMENTS);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        String arguments = agent.getJavaagentArguments();

        //then
        assertThat(arguments, is(ARGUMENTS));
    }

    @Test
    public void shouldGetNoJavaagentArguments() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        String arguments = agent.getJavaagentArguments();

        //then
        assertThat(arguments, is(nullValue()));
    }

    @Test
    public void shouldGetConfigLocation() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION, ARGUMENTS);
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        File location = agent.getConfigLocation();

        //then
        assertThat(location, is(new File("/tmp/modjprof.properties")));
    }

    @Test
    public void shouldNotGetConfigLocationWhenLocationNotSet() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION, "enable=true");
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        File location = agent.getConfigLocation();

        //then
        assertThat(location, is(nullValue()));
    }

    @Test
    public void shouldNotGetConfigLocationWhenLocationNotExists() throws Exception {
        //given
        List<String> ret = buildVmArguments(AGENTLOCATION, "config=/foo/bar");
        when(agent.getVmArguments()).thenReturn(ret);

        //when
        File location = agent.getConfigLocation();

        //then
        assertThat(location, is(nullValue()));
    }

    private List<String> buildVmArguments(String agentLocation) {
        return buildVmArguments(agentLocation, null);
    }

    private List<String> buildVmArguments(String agentLocation, String agentArguments) {
        List<String> ret = new ArrayList<String>();
        ret.add("-Dfile.encoding=UTF-8");
        ret.add("-Dch.foo=ch.bar");
        if (agentLocation != null) {
            if (agentArguments != null) {
                agentLocation += "=" + agentArguments;
            }
            ret.add("-javaagent:" + agentLocation);
        }
        ret.add("-verbose:class");
        return ret;
    }
}
