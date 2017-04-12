package mygit;

import org.apache.logging.log4j.Logger;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class TestWithMockedInternalUpdater {
    InternalUpdater updater;

    @Before
    public void initialise() throws Exception {
        updater = mock(InternalUpdater.class);
        final Logger fakeLogger = mock(Logger.class);
        when(updater.getLogger()).thenReturn(fakeLogger);
    }
}
