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
package org.architecturerules.exceptions;


import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashSet;

import org.architecturerules.domain.SourceDirectory;


/**
 * <code>SourceNotFoundException Tester.</code>
 *
 * @author mikenereson
 */
public class SourceNotFoundExceptionTest extends TestCase {

    public SourceNotFoundExceptionTest(final String name) {
        super(name);
    }

    public void testInheritance() {

        assertTrue(ArchitectureException.class.isAssignableFrom(SourceNotFoundException.class));
    }


    @SuppressWarnings({"ThrowableInstanceNeverThrown"
    })
    public void testInterestingConstructors() {

        final SourceNotFoundException exception;
        final String message;
        final Throwable cause;

        final Collection sourceDirectories = new HashSet();
        sourceDirectories.add(new SourceDirectory("core/target/classes"));
        sourceDirectories.add(new SourceDirectory("util/target/classes"));
        sourceDirectories.add(new SourceDirectory("parent-pom/target/classes"));
        sourceDirectories.add(new SourceDirectory("web/target/classes"));

        exception = new SourceNotFoundException(sourceDirectories);
        message = exception.getMessage();
        cause = exception.getCause();

        assertTrue(message.indexOf("core/target/classes") > -1);
        assertTrue(message.indexOf("util/target/classes") > -1);
        assertTrue(message.indexOf("parent-pom/target/classes") > -1);
        assertTrue(message.indexOf("web/target/classes") > -1);

        assertEquals(null, cause);
    }


    @SuppressWarnings({"ThrowableInstanceNeverThrown",
        "ThrowableInstanceNeverThrown",
        "ThrowableInstanceNeverThrown",
        "ThrowableInstanceNeverThrown",
        "ThrowableInstanceNeverThrown",
        "ThrowableInstanceNeverThrown"
    })
    public void testTypicalConstructors() {

        SourceNotFoundException exception;
        String message;
        Throwable cause;

        exception = new SourceNotFoundException();
        message = exception.getMessage();
        cause = exception.getCause();

        assertTrue(message.equals("sources not found"));
        assertEquals(null, cause);

        exception = new SourceNotFoundException("no source classes found");
        message = exception.getMessage();
        cause = exception.getCause();

        assertTrue(message.equals("no source classes found"));
        assertEquals(null, cause);

        exception = new SourceNotFoundException(new IllegalArgumentException());
        message = exception.getMessage();
        cause = exception.getCause();

        assertTrue(message.equals("sources not found"));
        assertTrue(cause instanceof IllegalArgumentException);

        exception = new SourceNotFoundException("no source classes found", new IllegalArgumentException());
        message = exception.getMessage();
        cause = exception.getCause();

        assertTrue(message.equals("no source classes found"));
        assertTrue(cause instanceof IllegalArgumentException);
    }
}
