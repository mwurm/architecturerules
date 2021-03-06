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
package test.com.seventytwomiles.services;


import test.com.seventytwomiles.model.Person;

import java.util.Collection;


/**
 * <p>Test service interface.</p>
 *
 * @author mikenereson
 */
public interface PersonService {

    void createPerson(final Person person);


    Collection loadPersons();
}
