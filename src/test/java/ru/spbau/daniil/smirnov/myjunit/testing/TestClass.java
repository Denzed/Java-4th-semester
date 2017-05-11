package ru.spbau.daniil.smirnov.myjunit.testing;

import ru.spbau.daniil.smirnov.myjunit.annotations.*;

import java.io.IOException;

/**
 * Class with tests
 */
public class TestClass {
    @Test
    static public void staticTest() {

    }

    @BeforeClass
    public void testBeforeClass() {

    }

    @Before
    public void testBefore() {

    }

    @Test(expected = IOException.class)
    public void testExpectedException() throws IOException {
        throw new IOException();
    }

    @Test
    public void testUnexpectedException() throws IOException {
        throw new IOException();
    }

    @Test
    public void normalTest() {

    }

    @Test
    private void privateTest() {

    }

    @Test
    public void testRequiresArguments(int kek) {

    }

    @Test(ignore = "because")
    public void ignoredTest() {

    }

    @After
    public void testAfter() {

    }

    @AfterClass
    public void testAfterClass() {

    }
}
