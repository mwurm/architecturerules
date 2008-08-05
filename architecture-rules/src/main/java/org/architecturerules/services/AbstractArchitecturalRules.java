/**
 * Copyright 2007, 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * For more information visit
 *         http://72miles.com and
 *         http://architecturerules.googlecode.com
 */
package org.architecturerules.services;


import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.architecturerules.configuration.Configuration;
import org.architecturerules.domain.JPackage;
import org.architecturerules.domain.SourceDirectory;
import org.architecturerules.exceptions.CyclicRedundancyException;
import org.architecturerules.exceptions.DependencyConstraintException;
import org.architecturerules.exceptions.NoPackagesFoundException;
import org.architecturerules.exceptions.SourceNotFoundException;


/**
 * <p>Provides support for any type of rule that can be asserted using the jdepend library</p>
 *
 * @author mikenereson
 * @see RulesServiceImpl
 * @see CyclicRedundancyServiceImpl
 */
abstract class AbstractArchitecturalRules {

    /**
     * <p>log to debug with.</p>
     *
     * @parameter log Log
     */
    private static final Log log = LogFactory.getLog(AbstractArchitecturalRules.class);

    /**
     * <p>The <code>Configuration</code> to test against.</p>
     *
     * @parameter configuration Configuration
     */
    protected final Configuration configuration;

    /**
     * <p>Instance of jdepend to assert architecture with</p>
     *
     * @parameter jdepend JDepend
     */
    private final JDepend jdepend;

    /**
     * <p>All packages that will be analyze</p>
     *
     * @parameter packages Collection
     */
    private Collection<JavaPackage> packages;

    /**
     * <p>Constructor that loads up the configuration and loads all the packages in the source paths</p>
     *
     * @param configuration Configuration
     * @throws SourceNotFoundException when an required source directory does not exist and when
     * <tt>exception</tt>=<tt>"true"</tt> in the source configuration
     * @throws NoPackagesFoundException when none of the source directories exist and
     * <tt>no-packages</tt>="<tt>ignore</tt>" in the sources configuration
     */
    AbstractArchitecturalRules(final Configuration configuration)
            throws SourceNotFoundException, NoPackagesFoundException {

        log.info("instantiating new AbstractArchitecturalRules");

        this.configuration = configuration;

        /* instantiate JDepend */
        jdepend = new JDepend();

        /* read source paths from configuration file */
        final Collection<SourceDirectory> sources = this.configuration.getSources();

        /* add each source to jdepend */
        for (final SourceDirectory source : sources) {

            addSourceToJdepend(source);
        }

        analyze();
    }

    /**
     * <p>Add a sourceDirectory path to JDepend instance. Throws Exception when sourceDirectory path can not be found,
     * if a given <code>SourceDirectory</code> is configured to do so.</p>
     *
     * @param sourceDirectory a <code>SourceDirectory</code> to read path from
     */
    private void addSourceToJdepend(final SourceDirectory sourceDirectory) {

        final String sourcePath = sourceDirectory.getPath();
        final StringBuffer message = new StringBuffer();

        final boolean throwExceptionWhenNotFound = sourceDirectory.shouldThrowExceptionWhenNotFound();

        try {

            jdepend.addDirectory(sourcePath);

            if (log.isDebugEnabled()) {

                message.append("loaded ");
                message.append(throwExceptionWhenNotFound ? "required " : "");
                message.append("sourceDirectory ");
                message.append(new File("").getAbsolutePath());
                message.append("\\");
                message.append(sourcePath);

                log.debug(message.toString());
            }
        } catch (final IOException e) {

            /* sourceDirectory not found */
            if (log.isWarnEnabled()) {

                message.append(throwExceptionWhenNotFound ? "required " : "");
                message.append("sourceDirectory ");
                message.append(new File("").getAbsolutePath());
                message.append("\\");
                message.append(sourcePath);
                message.append(" does not exist");

                log.warn(message.toString());
            }

            if (sourceDirectory.shouldThrowExceptionWhenNotFound()) {

                log.error(sourcePath + " was not found", e);

                throw new SourceNotFoundException(sourcePath + " was not found", e);
            }
        }
    }


    /**
     * <p>Analyze with JDepend. Call after JDepend knows about all of the source paths.</p>
     */
    private void analyze() {

        /**
         * Ask jdepend to analyze each package in each of the source
         * directories that were added above.
         */
        log.debug("fetching packages");
        packages = jdepend.analyze();

        log.debug("checking how many packages were found by JDepend");

        if (packages.isEmpty()) {

            log.warn("no packages were found with the given configuration. " + "check your <sources /> configuration");

            final boolean isConfiguredToThrowExceptionWhenNoPackagesFound = configuration.shouldThrowExceptionWhenNoPackages();

            log.debug("throw exception when no packages? " + isConfiguredToThrowExceptionWhenNoPackagesFound);

            if (isConfiguredToThrowExceptionWhenNoPackagesFound) {

                log.debug("throwing RuntimeException because no packages " + "were found");

                throw new NoPackagesFoundException("no packages were found " + "with the given configuration. check your <sources /> " + "configuration");
            }
        } else {

            log.debug("jdepend found " + packages.size() + " to analyze for cyclic redundancies");
        }
    }


    /**
     * <p>All of the packages that were, or will be analyzed by the JDepend instance, given the source paths that it
     * knows about.</p>
     *
     * @return Collection
     */
    Collection<JavaPackage> getPackages() {

        return this.packages;
    }


    /**
     * <p>Test the given layer (a package, but package is java keyword) against the given Rules</p>
     *
     * @param layer String the package to test
     * @param violations Collection of packages defining which packages the layer may not use
     * @throws DependencyConstraintException when a rule is broken
     * @throws CyclicRedundancyException when cyclic redundancy is found
     */
    void testLayeringValid(final JPackage layer, final Collection<JPackage> violations)
            throws DependencyConstraintException, CyclicRedundancyException {

        final Collection<JavaPackage> analyzedPackages = jdepend.analyze();

        log.debug("checking how many packages were found by JDepend");

        if (analyzedPackages.isEmpty()) {

            log.warn("no packages were found with the given configuration. " + "check your <sources />");

            final boolean isConfiguredToThrowExceptionWhenNoPackagesFound = this.configuration.shouldThrowExceptionWhenNoPackages();

            log.debug("throw exception when no packages? " + isConfiguredToThrowExceptionWhenNoPackagesFound);

            if (isConfiguredToThrowExceptionWhenNoPackagesFound) {

                log.debug("throwing CyclicRedundancyException");

                final String message = "cyclic redundancy does exist";
                throw new CyclicRedundancyException(message);
            }
        } else {

            log.debug("jdepend found " + analyzedPackages.size() + " to analyze for dependency architecture");
        }

        for (final JavaPackage analyzedPackage : analyzedPackages) {

            final JPackage javaPackage = new JPackage(analyzedPackage.getName());

            log.debug("checking dependencies on package " + javaPackage);

            if (layer.matches(javaPackage)) {

                testEfferentsValid(violations, analyzedPackage);
            }
        }
    }


    /**
     * <p>Test a given layer (java package) against a Collection of <code>Rules</code></p>
     *
     * @param violations Collection of rules defining which packages the given package may not depend upon
     * @param jPackage JavaPackage
     * @throws DependencyConstraintException when a rule is broken
     */
    private void testEfferentsValid(final Collection<JPackage> violations, final JavaPackage jPackage)
            throws DependencyConstraintException {

        final Collection<JavaPackage> efferents = jPackage.getEfferents();

        for (final JavaPackage efferent : efferents) {

            final String analyzedPackageName = jPackage.getName();
            final JPackage efferentJPackage = new JPackage(efferent.getName());

            for (final JPackage violation : violations) {

                if (violation.matches(efferentJPackage)) {

                    final String message = analyzedPackageName + " is not allowed to depend upon " + efferent.getName();

                    log.error(message);

                    throw new DependencyConstraintException(message);
                }
            }
        }
    }
}