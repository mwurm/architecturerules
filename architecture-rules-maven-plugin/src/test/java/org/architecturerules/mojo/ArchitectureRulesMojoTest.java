package org.architecturerules.mojo;


import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import org.architecturerules.api.configuration.ConfigurationFactory;


public class ArchitectureRulesMojoTest extends AbstractMojoTestCase {

    public void testAssert()
            throws Exception {

        String pom = "src/test/resources/simple/pom.xml";

        ArchitectureRulesMojo mojo = (ArchitectureRulesMojo) lookupMojo("assert", pom);

        assertNotNull(mojo);

        assertEquals(ConfigurationFactory.DEFAULT_CONFIGURATION_FILE_NAME, mojo.getConfigurationFileName());
    }
}
