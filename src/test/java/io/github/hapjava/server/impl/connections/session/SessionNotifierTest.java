package io.github.hapjava.server.impl.connections.session;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SessionNotifierTest {

    private final SessionNotifierImpl notifier = new SessionNotifierImpl();
    private final SessionNotifierImpl.SessionNotificationListener listener = mock(SessionNotifierImpl.SessionNotificationListener.class);

    @Test
    public void notifiesOnRegistrationStateChange() {
        notifier.addListener(listener);

        notifier.userRegistered("user");
        verify(listener).countUpdated(1, 0, 0);

        notifier.userRemoved("user");
        verify(listener).countUpdated(0, 0, 0);
    }

}