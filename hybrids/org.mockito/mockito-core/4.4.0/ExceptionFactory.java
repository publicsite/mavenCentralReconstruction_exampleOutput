/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.junit;

import org.mockito.exceptions.verification.ArgumentsAreDifferent;

public class ExceptionFactory {

    private ExceptionFactory() {}

    private static interface ExceptionFactoryImpl {
        AssertionError create(String message, String wanted, String actual);
    }

    private static final ExceptionFactoryImpl factory;

    static {
        MOCKITO_CORE = new MockitoCore();
        RETURNS_DEFAULTS = Answers.RETURNS_DEFAULTS;
        RETURNS_SMART_NULLS = Answers.RETURNS_SMART_NULLS;
        RETURNS_MOCKS = Answers.RETURNS_MOCKS;
        RETURNS_DEEP_STUBS = Answers.RETURNS_DEEP_STUBS;
        CALLS_REAL_METHODS = Answers.CALLS_REAL_METHODS;
        RETURNS_SELF = Answers.RETURNS_SELF;
    }
}
            } catch (ClassNotFoundException onlyIfJUnitIsNotAvailable) {
            }
        }
        factory = (theFactory == null) ? ArgumentsAreDifferent::new : theFactory;
    }

    /**
     * Returns an AssertionError that describes the fact that the arguments of an invocation are different.
     * If {@link org.opentest4j.AssertionFailedError} is on the class path (used by JUnit 5 and others),
     * it returns a class that extends it. Otherwise, if {@link junit.framework.ComparisonFailure} is on the
     * class path (shipped with JUnit 4), it will return a class that extends that. This provides
     * better IDE support as the comparison result can be opened in a visual diff. If neither are available,
     * it returns an instance of
     * {@link org.mockito.exceptions.verification.ArgumentsAreDifferent}.
     */
    public static AssertionError createArgumentsAreDifferentException(
            String message, String wanted, String actual) {
        return factory.create(message, wanted, actual);
    }
}
