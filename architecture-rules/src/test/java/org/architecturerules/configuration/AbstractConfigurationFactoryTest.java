/**
 * Copyright 2007 - 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * For more information visit
 *         http://wiki.architecturerules.org/ and
 *         http://blog.architecturerules.org/
 */
package org.architecturerules.configuration;


import junit.framework.TestCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.architecturerules.configuration.AbstractConfigurationFactory;

import org.springframework.core.io.ClassPathResource;


/**
 * AbstractConfigurationFactory Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>02/27/2008</pre>
 */
public class AbstractConfigurationFactoryTest extends TestCase {

    private AbstractConfigurationFactory factory;

    public AbstractConfigurationFactoryTest(final String name) {
        super(name);
    }

    @Override
    public void setUp()
            throws Exception {

        super.setUp();

        factory = new AbstractConfigurationFactory() {

                    @Override
                    protected void validateConfiguration(final String configuration) {

                        /* do nothing*/
                    }


                    @Override
                    protected void processConfiguration(String configuration) {

                        /* do nothing*/
                    }


                    @Override
                    protected Set<String> getSupportedFileTypes() {

                        // TODO Auto-generated method stub
                        return null;
                    }
                };
    }


    @Override
    public void tearDown()
            throws Exception {

        factory = null;

        super.tearDown();
    }


    public void testGetConfigurationAsXmlFromClassPathResource() {

        try {

            factory.loadConfiguration("some-file-that-does-not-exist.xml");
            fail("expected IllegalArgumentException because input file does not exist");
        } catch (IllegalArgumentException e) {

            /* success */
        }
    }


    public void testGetConfigurationAsXmlFromNotExistedAbsolutePath()
            throws FileNotFoundException {

        try {

            final File file = new File("some-file-that-does-not-exist.xml");
            final String filepath = file.getAbsolutePath();
            factory.loadConfiguration(filepath);
        } catch (IllegalArgumentException e) {

            /* success */
        }
    }


    public void testGetConfigurationAsXmlFromAbsolutePath()
            throws IOException {

        final ClassLoader classLoader = getClass().getClassLoader();

        final ClassPathResource resource = new ClassPathResource("architecture-rules.xml", classLoader);

        // TODO find other ways to compute absolute path to configuration file
        final String filepath = resource.getFile().getAbsolutePath();
        factory.loadConfiguration(filepath);
    }
}
