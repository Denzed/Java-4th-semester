package ru.spbau.daniil.smirnov.myjunit.testing;

/**
 *
 */
public class TestClassWithoutPublicNullaryConstructor {
    private int id;

    private TestClassWithoutPublicNullaryConstructor() {
        id = -1;
    }

    public TestClassWithoutPublicNullaryConstructor(int id) {
        this.id = 1;
    }
}
