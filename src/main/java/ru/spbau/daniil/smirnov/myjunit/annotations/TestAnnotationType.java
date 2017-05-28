package ru.spbau.daniil.smirnov.myjunit.annotations;

import java.lang.annotation.Annotation;

/**
 * Annotation types enumerator
 */
public enum TestAnnotationType {
    TEST(Test.class),
    BEFORE(Before.class),
    AFTER(After.class),
    BEFORE_CLASS(BeforeClass.class),
    AFTER_CLASS(AfterClass.class);

    private final Class<? extends Annotation> associatedClass;

    TestAnnotationType(Class<? extends Annotation> associatedClass) {
        this.associatedClass = associatedClass;
    }

    /**
     * Gets {@link Class} of annotation it is associated with}
     * @return associated annotation class
     */
    public Class<? extends Annotation> getAssociatedClass() {
        return associatedClass;
    }
}
